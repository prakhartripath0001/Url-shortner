package com.shortify.urlservice.repository;

import com.shortify.urlservice.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Primary lookup for redirect hot path.
     * Uses idx_urls_short_code index — O(log n).
     */
    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);

    /**
     * Check alias availability before creation.
     */
    boolean existsByShortCode(String shortCode);

    /**
     * Paginated user dashboard query.
     */
    Page<Url> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Count active URLs for a user (for rate limiting and dashboard stats).
     */
    long countByUserIdAndIsActiveTrue(Long userId);

    /**
     * Increment visit counter atomically without loading the full entity.
     * More efficient than find → modify → save for a hot-path counter.
     */
    @Modifying
    @Query("UPDATE Url u SET u.visitCount = u.visitCount + 1 WHERE u.shortCode = :shortCode")
    void incrementVisitCount(@Param("shortCode") String shortCode);

    /**
     * Soft-delete all URLs for a user (account deletion GDPR flow).
     */
    @Modifying
    @Query("UPDATE Url u SET u.isActive = false WHERE u.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);

    /**
     * Find expired active URLs (for scheduled cleanup job).
     * Uses idx_urls_expires_at index.
     */
    @Query("SELECT u FROM Url u WHERE u.isActive = true AND u.expiresAt IS NOT NULL AND u.expiresAt < :now")
    Page<Url> findExpiredUrls(@Param("now") Instant now, Pageable pageable);
}
