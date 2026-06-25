package com.shortify.paymentservice.dto;

import com.shortify.paymentservice.enums.PlanType;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "Plan type is required")
    PlanType plan
) {}
