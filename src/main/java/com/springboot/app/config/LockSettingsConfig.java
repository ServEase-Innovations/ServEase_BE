package com.springboot.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockSettingsConfig {
    @Value("${locktime}")
    private long locktime;

    public long getLocktime() {
        return locktime;
    }
}
