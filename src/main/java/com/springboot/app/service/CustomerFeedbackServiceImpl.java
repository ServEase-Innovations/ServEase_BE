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
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all feedback with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<CustomerFeedback> feedbackList = customerFeedbackRepository.findAll(pageable).getContent();

        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} feedback entries from the database.", feedbackList.size());
        }
        return feedbackList.stream()
                .map(customerFeedbackMapper::customerFeedbackToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerFeedbackDTO getFeedbackById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching feedback by ID: {}", id);
        }
        CustomerFeedback feedback = customerFeedbackRepository.findById(id).orElse(null);
        if (feedback != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found feedback entry with ID: {}", id);
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No feedback found with ID: {}", id);
            }
        }
        return customerFeedbackMapper.customerFeedbackToDTO(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerFeedbackDTO> getFeedbacksByServiceProviderId(Long serviceproviderId) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching feedback list for ServiceProvider ID: {}", serviceproviderId);
        }

        List<CustomerFeedback> feedbackList = customerFeedbackRepository
                .findByServiceProvider_ServiceproviderId(serviceproviderId);

        if (feedbackList.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No feedback found for ServiceProvider ID: {}", serviceproviderId);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Found {} feedback entries for ServiceProvider ID: {}",
                        feedbackList.size(), serviceproviderId);
            }
        }

        return feedbackList.stream()
                .map(customerFeedbackMapper::customerFeedbackToDTO)
                .toList();
    }

    @Override
    @Transactional
    public String addFeedback(CustomerFeedbackDTO customerFeedbackDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new feedback for ServiceProvider ID: {}", customerFeedbackDTO.getServiceProviderId());
        }
        // Fetch ServiceProvider using the repository
        ServiceProvider serviceProvider = serviceProviderRepository.findById(customerFeedbackDTO.getServiceProviderId())
                .orElseThrow(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Service Provider not found with ID: {}",
                                customerFeedbackDTO.getServiceProviderId());
                    }
                    return new IllegalArgumentException(
                            "Service Provider not found with ID: " + customerFeedbackDTO.getServiceProviderId());
                });

        // Map DTO to CustomerFeedback entity
        CustomerFeedback feedback = customerFeedbackMapper.dtoToCustomerFeedback(customerFeedbackDTO);
        feedback.setServiceProvider(serviceProvider);

        // Save the feedback
        customerFeedbackRepository.save(feedback);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new feedback for ServiceProvider ID: {}",
                    customerFeedbackDTO.getServiceProviderId());
        }
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
        if (logger.isInfoEnabled()) {
            logger.info("Updated ServiceProvider rating to: {}", averageRating);
        }

        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String deleteFeedback(Long feedbackId) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting feedback with ID: {}", feedbackId);
        }
        if (customerFeedbackRepository.existsById(feedbackId)) {
            customerFeedbackRepository.deleteById(feedbackId);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted feedback with ID: {}", feedbackId);
            }
            return CustomerConstants.DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Feedback not found with ID: {}", feedbackId);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }
}
