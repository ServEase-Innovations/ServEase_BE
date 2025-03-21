package com.springboot.app.controller;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.UserCredentialsDTO;

import com.springboot.app.service.UserCredentialsService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserCredentialsController {

    private final UserCredentialsService userCredentialsService;

    public UserCredentialsController(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserCredentialsDTO loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        try {
            // Directly return the response from the service method
            return userCredentialsService.checkLoginAttempts(username, password);
        } catch (RuntimeException e) {
            // Return a 401 Unauthorized response with the exception message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
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
    @ApiOperation(value = "Deactivate a user account")
    public ResponseEntity<String> deactivateUser(
            @ApiParam(value = "Username of the account to deactivate", required = true) @PathVariable String username) {
        boolean isDeactivated = userCredentialsService.deactivateUser(username);

        if (!isDeactivated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.USER_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.ACCOUNT_DEACTIVATED);
    }

    // API to update user credentials
    @PutMapping("/update")
    @ApiOperation(value = "Update user credentials")
    public ResponseEntity<String> updateUserCredentials(
            @ApiParam(value = "Updated user credentials object", required = true) @RequestBody UserCredentialsDTO userCredentialsDTO) {
        try {
            userCredentialsService.updateUserCredentials(userCredentialsDTO);
            return ResponseEntity.ok(ServiceProviderConstants.PASSWORD_UPDATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
