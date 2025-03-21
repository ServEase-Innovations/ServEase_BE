package com.springboot.app.exception;

public class ServiceProviderNotFoundException extends RuntimeException {
    public ServiceProviderNotFoundException(String message) {
        super(message);
    }
}
