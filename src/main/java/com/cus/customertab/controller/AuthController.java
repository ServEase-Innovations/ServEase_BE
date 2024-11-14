package com.cus.customertab.controller;

import com.cus.customertab.dto.UserCredentialsDTO;
import com.cus.customertab.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Register user
    @PostMapping("/register")
    public String registerUser(@RequestBody UserCredentialsDTO userDTO) {
        return userService.registerUser(userDTO);
    }

    // Login user
    @PostMapping("/login")
    public String loginUser(@RequestBody UserCredentialsDTO userDTO) {
        return userService.authenticateUser(userDTO);
    }

    // Get all users
    @GetMapping("/get-all-users")
    public List<UserCredentialsDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by ID
    @GetMapping("/get-user-by-id/{id}")
    public UserCredentialsDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}