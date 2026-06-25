package com.shortify.paymentservice.controller;

import com.shortify.paymentservice.dto.CreateOrderRequest;
import com.shortify.paymentservice.dto.CreateOrderResponse;
import com.shortify.paymentservice.dto.SubscriptionResponse;
import com.shortify.paymentservice.dto.VerifyPaymentRequest;
import com.shortify.paymentservice.entity.Subscription;
import com.shortify.paymentservice.security.UserPrincipal;
import com.shortify.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Received request to create payment order for user: {}", principal.getUserId());
        return paymentService.createOrder(principal.getUserId(), request.plan());
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public void verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Received request to verify payment for user: {}, orderId: {}", principal.getUserId(), request.razorpayOrderId());
        paymentService.verifyPayment(principal.getUserId(), request);
    }

    @GetMapping("/subscription")
    public SubscriptionResponse getSubscription(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Received request to fetch subscription details for user: {}", principal.getUserId());
        Subscription subscription = paymentService.getSubscription(principal.getUserId());
        return SubscriptionResponse.from(subscription);
    }

    @PostMapping("/cancel")
    public SubscriptionResponse cancelSubscription(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Received request to cancel subscription for user: {}", principal.getUserId());
        Subscription subscription = paymentService.cancelSubscription(principal.getUserId());
        return SubscriptionResponse.from(subscription);
    }
}
