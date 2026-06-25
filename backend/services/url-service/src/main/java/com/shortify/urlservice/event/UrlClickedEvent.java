package com.shortify.urlservice.event;

import java.time.Instant;

/**
 * Kafka event published when a short URL is clicked.
 *
 * This event is consumed by the Analytics Service to:
 * 1. Store the click record in the analytics database
 * 2. Increment the URL visit counter (eventually)
 *
 * WHY A RECORD?
 * Java 21 records are immutable value objects — perfect for events.
 * Events should NEVER be mutated after creation.
 *
 * WHY KAFKA AND NOT A DIRECT DB WRITE?
 * - Writing to analytics DB synchronously on every redirect adds ~20-50ms latency.
 * - At 1,160 redirects/sec (peak), that's 1,160 synchronous DB writes/sec on the
 *   redirect path — this would kill performance.
 * - Publishing to Kafka takes ~2ms. Analytics processing happens asynchronously.
 * - This is the core principle of Event-Driven Architecture (EDA).
 */
public record UrlClickedEvent(
        String shortCode,
        String originalUrl,
        Long urlId,
        Long userId,
        String ipAddress,
        String userAgent,
        String referer,
        String country,    // Populated asynchronously via IP geolocation
        String city,
        String deviceType, // MOBILE, DESKTOP, TABLET, BOT
        Instant clickedAt
) {}
