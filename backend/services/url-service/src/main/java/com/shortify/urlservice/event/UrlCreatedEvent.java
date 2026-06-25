package com.shortify.urlservice.event;

import java.time.Instant;

/**
 * Kafka event published when a new short URL is created.
 * Consumed by:
 * - Notification Service: sends confirmation email to user
 * - Analytics Service: initialises analytics record for this URL
 */
public record UrlCreatedEvent(
        Long urlId,
        String shortCode,
        String shortUrl,
        String originalUrl,
        Long userId,
        String userEmail,
        Instant createdAt,
        Instant expiresAt
) {}
