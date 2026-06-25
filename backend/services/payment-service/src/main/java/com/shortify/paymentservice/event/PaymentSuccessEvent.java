package com.shortify.paymentservice.event;

import java.time.Instant;

public record PaymentSuccessEvent(
        Long userId,
        String plan,
        String razorpayPaymentId,
        Long amount,
        String currency,
        Instant timestamp
) {}
