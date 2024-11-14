package com.cus.customertab.service;

import com.cus.customertab.dto.UserCredentialsDTO;
import com.cus.customertab.entity.UserCredentials;
import com.cus.customertab.mapper.UserCredentialsMapper;
import com.cus.customertab.security.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

    private final SessionFactory sessionFactory;
    private final JwtUtil jwtUtil;

    @Autowired
    private UserCredentialsMapper userCredentialsMapper;

    public UserService(SessionFactory sessionFactory, JwtUtil jwtUtil) {
        this.sessionFactory = sessionFactory;
        this.jwtUtil = jwtUtil;
    }

    // Register user and generate token
    @Transactional
    public String registerUser(UserCredentialsDTO userDTO) {
        Session session = sessionFactory.getCurrentSession();

        // Check if the user already exists
        UserCredentials existingUser = session.get(UserCredentials.class, userDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }

        // Create a new UserCredentials entity
        UserCredentials user = new UserCredentials();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setNoOfTries(userDTO.getNoOfTries()); // Set noOfTries
        user.setTempLocked(userDTO.isTempLocked()); // Set isTempLocked
        user.setPhoneNumber(userDTO.getPhoneNumber()); // Set phoneNumber
        user.setActive(true);

        // Save the user to the database
        session.persist(user);

        // Generate JWT token after successful registration
        String token = jwtUtil.generateToken(user.getUsername());

        // Return response message with JWT token
        return "User registered successfully. Token: " + token;
    }

    // Authenticate user and return token or user data
    @Transactional
    public String authenticateUser(UserCredentialsDTO userDTO) {
        Session session = sessionFactory.getCurrentSession();
        UserCredentials user = session.get(UserCredentials.class, userDTO.getUsername());

        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(userDTO.getPassword())) {
            // Return user details and token
            String token = jwtUtil.generateToken(user.getUsername());
            return "Login successful. User: " + user.getUsername() + ", Token: " + token;
        }

        // Invalid credentials
        throw new RuntimeException("Invalid credentials");
    }

    // Fetch all users
    @Transactional
    public List<UserCredentialsDTO> getAllUsers() {
        logger.info("Fetching all users");
        Session session = sessionFactory.getCurrentSession();
        List<UserCredentials> users = session.createQuery("from UserCredentials", UserCredentials.class).list();
        logger.debug("Found {} users", users.size());

        return users.stream()
                .map(userCredentialsMapper::userCredentialsToDTO)
                .collect(Collectors.toList());
    }

    // Fetch user by ID
    @Transactional
    public UserCredentialsDTO getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        UserCredentials user = session.get(UserCredentials.class, id);

        if (user == null) {
            logger.warn("User with ID {} not found", id);
            throw new RuntimeException("User not found with id: " + id);
        }

        logger.debug("Found user: {}", user);
        return userCredentialsMapper.userCredentialsToDTO(user);
    }

}
