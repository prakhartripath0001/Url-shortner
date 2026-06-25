package com.shortify.paymentservice.exception;

public class OrderNotFoundException extends PaymentException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
