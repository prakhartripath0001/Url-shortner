package com.shortify.paymentservice.enums;

/**
 * Plan tiers available in Shortify.
 * Ordinal order matters for feature gating — higher ordinal = more features.
 */
public enum PlanType {
    FREE,
    PRO,
    BUSINESS
}
