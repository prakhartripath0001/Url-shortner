package com.shortify.paymentservice.entity;

import com.shortify.paymentservice.enums.PlanType;
import com.shortify.paymentservice.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Tracks a user's active subscription to a paid plan.
 *
 * DESIGN DECISIONS:
 *
 * 1. ONE subscription per user (unique constraint on userId).
 *    When a user upgrades, we UPDATE their existing row.
 *    When they cancel, we set status = CANCELLED and mark cancellationDate.
 *    When they expire, a scheduled job sets status = EXPIRED and plan = FREE.
 *
 * 2. periodEnd is crucial:
 *    - A CANCELLED subscription is still VALID until periodEnd.
 *    - The scheduler checks for periodEnd < NOW to expire subscriptions.
 *    - This gives users the full paid period they paid for.
 *
 * 3. lastPaymentOrderId links to the last successful PaymentOrder.
 *    Useful for refund initiation without extra lookups.
 */
@Entity
@Table(
    name = "subscriptions",
    indexes = {
        @Index(name = "idx_sub_user_id",     columnList = "userId", unique = true),
        @Index(name = "idx_sub_status",      columnList = "status"),
        @Index(name = "idx_sub_period_end",  columnList = "periodEnd")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PlanType planType = PlanType.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.FREE;

    /** Billing period start (null for FREE plan). */
    @Column
    private Instant periodStart;

    /** Billing period end (null for FREE plan). */
    @Column
    private Instant periodEnd;

    /** When the user cancelled (null = not cancelled). */
    @Column
    private Instant cancelledAt;

    /** ID of the last successful PaymentOrder that activated this subscription. */
    @Column
    private Long lastPaymentOrderId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
