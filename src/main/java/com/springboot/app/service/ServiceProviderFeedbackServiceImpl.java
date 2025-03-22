package com.springboot.app.service;

import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.entity.ServiceProviderFeedback;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.mapper.ServiceProviderFeedbackMapper;
import com.springboot.app.repository.ServiceProviderFeedbackRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceProviderFeedbackServiceImpl implements ServiceProviderFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderFeedbackServiceImpl.class);

    private final ServiceProviderFeedbackRepository serviceProviderFeedbackRepository;
    private final ServiceProviderFeedbackMapper serviceProviderFeedbackMapper;
    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Autowired
    public ServiceProviderFeedbackServiceImpl(ServiceProviderFeedbackRepository serviceProviderFeedbackRepository,
            ServiceProviderFeedbackMapper serviceProviderFeedbackMapper,
            CustomerRepository customerRepository,
            ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderFeedbackRepository = serviceProviderFeedbackRepository;
        this.serviceProviderFeedbackMapper = serviceProviderFeedbackMapper;
        this.customerRepository = customerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderFeedbackDTO> getAllServiceProviderFeedbackDTOs(int page, int size) {
        logger.info("Fetching all service provider feedback with pagination - page: {}, size: {}", page, size);
        // Implement pagination logic if needed using Pageable
        List<ServiceProviderFeedback> feedbackList = serviceProviderFeedbackRepository.findAll(); // Adjust as needed
        return feedbackList.stream()
                .map(serviceProviderFeedbackMapper::serviceProviderFeedbackToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderFeedbackDTO getServiceProviderFeedbackDTOById(Long id) {
        logger.info("Fetching service provider feedback by ID: {}", id);
        ServiceProviderFeedback feedback = serviceProviderFeedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            logger.debug("Found service provider feedback with ID: {}", id);
        } else {
            logger.error("No service provider feedback found with ID: {}", id);
        }
        return serviceProviderFeedbackMapper.serviceProviderFeedbackToDTO(feedback);
    }

    @Override
    @Transactional
    public void saveServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO) {
        logger.info("Saving new service provider feedback for ServiceProvider ID: {}",
                serviceProviderFeedbackDTO.getServiceproviderId());

        // Fetch Customer and ServiceProvider entities
        Customer customer = customerRepository.findById(serviceProviderFeedbackDTO.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", serviceProviderFeedbackDTO.getCustomerId());
                    return new IllegalArgumentException(
                            "Customer not found with ID: " + serviceProviderFeedbackDTO.getCustomerId());
                });

        ServiceProvider serviceProvider = serviceProviderRepository
                .findById(serviceProviderFeedbackDTO.getServiceproviderId())
                .orElseThrow(() -> {
                    logger.error("Service Provider not found with ID: {}",
                            serviceProviderFeedbackDTO.getServiceproviderId());
                    return new IllegalArgumentException(
                            "Service Provider not found with ID: " + serviceProviderFeedbackDTO.getServiceproviderId());
                });

        // Map DTO to entity and set the relationships
        ServiceProviderFeedback feedback = serviceProviderFeedbackMapper
                .dtoToServiceProviderFeedback(serviceProviderFeedbackDTO);
        feedback.setCustomer(customer);
        feedback.setServiceprovider(serviceProvider);

        // Save the feedback entity
        serviceProviderFeedbackRepository.save(feedback);
        logger.info("Persisted new service provider feedback for ServiceProvider ID: {}",
                serviceProviderFeedbackDTO.getServiceproviderId());

        // Update the average rating for the Customer
        updateCustomerAverageRating(customer.getCustomerId());
    }

    @Override
    @Transactional
    public void updateServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO) {
        logger.info("Updating service provider feedback with ID: {}", serviceProviderFeedbackDTO.getId());

        // Find the existing feedback entry
        ServiceProviderFeedback existingFeedback = serviceProviderFeedbackRepository
                .findById(serviceProviderFeedbackDTO.getId())
                .orElseThrow(() -> {
                    logger.error("Service provider feedback not found with ID: {}", serviceProviderFeedbackDTO.getId());
                    return new IllegalArgumentException(
                            "Service provider feedback not found with ID: " + serviceProviderFeedbackDTO.getId());
                });

        // Map DTO to existing entity
        ServiceProviderFeedback updatedFeedback = serviceProviderFeedbackMapper
                .dtoToServiceProviderFeedback(serviceProviderFeedbackDTO);
        updatedFeedback.setId(existingFeedback.getId()); // Retain the original ID
        updatedFeedback.setCustomer(existingFeedback.getCustomer()); // Retain existing customer
        updatedFeedback.setServiceprovider(existingFeedback.getServiceprovider()); // Retain existing service provider

        // Save the updated feedback
        serviceProviderFeedbackRepository.save(updatedFeedback);
        logger.info("Updated service provider feedback with ID: {}", serviceProviderFeedbackDTO.getId());

        // Update the average rating for the Customer
        updateCustomerAverageRating(updatedFeedback.getCustomer().getCustomerId());
    }

    @Override
    @Transactional
    public void deleteServiceProviderFeedbackDTO(Long id) {
        logger.info("Deleting service provider feedback with ID: {}", id);
        if (serviceProviderFeedbackRepository.existsById(id)) {
            serviceProviderFeedbackRepository.deleteById(id);
            logger.info("Deleted service provider feedback with ID: {}", id);
        } else {
            logger.error("Service provider feedback not found with ID: {}", id);
            throw new IllegalArgumentException("Service provider feedback not found with ID: " + id);
        }
    }

    private void updateCustomerAverageRating(Long customerId) {
        logger.info("Updating average rating for Customer ID: {}", customerId);
        List<ServiceProviderFeedback> customerFeedbacks = serviceProviderFeedbackRepository
                .findByCustomer_CustomerId(customerId);

        double totalRating = customerFeedbacks.stream()
                .mapToDouble(ServiceProviderFeedback::getRating)
                .sum();
        double averageRating = customerFeedbacks.isEmpty() ? 0 : totalRating / customerFeedbacks.size();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", customerId);
                    return new IllegalArgumentException("Customer not found with ID: " + customerId);
                });

        customer.setRating(averageRating);
        customerRepository.save(customer);
        logger.info("Updated Customer rating to: {}", averageRating);
    }
}
