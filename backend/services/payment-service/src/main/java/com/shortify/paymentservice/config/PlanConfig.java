package com.shortify.paymentservice.config;

import com.shortify.paymentservice.enums.PlanType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Plan pricing configuration — loaded from application.properties.
 *
 * WHY CONFIGURE PRICES IN PROPERTIES (NOT HARDCODED)?
 * - Pricing changes without a code deployment.
 * - Different prices per environment (test/prod).
 * - Can be overridden per customer (enterprise pricing).
 *
 * Amounts are in PAISE (smallest INR unit).
 * ₹299/month = 29900 paise.
 */
@Configuration
@ConfigurationProperties(prefix = "app.plans")
public class PlanConfig {

    /**
     * Map of plan -> price in paise.
     * Loaded from:
     *   app.plans.prices.PRO=29900
     *   app.plans.prices.BUSINESS=99900
     */
    private Map<PlanType, Long> prices;

    /**
     * Plan descriptions (shown in Razorpay receipt email).
     */
    private Map<PlanType, String> descriptions;

    public Map<PlanType, Long> getPrices() { return prices; }
    public void setPrices(Map<PlanType, Long> prices) { this.prices = prices; }

    public Map<PlanType, String> getDescriptions() { return descriptions; }
    public void setDescriptions(Map<PlanType, String> descriptions) { this.descriptions = descriptions; }

    public long getPriceInPaise(PlanType plan) {
        return prices.getOrDefault(plan, 0L);
    }

    public String getDescription(PlanType plan) {
        return descriptions.getOrDefault(plan, "Shortify " + plan.name() + " Plan");
    }
}
