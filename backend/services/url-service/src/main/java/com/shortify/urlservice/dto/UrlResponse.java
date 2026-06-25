package com.shortify.urlservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shortify.urlservice.entity.Url;
import lombok.Builder;

import java.time.Instant;

/**
 * Response DTO for URL operations.
 * Uses @JsonInclude(NON_NULL) so null fields (like expiresAt) are omitted from JSON.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UrlResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String originalUrl,
        String title,
        Long visitCount,
        boolean isPrivate,
        Instant expiresAt,
        Instant createdAt
) {
    public static UrlResponse from(Url url, String baseUrl) {
        return UrlResponse.builder()
                .id(url.getId())
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .title(url.getTitle())
                .visitCount(url.getVisitCount())
                .isPrivate(url.isPrivate())
                .expiresAt(url.getExpiresAt())
                .createdAt(url.getCreatedAt())
                .build();
    }
}
