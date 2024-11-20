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
import org.springframework.beans.factory.annotation.Value;

@Service
public class UserCredentialsServiceImpl implements UserCredentialsService {

    @Value("${user.account.locking.enabled}")
    private boolean accountLockingEnabled;

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

        logger.info("Attempting to log in user: {}", username);

        // Fetch user credentials by username
        UserCredentials userCredentials = session.get(UserCredentials.class, username);

        if (userCredentials == null) {
            logger.warn("User not found: {}", username);
            return "User not found. Please register.";
        }

        // If account locking is disabled, skip lock checks and attempt login
        if (!accountLockingEnabled) {
            // Verify the password using BCrypt
            if (passwordEncoder.matches(password, userCredentials.getPassword())) {
                logger.info("User {} logged in successfully.", username);
                userCredentials.setNoOfTries(0); // Reset login attempts
                userCredentials.setLastLogin(new Timestamp(System.currentTimeMillis()));
                session.merge(userCredentials); // Update last login timestamp
                return "Login successful!";
            } else {
                logger.warn("Incorrect password for user: {}", username);
                return "Login failed! Incorrect credentials.";
            }
        }

        // Account locking logic follows (only applies if accountLockingEnabled is true)
        if (userCredentials.getDisableTill() != null &&
                userCredentials.getDisableTill().after(new Timestamp(System.currentTimeMillis()))) {
            logger.warn("Account is temporarily locked for user: {} until {}", username,
                    userCredentials.getDisableTill());
            return "Account is temporarily locked. Please try again after: " + userCredentials.getDisableTill();
        }

        if (passwordEncoder.matches(password, userCredentials.getPassword())) {
            logger.info("User {} logged in successfully.", username);
            userCredentials.setNoOfTries(0); // Reset login attempts
            userCredentials.setLastLogin(new Timestamp(System.currentTimeMillis()));
            session.merge(userCredentials); // Update last login timestamp
            return "Login successful!";
        } else {
            int attempts = userCredentials.getNoOfTries() + 1;
            userCredentials.setNoOfTries(attempts);
            logger.warn("Incorrect password for user: {}. Attempts: {}/3", username, attempts);

            if (attempts >= 3) {
                Timestamp lockTime = new Timestamp(System.currentTimeMillis() + lockSettingsConfig.getLocktime());
                userCredentials.setDisableTill(lockTime);
                userCredentials.lock();
                logger.error("User account locked due to multiple failed attempts. User: {}", username);
            }

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
