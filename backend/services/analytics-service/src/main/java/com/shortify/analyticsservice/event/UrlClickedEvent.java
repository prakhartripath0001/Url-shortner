package com.shortify.analyticsservice.event;

import java.time.Instant;

public record UrlClickedEvent(
        String shortCode,
        String originalUrl,
        Long urlId,
        Long userId,
        String ipAddress,
        String userAgent,
        String referer,
        String country,
        String city,
        String deviceType,
        Instant clickedAt
) {}
