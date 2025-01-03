package com.springboot.app.enums;

public enum TaskStatus {
    NOT_STARTED,
    STARTED,
    IN_PROGRESS,
    COMPLETED;

    // Default value method (optional, for convenience)
    public static TaskStatus getDefault() {
        return NOT_STARTED;
    }
}
