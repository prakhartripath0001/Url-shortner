package com.shortify.paymentservice.enums;

/**
 * Subscription lifecycle:
 * ACTIVE → CANCELLED (user cancels, remains active until period end)
 *        → EXPIRED   (period ended, downgraded to FREE)
 * FREE   (no payment needed — default for all users)
 */
public enum SubscriptionStatus {
    ACTIVE,
    CANCELLED,   // Cancelled but still valid until periodEnd
    EXPIRED,
    FREE
}
