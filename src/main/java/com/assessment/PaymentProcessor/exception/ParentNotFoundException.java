package com.assessment.PaymentProcessor.exception;

public class ParentNotFoundException extends RuntimeException {
    public ParentNotFoundException(String message) {
        super(message);
    }
}