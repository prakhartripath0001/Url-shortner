package com.shortify.paymentservice.dto;

import com.shortify.paymentservice.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long userId;
    private String planType;
    private String status;
    private Instant periodStart;
    private Instant periodEnd;
    private Instant cancelledAt;

    public static SubscriptionResponse from(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        return SubscriptionResponse.builder()
                .userId(subscription.getUserId())
                .planType(subscription.getPlanType() != null ? subscription.getPlanType().name() : "FREE")
                .status(subscription.getStatus() != null ? subscription.getStatus().name() : "FREE")
                .periodStart(subscription.getPeriodStart())
                .periodEnd(subscription.getPeriodEnd())
                .cancelledAt(subscription.getCancelledAt())
                .build();
    }
}
