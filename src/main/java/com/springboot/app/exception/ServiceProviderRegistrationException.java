package com.springboot.app.exception;

public class ServiceProviderRegistrationException extends RuntimeException {
    public ServiceProviderRegistrationException(String message) {
        super(message);
    }
}
