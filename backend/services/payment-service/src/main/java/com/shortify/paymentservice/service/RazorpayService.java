package com.shortify.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shortify.paymentservice.config.PlanConfig;
import com.shortify.paymentservice.enums.PlanType;
import com.shortify.paymentservice.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Low-level Razorpay API wrapper service.
 *
 * This service only talks to Razorpay. All business logic
 * (saving orders, updating subscriptions) lives in PaymentService.
 *
 * ─────────────────────────────────────────────────────────────────────
 *  HOW RAZORPAY PAYMENT FLOW WORKS
 * ─────────────────────────────────────────────────────────────────────
 *
 *  1. BACKEND creates an Order on Razorpay (this service, createOrder method)
 *     → Razorpay returns: order_id, amount, currency, status="created"
 *
 *  2. FRONTEND opens Razorpay Checkout JS SDK with the order_id
 *     → User enters card/UPI/netbanking details
 *     → On success, Razorpay returns: razorpay_order_id, razorpay_payment_id, razorpay_signature
 *
 *  3. BACKEND verifies the signature (this service, verifySignature method)
 *     → Signature = HMAC-SHA256(order_id + "|" + payment_id, key_secret)
 *     → If valid → payment is genuine → activate subscription
 *     → If invalid → someone tampered with the response → reject
 *
 *  WHY VERIFY ON BACKEND?
 *  - Frontend can be modified by attackers (browser devtools, scripts)
 *  - NEVER trust payment confirmation from frontend without server verification
 *  - ALWAYS verify the HMAC signature on your backend
 *
 * ─────────────────────────────────────────────────────────────────────
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;
    private final PlanConfig planConfig;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    /**
     * Step 1: Creates a Razorpay order.
     *
     * @param userId  — internal user ID (used to generate unique receipt)
     * @param plan    — which plan they're buying (PRO, BUSINESS)
     * @return Razorpay Order object containing: id, amount, currency, receipt
     */
    public Order createOrder(Long userId, PlanType plan) {
        long amountInPaise = planConfig.getPriceInPaise(plan);

        if (amountInPaise <= 0) {
            throw new PaymentException("Invalid plan for payment: " + plan);
        }

        // Unique receipt ID: "shortify_<userId>_<uuid-prefix>"
        // Razorpay uses this to deduplicate orders and in customer emails
        String receiptId = "shortify_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);
        orderRequest.put("notes", new JSONObject()
                .put("userId", userId.toString())
                .put("plan", plan.name())
                .put("description", planConfig.getDescription(plan)));

        try {
            Order order = razorpayClient.orders.create(orderRequest);
            log.info("Created Razorpay order: orderId={}, userId={}, plan={}, amount={}",
                    order.get("id"), userId, plan, amountInPaise);
            return order;
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for userId={}, plan={}: {}", userId, plan, e.getMessage());
            throw new PaymentException("Failed to initiate payment. Please try again.", e);
        }
    }

    /**
     * Step 3: Verifies the payment signature.
     *
     * CRYPTOGRAPHIC VERIFICATION:
     * Razorpay generates: HMAC_SHA256(orderId + "|" + paymentId, keySecret)
     * We independently compute the same hash and compare.
     * If they match → the payment response genuinely came from Razorpay.
     * If they don't → the response was tampered with → REJECT immediately.
     *
     * @throws PaymentException if signature verification fails
     */
    public void verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        try {
            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);
            if (!isValid) {
                log.warn("Invalid Razorpay signature for orderId={}, paymentId={}",
                        razorpayOrderId, razorpayPaymentId);
                throw new PaymentException("Payment signature verification failed. Possible fraud attempt.");
            }
            log.info("Signature verified successfully: orderId={}, paymentId={}",
                    razorpayOrderId, razorpayPaymentId);
        } catch (RazorpayException e) {
            log.error("Signature verification error: {}", e.getMessage());
            throw new PaymentException("Payment verification failed: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies a webhook signature.
     * Used to validate incoming webhook events from Razorpay.
     *
     * Webhook signature = HMAC_SHA256(rawBody, webhookSecret)
     *
     * @param payload       raw request body as string
     * @param signature     X-Razorpay-Signature header
     * @param webhookSecret your Razorpay webhook secret (separate from API secret)
     */
    public boolean verifyWebhookSignature(String payload, String signature, String webhookSecret) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            log.warn("Webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fetches payment details from Razorpay (for reconciliation or refund).
     */
    public Payment fetchPayment(String razorpayPaymentId) {
        try {
            return razorpayClient.payments.fetch(razorpayPaymentId);
        } catch (RazorpayException e) {
            log.error("Failed to fetch payment {}: {}", razorpayPaymentId, e.getMessage());
            throw new PaymentException("Failed to fetch payment details.", e);
        }
    }

    /**
     * Initiates a full refund for a payment.
     *
     * WHEN TO USE:
     * - User cancels within refund period (e.g., 7-day money-back guarantee)
     * - Double charge detected
     * - Service unavailable during paid period
     */
    public void refundPayment(String razorpayPaymentId, long amountInPaise) {
        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amountInPaise);
        refundRequest.put("speed", "normal"); // "normal" (3-5 days) or "optimum"
        refundRequest.put("notes", new JSONObject()
                .put("reason", "Customer requested refund"));

        try {
            razorpayClient.payments.refund(razorpayPaymentId, refundRequest);
            log.info("Refund initiated for paymentId={}, amount={}", razorpayPaymentId, amountInPaise);
        } catch (RazorpayException e) {
            log.error("Refund failed for paymentId={}: {}", razorpayPaymentId, e.getMessage());
            throw new PaymentException("Refund initiation failed: " + e.getMessage(), e);
        }
    }
}
