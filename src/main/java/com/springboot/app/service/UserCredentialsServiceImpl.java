package com.springboot.app.service;

import com.springboot.app.config.LockSettingsConfig;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.UserCredentials;
import com.springboot.app.enums.UserRole;
import com.springboot.app.mapper.UserCredentialsMapper;
import com.springboot.app.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final UserCredentialsMapper userCredentialsMapper;
    private final LockSettingsConfig lockSettingsConfig;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public String checkLoginAttempts(String username, String password, UserRole requiredRole) {
        Optional<UserCredentials> optionalUser = userCredentialsRepository.findById(username);

        if (optionalUser.isEmpty()) {
            return "User not found. Please register.";
        }

        UserCredentials user = optionalUser.get();

        // Check if the account is temporarily locked
        if (user.getDisableTill() != null &&
                user.getDisableTill().after(new Timestamp(System.currentTimeMillis()))) {
            return "Account is temporarily locked. Please try again after: " + user.getDisableTill();
        }

        // Verify role
        UserRole userRole = user.getRole(); // Directly use the UserRole object

        if (!requiredRole.equals(userRole)) {
            return "Access denied. You do not have the required role (" + requiredRole.name() + ") for login.";
        }

        // Verify password
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setNoOfTries(0); // Reset failed attempts on successful login
            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            userCredentialsRepository.save(user);
            return "Login successful!";
        } else {
            int attempts = user.getNoOfTries() + 1;
            user.setNoOfTries(attempts);

            if (attempts >= 3) {
                // Lock account using the configured lock time
                long lockDuration = lockSettingsConfig.getLocktime();
                user.setDisableTill(new Timestamp(System.currentTimeMillis() + lockDuration));
                user.lock();
            }

            userCredentialsRepository.save(user);
            return "Login failed! You have " + (3 - attempts) + " attempts remaining.";
        }
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
