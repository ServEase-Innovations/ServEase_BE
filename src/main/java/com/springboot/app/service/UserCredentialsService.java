package com.springboot.app.service;

import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.enums.UserRole;

import java.util.Optional;

public interface UserCredentialsService {

    // Method to check login attempts
    String checkLoginAttempts(String username, String password, UserRole requiredRole);

    // Method to deactivate user
    String deactivateUser(String username);

    // Method to register a new user
    String saveUserCredentials(UserCredentialsDTO userCredentialsDTO);

    Optional<UserCredentialsDTO> getUserCredentialsByUsername(String username);

    void updateUserCredentials(UserCredentialsDTO userCredentialsDTO);
}
