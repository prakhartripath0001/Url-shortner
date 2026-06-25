package com.shortify.paymentservice.repository;

import com.shortify.paymentservice.entity.Subscription;
import com.shortify.paymentservice.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /** Primary lookup — one subscription per user. */
    Optional<Subscription> findByUserId(Long userId);

    /**
     * Find subscriptions that have passed their period end.
     * Used by the expiry scheduler to downgrade cancelled users.
     */
    @Query("""
        SELECT s FROM Subscription s
        WHERE s.status = 'CANCELLED'
          AND s.periodEnd IS NOT NULL
          AND s.periodEnd < :now
        """)
    Page<Subscription> findExpiredCancelledSubscriptions(@Param("now") Instant now, Pageable pageable);

    /** Admin: count users per plan (for revenue dashboard). */
    long countByStatus(SubscriptionStatus status);
}
