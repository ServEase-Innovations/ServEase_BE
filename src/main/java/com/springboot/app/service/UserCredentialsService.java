package com.springboot.app.service;

import com.springboot.app.dto.UserCredentialsDTO;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

//import org.springframework.http.ResponseEntity;

public interface UserCredentialsService {

    // Method to check login attempts
    public ResponseEntity<Map<String, Object>> checkLoginAttempts(String username, String password);

    // Method to deactivate user
    // String deactivateUser(String username);
    public boolean deactivateUser(String username);

    // Method to register a new user
    String saveUserCredentials(UserCredentialsDTO userCredentialsDTO);

    Optional<UserCredentialsDTO> getUserCredentialsByUsername(String username);

    void updateUserCredentials(UserCredentialsDTO userCredentialsDTO);
}
