package com.springboot.app.exception;

public class ServiceProviderEngagementNotFoundException extends RuntimeException {
    public ServiceProviderEngagementNotFoundException(String message) {
        super(message);
    }
}
