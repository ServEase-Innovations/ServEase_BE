package com.springboot.app.exception;

public class VendorRegistrationException extends RuntimeException {
    public VendorRegistrationException(String message) {
        super(message);
    }

    public VendorRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
