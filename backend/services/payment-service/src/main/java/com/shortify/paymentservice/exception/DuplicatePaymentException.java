package com.shortify.paymentservice.exception;

public class DuplicatePaymentException extends PaymentException {
    public DuplicatePaymentException(String message) {
        super(message);
    }
}
