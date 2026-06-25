package com.shortify.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Stores one record per URL click event.
 *
 * DESIGN DECISIONS:
 * - Immutable record — clicks are NEVER updated, only inserted.
 * - url_id and user_id are plain BIGINTs — no FK to url-service DB (decoupled).
 * - Partitioned by clicked_at in production (monthly range partitioning).
 *   This keeps query performance stable as billions of rows accumulate.
 *
 * STORAGE ESTIMATE:
 * ~200 bytes per record × 10M clicks/day × 5 years = 3.65 TB
 * → Consider TimescaleDB or ClickHouse for analytics at this scale.
 */
@Entity
@Table(
    name = "click_events",
    indexes = {
        @Index(name = "idx_click_url_id",     columnList = "urlId"),
        @Index(name = "idx_click_user_id",    columnList = "userId"),
        @Index(name = "idx_click_clicked_at", columnList = "clickedAt"),
        @Index(name = "idx_click_short_code", columnList = "shortCode")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12)
    private String shortCode;

    @Column(nullable = false)
    private Long urlId;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 45)  // IPv6 max length
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String referer;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String deviceType; // MOBILE, DESKTOP, TABLET, BOT, UNKNOWN

    @Column(nullable = false)
    private Instant clickedAt;
}
