package com.springboot.app.enums;

public enum Status {
    NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED, PENDING, REJECTED, ON_HOLD, NEW;

    // Default value method (optional, for convenience)
    public static Status getDefault() {
        return NEW;
    }
}
