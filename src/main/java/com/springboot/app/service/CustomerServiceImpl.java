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
        logger.info("Fetching all customers with page: {} and size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Customer> customers = customerRepository.findAll(pageable).getContent();
        logger.debug("Number of customers fetched: {}", customers.size());

        return customers.stream()
                .map(customerMapper::customerToDTO)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        logger.info("Fetching customer by id: {}", id);
        return customerRepository.findById(id)
                .map(customerMapper::customerToDTO)
                .orElseGet(() -> {
                    logger.error("Customer not found with id: {}", id);
                    return null;
                });
    }

    // Add a new customer
    @Override
    @Transactional
    public String saveCustomer(CustomerDTO customerDTO) {
        logger.info("Saving a new customer: {}", customerDTO);
        String email = customerDTO.getEmailId();
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("EmailId is required to save a customer.");
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
        logger.info("User registration response: {}", registrationResponse);

        // Map CustomerDTO to Customer entity
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        customer.setActive(true); // Set customer as active
        customerRepository.save(customer);

        logger.info("Customer saved with id: {}", customer.getCustomerId());
        return registrationResponse;
    }

    // Update an existing customer
    @Override
    @Transactional
    public String updateCustomer(CustomerDTO customerDTO) {
        logger.info("Updating customer with id: {}", customerDTO.getCustomerId());

        // Map DTO to entity
        Customer customer = customerMapper.dtoToCustomer(customerDTO);

        if (customerRepository.existsById(customer.getCustomerId())) {
            customerRepository.save(customer);
            logger.info("Customer updated with id: {}", customer.getCustomerId());
            return CustomerConstants.UPDATED;
        } else {
            logger.error("Customer not found for update with id: {}", customer.getCustomerId());
            return CustomerConstants.NOT_FOUND;
        }
    }

    // Soft-delete customer (deactivate)
    @Override
    @Transactional
    public String deleteCustomer(Long id) {
        logger.info("Deleting customer with id: {}", id);

        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setActive(false);
                    customerRepository.save(customer);
                    logger.info("Customer deactivated successfully with id: {}", id);
                    return CustomerConstants.DELETED;
                })
                .orElseGet(() -> {
                    logger.error("Customer not found for deletion with id: {}", id);
                    return CustomerConstants.NOT_FOUND;
                });
    }
}
