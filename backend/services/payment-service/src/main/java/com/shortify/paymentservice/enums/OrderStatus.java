package com.shortify.paymentservice.enums;

/**
 * Razorpay order status lifecycle:
 * CREATED → ATTEMPTED → PAID
 *                     ↘ FAILED
 *                     ↘ REFUNDED
 */
public enum OrderStatus {
    CREATED,    // Order created on Razorpay, awaiting payment attempt
    ATTEMPTED,  // Payment attempted (user opened checkout, may have failed)
    PAID,       // Payment successful and verified
    FAILED,     // All payment attempts failed
    REFUNDED    // Payment refunded
}
