package com.springboot.app.service;

import com.springboot.app.config.LockSettingsConfig;

import com.springboot.app.dto.UserCredentialsDTO;

import com.springboot.app.entity.UserCredentials;
import com.springboot.app.enums.UserRole;
import com.springboot.app.mapper.CustomerMapper;
import com.springboot.app.mapper.ServiceProviderMapper;
import com.springboot.app.mapper.UserCredentialsMapper;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import com.springboot.app.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import java.util.Optional;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserCredentialsMapper userCredentialsMapper;
    private final LockSettingsConfig lockSettingsConfig;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    @Autowired
    public UserCredentialsServiceImpl(UserCredentialsRepository userCredentialsRepository,
            UserCredentialsMapper userCredentialsMapper,
            LockSettingsConfig lockSettingsConfig) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.userCredentialsMapper = userCredentialsMapper;
        this.lockSettingsConfig = lockSettingsConfig;
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> checkLoginAttempts(String username, String password) {
        UserCredentials user = getUserCredentials(username);

        if (isAccountLocked(user)) {
            // Create a structured response for an account locked scenario
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account is temporarily locked. Please try again after: " + user.getDisableTill());
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setNoOfTries(0);
            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            userCredentialsRepository.save(user);

            // Use the simplified response structure
            Map<String, Object> response = createSuccessResponse(user);
            return ResponseEntity.ok(response);
        }
        Map<String, Object> response = handleFailedLogin(user);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    private boolean isAccountLocked(UserCredentials user) {
        return user.getDisableTill() != null && user.getDisableTill().after(new Timestamp(System.currentTimeMillis()));
    }

    private UserCredentials getUserCredentials(String username) {
        return userCredentialsRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, Object> createSuccessResponse(UserCredentials user) {
        Map<String, Object> response = new HashMap<>();
        // response.put("username", user.getUsername());
        // response.put("isActive", user.isActive());
        response.put("role", user.getRole().name());
        // response.put("lastLogin", user.getLastLogin());

        if (user.getRole() == UserRole.CUSTOMER) {
            customerRepository.findAll().stream()
                    .filter(customer -> customer.getEmailId().equals(user.getUsername()))
                    .findFirst()
                    .map(customerMapper::customerToDTO)
                    .ifPresent(dto -> response.put("customerDetails", dto));
        } else if (user.getRole() == UserRole.SERVICE_PROVIDER) {
            serviceProviderRepository.findAll().stream()
                    .filter(provider -> provider.getEmailId().equals(user.getUsername()))
                    .findFirst()
                    .map(serviceProviderMapper::serviceProviderToDTO)
                    .ifPresent(dto -> response.put("serviceProviderDetails", dto));
        }

        return response;
    }

    // Modify handleFailedLogin to return a Map<String, Object>
    private Map<String, Object> handleFailedLogin(UserCredentials user) {
        int attempts = user.getNoOfTries() + 1;
        user.setNoOfTries(attempts);

        if (attempts >= 3) {
            long lockDuration = lockSettingsConfig.getLocktime();
            user.setDisableTill(new Timestamp(System.currentTimeMillis() + lockDuration));
            user.lock();
        }

        userCredentialsRepository.save(user);

        // Return a structured response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login failed! You have " + (3 - attempts) + " attempts remaining.");
        // response.put("remainingAttempts", 3 - attempts);
        return response;
    }

    @Override
    @Transactional
    public String deactivateUser(String username) {
        Optional<UserCredentials> optionalUser = userCredentialsRepository.findById(username);

        if (optionalUser.isEmpty()) {
            return "User not found. Unable to deactivate account.";
        }

        UserCredentials user = optionalUser.get();
        user.deactivate();
        userCredentialsRepository.save(user);
        return "Account deactivated successfully.";
    }

    @Override
    @Transactional
    public String saveUserCredentials(UserCredentialsDTO userCredentialsDTO) {
        if (userCredentialsRepository.existsByUsername(userCredentialsDTO.username())) {
            return "User already exists with this email. Please log in.";
        }

        String encryptedPassword = passwordEncoder.encode(userCredentialsDTO.password());
        UserCredentialsDTO updatedDTO = new UserCredentialsDTO(
                userCredentialsDTO.username(),
                encryptedPassword,
                userCredentialsDTO.isActive(),
                userCredentialsDTO.noOfTries(),
                userCredentialsDTO.disableTill(),
                userCredentialsDTO.isTempLocked(),
                userCredentialsDTO.phoneNumber(),
                userCredentialsDTO.lastLogin(),
                userCredentialsDTO.role());

        UserCredentials userCredentials = userCredentialsMapper.dtoToUserCredentials(updatedDTO);
        userCredentialsRepository.save(userCredentials);
        return "Registration successful!";
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserCredentialsDTO> getUserCredentialsByUsername(String username) {
        return userCredentialsRepository.findById(username)
                .map(userCredentialsMapper::userCredentialsToDTO);
    }

    @Override
    @Transactional
    public void updateUserCredentials(UserCredentialsDTO userCredentialsDTO) {
        if (userCredentialsDTO.username() == null || userCredentialsDTO.password() == null) {
            throw new IllegalArgumentException("Username and password are required for updating credentials.");
        }

        // Fetch the user by username
        Optional<UserCredentials> optionalUser = userCredentialsRepository.findById(userCredentialsDTO.username());

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with username: " + userCredentialsDTO.username());
        }

        UserCredentials user = optionalUser.get();

        // Update the password if it has changed
        if (!passwordEncoder.matches(userCredentialsDTO.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userCredentialsDTO.password()));
        }

        // Save the updated entity
        userCredentialsRepository.save(user);
    }

}
