package com.shortify.urlservice.controller;

import com.shortify.urlservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Redirect controller — the HOT PATH of the entire system.
 *
 * CRITICAL PERFORMANCE REQUIREMENTS:
 * - P99 latency: < 50ms
 * - This endpoint handles 10x more traffic than all other endpoints combined.
 *
 * DESIGN DECISIONS:
 * 1. NO authentication filter on this path (public redirect).
 * 2. Cache-Aside pattern via @Cacheable in UrlService (Redis hit = ~2ms).
 * 3. Analytics published AFTER sending the redirect response (non-blocking).
 *    The redirect is sent first, analytics is fire-and-forget.
 * 4. Uses HTTP 302 (not 301) to prevent browser caching — we need click data.
 *
 * MAPPED AT ROOT LEVEL (not /api/v1/) to produce clean URLs:
 * - https://short.ly/abc1234 → redirects to original URL
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Redirect", description = "Public URL redirect endpoint")
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to original URL (public, no auth required)")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request
    ) {
        // 1. Resolve URL (Cache-Aside: Redis first, then DB)
        String originalUrl = urlService.resolveUrl(shortCode);

        // 2. Fire analytics event asynchronously (non-blocking)
        // This runs AFTER we've already fetched the URL, so it doesn't block the redirect
        urlService.recordClick(
                shortCode,
                extractIpAddress(request),
                request.getHeader(HttpHeaders.USER_AGENT),
                request.getHeader("Referer")
        );

        // 3. Return 302 redirect
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
