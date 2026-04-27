package com.smartqueue.business.exception;

public class BusinessOperationException extends RuntimeException {

    public BusinessOperationException(String message) {
        super(message);
    }
}