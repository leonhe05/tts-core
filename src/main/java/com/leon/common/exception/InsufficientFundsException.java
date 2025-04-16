package com.leon.common.exception;

public class InsufficientFundsException extends IllegalStateException {

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
} 