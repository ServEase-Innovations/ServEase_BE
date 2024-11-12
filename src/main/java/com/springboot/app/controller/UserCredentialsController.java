package com.springboot.app.controller;

import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.service.UserCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserCredentialsController {

    private final UserCredentialsService userCredentialsService;

    @Autowired
    public UserCredentialsController(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    // Endpoint to login a user account
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String response = userCredentialsService.checkLoginAttempts(username, password);

        if (response.contains("User not found")) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // User not found, 404
        } else if (response.contains("locked")) {
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // Account locked, 403
        } else if (response.contains("Login successful")) {
            return new ResponseEntity<>(response, HttpStatus.OK); // Login successful, 200
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // Login failed, 401
        }
    }

    // Endpoint to register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserCredentialsDTO userCredentialsDTO) {
        // Call the service method to save the user credentials
        String responseMessage = userCredentialsService.saveUserCredentials(userCredentialsDTO);
        // Return appropriate HTTP status based on the response message
        if (responseMessage.contains("successful")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        } else if (responseMessage.contains("email")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessage);
        } else if (responseMessage.contains("phone number")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessage);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An unexpected error occurred.");
        }
    }

    // Endpoint to fetch user details by username
    @GetMapping("/{username}")
    public ResponseEntity<UserCredentialsDTO> getUserCredentials(@PathVariable String username) {
        Optional<UserCredentialsDTO> userCredentialsDTO = userCredentialsService.getUserCredentialsByUsername(username);

        if (userCredentialsDTO.isPresent()) {
            return ResponseEntity.ok(userCredentialsDTO.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // or you can throw a custom exception for user not found
        }
    }

    // Endpoint to deactivate a user account
    @PatchMapping("/deactivate/{username}")
    public ResponseEntity<String> deactivateUser(@PathVariable String username) {
        userCredentialsService.deactivateUser(username);
        return ResponseEntity.ok("Account deactivated successfully.");
    }

    // Endpoint to update user credentials
    @PutMapping("/update")
    public ResponseEntity<String> updateUserCredentials(@RequestBody UserCredentialsDTO userCredentialsDTO) {
        userCredentialsService.updateUserCredentials(userCredentialsDTO);
        return ResponseEntity.ok("User credentials updated successfully.");
    }
}
