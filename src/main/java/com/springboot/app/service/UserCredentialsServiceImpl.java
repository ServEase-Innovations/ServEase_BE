package com.springboot.app.service;

import com.springboot.app.config.LockSettingsConfig;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.UserCredentials;
import com.springboot.app.mapper.UserCredentialsMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsServiceImpl.class);

    // BCryptPasswordEncoder instance for password hashing and verification
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private final SessionFactory sessionFactory;

    @Autowired
    private final UserCredentialsMapper userCredentialsMapper;

    @Autowired
    private final LockSettingsConfig lockSettingsConfig;

    @Autowired
    public UserCredentialsServiceImpl(SessionFactory sessionFactory, UserCredentialsMapper userCredentialsMapper,
            LockSettingsConfig lockSettingsConfig) {
        this.sessionFactory = sessionFactory;
        this.userCredentialsMapper = userCredentialsMapper;
        this.lockSettingsConfig = lockSettingsConfig;
    }

    @Override
    @Transactional
    public String checkLoginAttempts(String username, String password) {
        Session session = sessionFactory.getCurrentSession();

        // Fetch user credentials by username (emailId)
        UserCredentials userCredentials = session.get(UserCredentials.class, username);

        if (userCredentials == null) {
            // If the user doesn't exist, return a message
            return "User not found. Please register.";
        }

        // Check if the account is locked (disableTill)
        if (userCredentials.getDisableTill() != null
                && userCredentials.getDisableTill().after(new Timestamp(System.currentTimeMillis()))) {
            logger.info("Account is temporarily locked. Please try again after: " + userCredentials.getDisableTill());
            return "Account is temporarily locked. Please try again after: " + userCredentials.getDisableTill();
        }

        // If password is correct (verify against the encrypted password)
        if (passwordEncoder.matches(password, userCredentials.getPassword())) {
            // Reset failed attempts on successful login
            userCredentials.setNoOfTries(0);
            userCredentials.setLastLogin(new Timestamp(System.currentTimeMillis())); // Update last login timestamp
            session.merge(userCredentials);
            return "Login successful!";
        } else {
            // If password is incorrect, increment the number of tries
            int attempts = userCredentials.getNoOfTries() + 1;
            userCredentials.setNoOfTries(attempts);

            if (attempts >= 3) {
                // Lock the account if more than 3 attempts, set disableTill to 30 minutes from
                // now
                Timestamp lockTime = new Timestamp(System.currentTimeMillis() + lockSettingsConfig.getLocktime());
                userCredentials.setDisableTill(lockTime);
                userCredentials.lock(); // Temporarily lock the account
            }

            // Save the updated user credentials
            session.merge(userCredentials);
            return "Login failed! You have " + (3 - attempts) + " attempts remaining.";
        }
    }

    @Override
    @Transactional
    public String deactivateUser(String username) {
        Session session = sessionFactory.getCurrentSession();
        UserCredentials userCredentials = session.get(UserCredentials.class, username);

        if (userCredentials != null) {
            userCredentials.deactivate(); // Deactivate the account
            session.merge(userCredentials);
            return "Account deactivated successfully.";
        } else {
            return "User not found. Unable to deactivate account.";
        }
    }

    @Override
    @Transactional
    public String saveUserCredentials(UserCredentialsDTO userCredentialsDTO) {
        Session session = sessionFactory.getCurrentSession();

        // Check if the user already exists by username (email)
        UserCredentials existingUserByUsername = session.get(UserCredentials.class, userCredentialsDTO.username());

        if (existingUserByUsername != null) {
            return "User already exists with this email. Please log in.";
        }

        // Check if the user already exists by phone number
        String phoneQuery = "FROM UserCredentials WHERE phoneNumber = :phoneNumber";
        UserCredentials existingUserByPhone = session.createQuery(phoneQuery, UserCredentials.class)
                .setParameter("phoneNumber", userCredentialsDTO.phoneNumber())
                .uniqueResult();

        if (existingUserByPhone != null) {
            return "User already registered with this phone number. Please log in.";
        }

        // Encrypt the password using BCrypt before saving
        String encryptedPassword = passwordEncoder.encode(userCredentialsDTO.password());

        // Set the encrypted password in the DTO
        UserCredentialsDTO updatedUserDTO = new UserCredentialsDTO(
                userCredentialsDTO.username(),
                encryptedPassword, // Store encrypted password
                userCredentialsDTO.isActive(),
                userCredentialsDTO.noOfTries(),
                userCredentialsDTO.disableTill(),
                userCredentialsDTO.isTempLocked(),
                userCredentialsDTO.phoneNumber(),
                userCredentialsDTO.lastLogin());

        // Map the DTO to entity
        UserCredentials userCredentials = userCredentialsMapper.dtoToUserCredentials(updatedUserDTO);

        // Persist the new user credentials
        session.persist(userCredentials);

        return "Registration successful!";
    }

    @Override
    @Transactional
    public Optional<UserCredentialsDTO> getUserCredentialsByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        UserCredentials userCredentials = session.get(UserCredentials.class, username);
        return Optional.ofNullable(userCredentials).map(userCredentialsMapper::userCredentialsToDTO);
    }

    @Override
    @Transactional
    public void updateUserCredentials(UserCredentialsDTO userCredentialsDTO) {
        Session session = sessionFactory.getCurrentSession();
        UserCredentials userCredentials = userCredentialsMapper.dtoToUserCredentials(userCredentialsDTO);
        session.merge(userCredentials); // Update the user credentials
    }
}
