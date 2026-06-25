package com.shortify.paymentservice.service;

import com.razorpay.Order;
import com.shortify.paymentservice.config.PlanConfig;
import com.shortify.paymentservice.dto.CreateOrderResponse;
import com.shortify.paymentservice.dto.VerifyPaymentRequest;
import com.shortify.paymentservice.entity.PaymentOrder;
import com.shortify.paymentservice.entity.Subscription;
import com.shortify.paymentservice.enums.OrderStatus;
import com.shortify.paymentservice.enums.PlanType;
import com.shortify.paymentservice.enums.SubscriptionStatus;
import com.shortify.paymentservice.event.PaymentSuccessEvent;
import com.shortify.paymentservice.exception.DuplicatePaymentException;
import com.shortify.paymentservice.exception.OrderNotFoundException;
import com.shortify.paymentservice.exception.PaymentException;
import com.shortify.paymentservice.repository.PaymentOrderRepository;
import com.shortify.paymentservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Business logic service for payments and subscriptions.
 *
 * SEPARATION OF CONCERNS:
 * - RazorpayService: only talks to Razorpay API (thin wrapper)
 * - PaymentService:  orchestrates the full flow — DB + Razorpay + Kafka
 *
 * IDEMPOTENCY:
 * - verifyPayment checks for existing PAID order before processing.
 * - This prevents double-activation if the verify endpoint is called twice
 *   (network retry, duplicate webhook, etc.).
 *
 * TRANSACTION BOUNDARIES:
 * - @Transactional on verifyPayment: Order + Subscription updates are atomic.
 * - If Kafka publish fails AFTER DB commit, we use the Transactional Outbox
 *   pattern in production. For now: fire-and-forget with error logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayService razorpayService;
    private final PaymentOrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanConfig planConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.payment-success}")
    private String paymentSuccessTopic;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    // ─── CREATE ORDER ─────────────────────────────────────────────────────────

    /**
     * Step 1 of payment flow: creates a Razorpay order and saves it to DB.
     *
     * @param userId  authenticated user's ID
     * @param plan    which plan they want (PRO / BUSINESS)
     * @return CreateOrderResponse with Razorpay order details for frontend checkout
     */
    @Transactional
    public CreateOrderResponse createOrder(Long userId, PlanType plan) {
        if (plan == PlanType.FREE) {
            throw new PaymentException("Cannot create a payment order for the free plan.");
        }

        // Call Razorpay to create order
        Order razorpayOrder = razorpayService.createOrder(userId, plan);

        String razorpayOrderId = razorpayOrder.get("id");
        long amountInPaise     = ((Number) razorpayOrder.get("amount")).longValue();
        String currency        = razorpayOrder.get("currency");
        String receiptId       = razorpayOrder.get("receipt");

        // Save to DB for audit trail
        PaymentOrder order = PaymentOrder.builder()
                .userId(userId)
                .razorpayOrderId(razorpayOrderId)
                .amountInPaise(amountInPaise)
                .currency(currency)
                .planType(plan)
                .receiptId(receiptId)
                .status(OrderStatus.CREATED)
                .build();

        orderRepository.save(order);
        log.info("Payment order saved: localId={}, razorpayOrderId={}", order.getId(), razorpayOrderId);

        return CreateOrderResponse.builder()
                .id(razorpayOrderId)
                .amount(amountInPaise)
                .currency(currency)
                .keyId(razorpayKeyId)  // Frontend needs key_id to open checkout
                .plan(plan.name())
                .build();
    }

    // ─── VERIFY PAYMENT ───────────────────────────────────────────────────────

    /**
     * Step 3 of payment flow: verifies Razorpay signature and activates subscription.
     *
     * SECURITY CRITICAL:
     * 1. Verify HMAC signature first — reject if invalid
     * 2. Check for duplicate payment (idempotency)
     * 3. Only then activate subscription
     */
    @Transactional
    public void verifyPayment(Long userId, VerifyPaymentRequest request) {
        // 1. SIGNATURE VERIFICATION — must be first, before any DB changes
        razorpayService.verifySignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature()
        );

        // 2. Load our stored order
        PaymentOrder order = orderRepository
                .findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found: " + request.razorpayOrderId()));

        // 3. Ownership check — user can only verify their own orders
        if (!order.getUserId().equals(userId)) {
            log.warn("SECURITY: User {} attempted to verify order belonging to user {}",
                    userId, order.getUserId());
            throw new PaymentException("Order does not belong to this user.");
        }

        // 4. Idempotency — reject if already paid
        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("Duplicate payment verification attempt for orderId={}", request.razorpayOrderId());
            throw new DuplicatePaymentException("This payment has already been verified.");
        }

        // 5. Update order status
        order.setRazorpayPaymentId(request.razorpayPaymentId());
        order.setRazorpaySignature(request.razorpaySignature());
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 6. Activate subscription
        activateSubscription(userId, order.getPlanType(), order.getId());

        // 7. Publish event (async — does not affect transaction)
        publishPaymentSuccess(userId, order);

        log.info("Payment verified and subscription activated: userId={}, plan={}, orderId={}",
                userId, order.getPlanType(), order.getId());
    }

    // ─── SUBSCRIPTION ─────────────────────────────────────────────────────────

    public Subscription getSubscription(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .orElseGet(() -> Subscription.builder()
                        .userId(userId)
                        .planType(PlanType.FREE)
                        .status(SubscriptionStatus.FREE)
                        .build());
    }

    /**
     * Cancel subscription.
     * Sets status = CANCELLED but keeps plan active until periodEnd.
     * A scheduled job handles the final downgrade to FREE after periodEnd.
     */
    @Transactional
    public Subscription cancelSubscription(Long userId) {
        Subscription sub = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new PaymentException("No active subscription found."));

        if (sub.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new PaymentException("Subscription is not currently active.");
        }

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setCancelledAt(Instant.now());
        // Plan remains active until periodEnd — user gets what they paid for
        subscriptionRepository.save(sub);

        log.info("Subscription cancelled for userId={}, active until={}", userId, sub.getPeriodEnd());
        return sub;
    }

    // ─── PRIVATE HELPERS ──────────────────────────────────────────────────────

    private void activateSubscription(Long userId, PlanType plan, Long orderId) {
        Subscription sub = subscriptionRepository.findByUserId(userId)
                .orElse(Subscription.builder().userId(userId).build());

        Instant now = Instant.now();
        sub.setPlanType(plan);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setPeriodStart(now);
        sub.setPeriodEnd(now.plus(30, ChronoUnit.DAYS)); // Monthly billing
        sub.setCancelledAt(null);
        sub.setLastPaymentOrderId(orderId);

        subscriptionRepository.save(sub);
    }

    private void publishPaymentSuccess(Long userId, PaymentOrder order) {
        try {
            kafkaTemplate.send(paymentSuccessTopic, userId.toString(), new PaymentSuccessEvent(
                    userId,
                    order.getPlanType().name(),
                    order.getRazorpayPaymentId(),
                    order.getAmountInPaise(),
                    order.getCurrency(),
                    Instant.now()
            ));
        } catch (Exception e) {
            // Log but don't fail the transaction — payment is already confirmed
            // TODO: Implement Transactional Outbox Pattern for guaranteed delivery
            log.error("Failed to publish PaymentSuccessEvent for userId={}: {}", userId, e.getMessage());
        }
    }
}
