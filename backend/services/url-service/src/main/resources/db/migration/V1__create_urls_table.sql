-- V1__create_urls_table.sql
-- Initial schema for the URL Service (MySQL version)

CREATE TABLE IF NOT EXISTS urls (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code   VARCHAR(12)  NOT NULL,
    original_url TEXT         NOT NULL,
    custom_alias VARCHAR(100),
    user_id      BIGINT       NOT NULL,
    title        VARCHAR(500),
    expires_at   TIMESTAMP    NULL,
    visit_count  BIGINT       NOT NULL DEFAULT 0,
    is_private   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_urls_short_code UNIQUE (short_code)
);

-- Primary lookup index: redirect hot path (shortCode -> originalUrl)
CREATE UNIQUE INDEX idx_urls_short_code ON urls (short_code);

-- User dashboard queries: list all URLs for a user, ordered by creation date
CREATE INDEX idx_urls_user_id_active ON urls (user_id, created_at DESC);

-- Expiry cleanup job: find expired active URLs
CREATE INDEX idx_urls_expires_at ON urls (expires_at);
