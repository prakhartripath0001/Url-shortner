-- V1__create_urls_table.sql
-- Initial schema for the URL Service
--
-- DESIGN DECISIONS:
-- 1. short_code is the primary lookup key (indexed, unique, not null)
-- 2. original_url uses TEXT type — URLs can exceed VARCHAR(255) limit
-- 3. user_id is a plain BIGINT — no FK to auth-service (services are decoupled)
-- 4. visit_count is a denormalised counter — avoids COUNT(*) on analytics table
-- 5. expires_at is nullable — NULL means never expires
-- 6. is_active enables soft-delete — preserves analytics history

CREATE TABLE IF NOT EXISTS urls (
    id           BIGSERIAL PRIMARY KEY,
    short_code   VARCHAR(12)  NOT NULL,
    original_url TEXT         NOT NULL,
    custom_alias VARCHAR(100),
    user_id      BIGINT       NOT NULL,
    title        VARCHAR(500),
    expires_at   TIMESTAMPTZ,
    visit_count  BIGINT       NOT NULL DEFAULT 0,
    is_private   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_urls_short_code UNIQUE (short_code)
);

-- Primary lookup index: redirect hot path (shortCode → originalUrl)
-- This is the most critical index in the entire system.
-- B-Tree index: O(log n) lookup — handles billions of rows efficiently.
CREATE UNIQUE INDEX IF NOT EXISTS idx_urls_short_code
    ON urls (short_code);

-- User dashboard queries: list all URLs for a user, ordered by creation date
-- Partial index (WHERE is_active = TRUE) — excludes soft-deleted rows
-- Reduces index size significantly as deleted URLs accumulate
CREATE INDEX IF NOT EXISTS idx_urls_user_id_active
    ON urls (user_id, created_at DESC)
    WHERE is_active = TRUE;

-- Expiry cleanup job: find expired active URLs
-- Partial index — only indexes rows where expires_at is NOT NULL
-- Avoids indexing the majority of URLs that never expire
CREATE INDEX IF NOT EXISTS idx_urls_expires_at
    ON urls (expires_at)
    WHERE is_active = TRUE AND expires_at IS NOT NULL;

-- Auto-update updated_at timestamp on row modification
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_urls_updated_at
    BEFORE UPDATE ON urls
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
