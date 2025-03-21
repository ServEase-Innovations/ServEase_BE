package com.springboot.app.exception;

public class ServiceProviderLeaveException extends RuntimeException {
    public ServiceProviderLeaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
