package com.shortify.urlservice.service;

import com.shortify.urlservice.dto.CreateUrlRequest;
import com.shortify.urlservice.dto.UrlResponse;
import com.shortify.urlservice.entity.Url;
import com.shortify.urlservice.event.UrlClickedEvent;
import com.shortify.urlservice.event.UrlCreatedEvent;
import com.shortify.urlservice.exception.AliasAlreadyTakenException;
import com.shortify.urlservice.exception.UrlExpiredException;
import com.shortify.urlservice.exception.UrlNotFoundException;
import com.shortify.urlservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Core URL service — handles creation, resolution, and management of short URLs.
 *
 * DESIGN PATTERNS USED:
 * 1. Cache-Aside Pattern: Read from cache first → miss → read DB → populate cache.
 *    Spring's @Cacheable annotation implements this automatically.
 *
 * 2. Event-Driven Architecture: After creating/clicking a URL, publish Kafka events.
 *    The service does NOT call analytics or notification services directly.
 *    This ensures loose coupling and resilience.
 *
 * 3. Soft Delete: isActive=false instead of physical deletion — preserves analytics.
 *
 * TRANSACTIONAL BOUNDARIES:
 * - createUrl: @Transactional — DB write + event publish must succeed together.
 *   Note: Kafka publish is NOT part of the DB transaction (2-phase commit problem).
 *   In production, use Transactional Outbox Pattern to guarantee delivery.
 * - resolveUrl: No @Transactional needed — cache hit avoids DB entirely.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.kafka.topics.url-clicked}")
    private String urlClickedTopic;

    @Value("${app.kafka.topics.url-created}")
    private String urlCreatedTopic;

    // ─── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public UrlResponse createUrl(CreateUrlRequest request, Long userId, String userEmail) {
        String shortCode;

        if (request.customAlias() != null && !request.customAlias().isBlank()) {
            // Custom alias: validate uniqueness
            if (urlRepository.existsByShortCode(request.customAlias())) {
                throw new AliasAlreadyTakenException(
                    "Custom alias '" + request.customAlias() + "' is already taken"
                );
            }
            shortCode = request.customAlias();
        } else {
            // Generate unique Base62 short code
            shortCode = shortCodeGenerator.generateUnique(urlRepository::existsByShortCode);
        }

        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(request.originalUrl())
                .customAlias(request.customAlias())
                .title(request.title())
                .userId(userId)
                .expiresAt(request.expiresAt())
                .isPrivate(request.isPrivate())
                .build();

        url = urlRepository.save(url);
        log.info("Created short URL: {} → {}", shortCode, request.originalUrl());

        // Publish event asynchronously (fire-and-forget)
        // In production: use Transactional Outbox Pattern for guaranteed delivery
        kafkaTemplate.send(urlCreatedTopic, shortCode, new UrlCreatedEvent(
                url.getId(), shortCode, baseUrl + "/" + shortCode,
                url.getOriginalUrl(), userId, userEmail,
                url.getCreatedAt(), url.getExpiresAt()
        ));

        return UrlResponse.from(url, baseUrl);
    }

    // ─── RESOLVE (Redirect Hot Path) ───────────────────────────────────────────

    /**
     * Resolves a short code to its original URL.
     *
     * Cache-Aside: @Cacheable checks Redis first.
     * - HIT:  Returns cached URL (~2ms, no DB call)
     * - MISS: Queries DB, caches result, returns URL (~15ms)
     *
     * WHY key="#shortCode"?
     * Each short code maps to exactly one URL. The cache key IS the short code.
     * The cache entry has a TTL configured in RedisConfig.
     */
    @Cacheable(value = "urls", key = "#shortCode", unless = "#result == null")
    public String resolveUrl(String shortCode) {
        Url url = urlRepository.findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(Instant.now())) {
            throw new UrlExpiredException("This short URL has expired");
        }

        return url.getOriginalUrl();
    }

    /**
     * Records a click event asynchronously via Kafka.
     * Called AFTER redirect — non-blocking, does not affect redirect latency.
     */
    public void recordClick(String shortCode, String ipAddress, String userAgent, String referer) {
        // Increment counter in DB (fast UPDATE, no SELECT)
        urlRepository.incrementVisitCount(shortCode);

        // Publish detailed click event to Kafka for analytics processing
        kafkaTemplate.send(urlClickedTopic, shortCode, new UrlClickedEvent(
                shortCode, null, null, null,
                ipAddress, userAgent, referer,
                null, null, detectDeviceType(userAgent),
                Instant.now()
        ));
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public Page<UrlResponse> getUserUrls(Long userId, Pageable pageable) {
        return urlRepository
                .findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId, pageable)
                .map(url -> UrlResponse.from(url, baseUrl));
    }

    public UrlResponse getUrlDetails(String shortCode, Long requestingUserId) {
        Url url = urlRepository.findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        // Users can only view their own URL details
        if (!url.getUserId().equals(requestingUserId)) {
            throw new UrlNotFoundException("URL not found"); // Intentionally vague — no info leakage
        }

        return UrlResponse.from(url, baseUrl);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "urls", key = "#shortCode")
    public void deleteUrl(String shortCode, Long requestingUserId) {
        Url url = urlRepository.findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        if (!url.getUserId().equals(requestingUserId)) {
            throw new UrlNotFoundException("URL not found");
        }

        url.setActive(false);
        urlRepository.save(url);
        log.info("Soft-deleted URL: {} by user: {}", shortCode, requestingUserId);
    }

    // ─── SCHEDULED CLEANUP ─────────────────────────────────────────────────────

    /**
     * Scheduled job to soft-delete expired URLs.
     * Runs every hour. Uses pagination to avoid loading all expired URLs into memory.
     * In production: Move to a separate scheduled microservice or use Spring Batch.
     */
    @Transactional
    public void expireUrls() {
        Pageable pageable = Pageable.ofSize(500);
        Page<Url> expired = urlRepository.findExpiredUrls(Instant.now(), pageable);

        expired.forEach(url -> {
            url.setActive(false);
            log.info("Expiring URL: {}", url.getShortCode());
        });

        urlRepository.saveAll(expired.getContent());
        log.info("Expired {} URLs in this batch", expired.getNumberOfElements());
    }

    // ─── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";
        String ua = userAgent.toLowerCase();
        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) return "BOT";
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) return "MOBILE";
        if (ua.contains("tablet") || ua.contains("ipad")) return "TABLET";
        return "DESKTOP";
    }
}
