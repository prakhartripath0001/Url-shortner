package com.shortify.urlservice.scheduler;

import com.shortify.urlservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job for URL expiry cleanup.
 *
 * DESIGN NOTE:
 * In a production microservices environment, this should be extracted into
 * a separate Spring Batch job or a dedicated scheduler service. Keeping it
 * here for simplicity in Phase 1 development.
 *
 * WHY NOT RELY ON REDIS TTL ALONE?
 * - Redis is the cache layer. If an expired URL is not in cache, the request
 *   hits the DB. The DB must also reflect the expired state.
 * - This scheduler soft-deletes expired URLs in PostgreSQL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UrlExpiryScheduler {

    private final UrlService urlService;

    /**
     * Runs every hour at :00 minutes.
     * cron = "0 0 * * * *" — second(0) minute(0) hour(*) dayOfMonth(*) month(*) dayOfWeek(*)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void expireUrls() {
        log.info("Starting URL expiry cleanup job...");
        urlService.expireUrls();
        log.info("URL expiry cleanup job completed");
    }
}
