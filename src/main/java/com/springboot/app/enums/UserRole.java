package com.springboot.app.enums;

public enum UserRole {
    CUSTOMER(101111),
    SERVICE_PROVIDER(103333),
    VENDOR(105555);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(int value) {
        for (UserRole role : UserRole.values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}
