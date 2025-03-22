package com.springboot.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerFeedbackDTO;
import com.springboot.app.entity.CustomerFeedback;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.mapper.CustomerFeedbackMapper;
import com.springboot.app.repository.CustomerFeedbackRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerFeedbackServiceImpl implements CustomerFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerFeedbackServiceImpl.class);

    private final CustomerFeedbackRepository customerFeedbackRepository;
    private final CustomerFeedbackMapper customerFeedbackMapper;
    private final ServiceProviderRepository serviceProviderRepository;

    // Constructor injection
    public CustomerFeedbackServiceImpl(CustomerFeedbackRepository customerFeedbackRepository,
            CustomerFeedbackMapper customerFeedbackMapper,
            ServiceProviderRepository serviceProviderRepository) {
        this.customerFeedbackRepository = customerFeedbackRepository;
        this.customerFeedbackMapper = customerFeedbackMapper;
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerFeedbackDTO> getAllFeedback(int page, int size) {
        logger.info("Fetching all feedback with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerFeedback> feedbackList = customerFeedbackRepository.findAll(pageable).getContent();

        logger.debug("Fetched {} feedback entries from the database.", feedbackList.size());

        return feedbackList.stream()
                .map(customerFeedbackMapper::customerFeedbackToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerFeedbackDTO getFeedbackById(Long id) {
        logger.info("Fetching feedback by ID: {}", id);
        CustomerFeedback feedback = customerFeedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            logger.debug("Found feedback entry with ID: {}", id);
        } else {
            logger.error("No feedback found with ID: {}", id);
        }
        return customerFeedbackMapper.customerFeedbackToDTO(feedback);
    }

    @Override
    @Transactional
    public String addFeedback(CustomerFeedbackDTO customerFeedbackDTO) {
        logger.info("Adding new feedback for ServiceProvider ID: {}", customerFeedbackDTO.getServiceProviderId());

        // Fetch ServiceProvider using the repository
        ServiceProvider serviceProvider = serviceProviderRepository.findById(customerFeedbackDTO.getServiceProviderId())
                .orElseThrow(() -> {
                    logger.error("Service Provider not found with ID: {}", customerFeedbackDTO.getServiceProviderId());
                    return new IllegalArgumentException(
                            "Service Provider not found with ID: " + customerFeedbackDTO.getServiceProviderId());
                });

        // Map DTO to CustomerFeedback entity
        CustomerFeedback feedback = customerFeedbackMapper.dtoToCustomerFeedback(customerFeedbackDTO);
        feedback.setServiceProvider(serviceProvider);

        // Save the feedback
        customerFeedbackRepository.save(feedback);
        logger.debug("Persisted new feedback for ServiceProvider ID: {}", customerFeedbackDTO.getServiceProviderId());
        // Fetch all feedbacks for the ServiceProvider
        List<CustomerFeedback> providerFeedbacks = customerFeedbackRepository
                .findByServiceProviderServiceproviderId(customerFeedbackDTO.getServiceProviderId());

        // Fetch all feedbacks for the ServiceProvider
        // List<CustomerFeedback> providerFeedbacks = customerFeedbackRepository

        // Calculate the average rating
        double totalRating = providerFeedbacks.stream()
                .mapToDouble(CustomerFeedback::getRating)
                .sum();
        double averageRating = providerFeedbacks.isEmpty() ? feedback.getRating()
                : totalRating / providerFeedbacks.size();
        serviceProvider.setRating(averageRating);

        // Save updated ServiceProvider
        serviceProviderRepository.save(serviceProvider);
        logger.info("Updated ServiceProvider rating to: {}", averageRating);

        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String deleteFeedback(Long feedbackId) {
        logger.info("Deleting feedback with ID: {}", feedbackId);
        if (customerFeedbackRepository.existsById(feedbackId)) {
            customerFeedbackRepository.deleteById(feedbackId);
            logger.debug("Deleted feedback with ID: {}", feedbackId);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Feedback not found with ID: {}", feedbackId);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
