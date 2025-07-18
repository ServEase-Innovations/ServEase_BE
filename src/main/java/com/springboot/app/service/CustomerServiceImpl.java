package com.springboot.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerDTO;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.enums.UserRole;
import com.springboot.app.mapper.CustomerMapper;
import com.springboot.app.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserCredentialsService userCredentialsService;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            UserCredentialsService userCredentialsService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.userCredentialsService = userCredentialsService;
    }

    // Get all customers with pagination
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all customers with page: {} and size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<Customer> customers = customerRepository.findAll(pageable).getContent();
        if (logger.isDebugEnabled()) {
            logger.debug("Number of customers fetched: {}", customers.size());
        }

        return customers.stream()
                .map(customerMapper::customerToDTO)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching customer by id: {}", id);
        }
        return customerRepository.findById(id)
                .map(customerMapper::customerToDTO)
                .orElseGet(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Customer not found with id: {}", id);
                    }
                    return null;
                });
    }

    // Add a new customer
    @Override
    @Transactional
    public String saveCustomer(CustomerDTO customerDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Saving a new customer: {}", customerDTO);
        }

        String email = customerDTO.getEmailId();
        String mobile = customerDTO.getMobileNo() != null ? customerDTO.getMobileNo().toString() : null;

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("EmailId is required to save a customer.");
        }


        boolean emailExists = customerRepository.existsByEmailId(email);
        if (emailExists) {
            if (logger.isInfoEnabled()) {
                logger.info("User already exists with this email: {}", email);
            }
            return "User already exists with this email";
        }

        // Mobile check
        if (mobile != null && !mobile.isEmpty()) {
            try {
                Long mobileLong = Long.parseLong(mobile);
                boolean mobileExists = customerRepository.existsByMobileNo(mobileLong);
                if (mobileExists) {
                    if (logger.isInfoEnabled()) {
                        logger.info("User already exists with this mobile number: {}", mobile);
                    }
                    return "User already exists with this mobile";
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid mobile number format. Must be digits only.");
            }
        }


        customerDTO.setUsername(email);
        // Step 1: Register the user credentials using the injected service
        UserCredentialsDTO userDTO = new UserCredentialsDTO(
                customerDTO.getUsername(),
                customerDTO.getPassword(),
                true, // isActive
                0, // Default number of tries
                null, // No lock time initially
                false, // Not temporarily locked
                customerDTO.getMobileNo().toString(),
                null, // Last login is initially null
                UserRole.CUSTOMER.getValue()

        );
        // Call the registerUser method to add the user
        String registrationResponse = userCredentialsService.saveUserCredentials(userDTO);
        if (logger.isInfoEnabled()) {
            logger.info("User registration response: {}", registrationResponse);
        }

        // Map CustomerDTO to Customer entity
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        customer.setActive(true); // Set customer as active
        customerRepository.save(customer);
        if (logger.isInfoEnabled()) {
            logger.info("Customer saved with id: {}", customer.getCustomerId());
        }
        return registrationResponse;
    }

    @Override
    @Transactional
    public String saveCustomerRequird(CustomerDTO customerDTO) {
        logger.info("Saving a new customer: {}", customerDTO);

        String email = customerDTO.getEmailId();
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("EmailId is required to save a customer.");
        }

        customerDTO.setUsername(email); // optional if username == email

        // Step 1: Register the user credentials (if required)
        UserCredentialsDTO userDTO = new UserCredentialsDTO(
                customerDTO.getUsername(),
                customerDTO.getPassword(),
                true, // isActive
                0, // tries
                null, // lockTime
                false, // isTempLocked
                null, // mobileNo not provided
                null, // lastLogin
                UserRole.CUSTOMER.getValue());
        String registrationResponse = userCredentialsService.saveUserCredentials(userDTO);
        logger.info("User registration response: {}", registrationResponse);

        // Step 2: Create and save Customer with only required fields
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmailId(customerDTO.getEmailId());
        customer.setActive(true); // default active
        customerRepository.save(customer);

        logger.info("Customer saved with ID: {}", customer.getCustomerId());
        return registrationResponse;
    }

    // Update an existing customer
    @Override
    @Transactional
    public String updateCustomer(CustomerDTO customerDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating customer with id: {}", customerDTO.getCustomerId());
        }

        // Map DTO to entity
        Customer customer = customerMapper.dtoToCustomer(customerDTO);

        if (customerRepository.existsById(customer.getCustomerId())) {
            customerRepository.save(customer);
            if (logger.isInfoEnabled()) {
                logger.info("Customer updated with id: {}", customer.getCustomerId());
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Customer not found for update with id: {}", customer.getCustomerId());
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    // Soft-delete customer (deactivate)
    @Override
    @Transactional
    public String deleteCustomer(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting customer with id: {}", id);
        }

        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setActive(false);
                    customerRepository.save(customer);
                    if (logger.isInfoEnabled()) {
                        logger.info("Customer deactivated successfully with id: {}", id);
                    }
                    return CustomerConstants.DELETED;
                })
                .orElseGet(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Customer not found for deletion with id: {}", id);
                    }
                    return CustomerConstants.NOT_FOUND;
                });
    }
}
