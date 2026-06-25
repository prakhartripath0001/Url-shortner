package com.shortify.urlservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Core entity representing a shortened URL.
 *
 * Design decisions:
 * - shortCode is indexed for O(log n) lookups on redirect path (hot path)
 * - userId stored as Long (not a FK to auth-service) — services are decoupled
 * - visitCount is a denormalised counter for fast dashboard reads
 * - expiresAt is nullable — null means never expires
 * - isActive flag allows soft-delete without losing analytics history
 */
@Entity
@Table(
    name = "urls",
    indexes = {
        @Index(name = "idx_urls_short_code", columnList = "shortCode", unique = true),
        @Index(name = "idx_urls_user_id",    columnList = "userId"),
        @Index(name = "idx_urls_expires_at", columnList = "expiresAt")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 7-character Base62 encoded short code (e.g. "aB3xY2z").
     * This is the primary lookup key on the redirect hot path.
     */
    @Column(nullable = false, unique = true, length = 12)
    private String shortCode;

    /**
     * Original long URL supplied by the user.
     * Using TEXT type to support arbitrarily long URLs (some can be 8000+ chars).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    /**
     * Optional human-readable custom alias (e.g. "my-product-launch").
     * Custom aliases are stored as the shortCode when provided.
     */
    @Column(length = 100)
    private String customAlias;

    /**
     * User ID from auth-service. Stored as plain Long to keep services decoupled.
     * We never join across service databases.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Title scraped or user-provided for dashboard display.
     */
    @Column(length = 500)
    private String title;

    /**
     * URL expiration timestamp. Null = never expires.
     */
    @Column
    private Instant expiresAt;

    /**
     * Denormalised click counter. Updated asynchronously via Kafka.
     * Avoids a COUNT(*) on the analytics table for every dashboard load.
     */
    @Column(nullable = false)
    @Builder.Default
    private Long visitCount = 0L;

    /**
     * false = link is publicly accessible
     * true  = link requires auth to redirect (private links)
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isPrivate = false;

    /**
     * Soft delete — preserved for analytics history.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
