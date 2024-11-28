package com.springboot.app.dto;

import java.sql.Timestamp;

public record UserCredentialsDTO(
                String username,
                String password,
                boolean isActive,
                int noOfTries,
                Timestamp disableTill,
                boolean isTempLocked,
                String phoneNumber,
                Timestamp lastLogin,
                int role) {
}
