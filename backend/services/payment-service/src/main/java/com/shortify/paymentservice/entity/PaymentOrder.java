package com.shortify.paymentservice.entity;

import com.shortify.paymentservice.enums.OrderStatus;
import com.shortify.paymentservice.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Represents a Razorpay payment order.
 *
 * LIFECYCLE:
 *   1. User clicks "Upgrade" → we create an Order record + Razorpay order
 *   2. Razorpay returns order_id → frontend opens checkout modal
 *   3. User pays → Razorpay sends payment_id + signature
 *   4. We verify signature → update Order.status = PAID
 *   5. We create/update Subscription
 *
 * WHY STORE THE ORDER?
 *   - Audit trail — every payment attempt is recorded
 *   - Idempotency — we can detect duplicate payment verifications
 *   - Refund handling — we need the razorpayOrderId to issue refunds
 *   - Dispute evidence — Razorpay webhooks reference the order ID
 */
@Entity
@Table(
    name = "payment_orders",
    indexes = {
        @Index(name = "idx_orders_user_id",          columnList = "userId"),
        @Index(name = "idx_orders_razorpay_order_id", columnList = "razorpayOrderId", unique = true),
        @Index(name = "idx_orders_status",            columnList = "status")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Auth-service userId — plain Long (services are decoupled). */
    @Column(nullable = false)
    private Long userId;

    /** Razorpay's order ID (e.g. "order_PxyzAbc123"). */
    @Column(nullable = false, unique = true, length = 100)
    private String razorpayOrderId;

    /** Razorpay payment ID — set after successful payment (e.g. "pay_PxyzDef456"). */
    @Column(length = 100)
    private String razorpayPaymentId;

    /**
     * Razorpay signature — HMAC-SHA256(razorpayOrderId + "|" + razorpayPaymentId, apiSecret).
     * We store it for audit purposes after verification.
     */
    @Column(length = 500)
    private String razorpaySignature;

    /**
     * Amount in SMALLEST CURRENCY UNIT (paise for INR).
     * ₹299 = 29900 paise. Razorpay always works in smallest unit.
     * NEVER store as decimal — floating point is unreliable for money.
     */
    @Column(nullable = false)
    private Long amountInPaise;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanType planType;

    /** Razorpay receipt ID — for reconciliation and customer emails. */
    @Column(length = 100)
    private String receiptId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
