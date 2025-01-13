package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.mapper.ServiceProviderEngagementMapper;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import com.springboot.app.util.ResponsibilityParser;

import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
public class ServiceProviderEngagementServiceImpl implements ServiceProviderEngagementService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderEngagementServiceImpl.class);

    @Autowired
    private ServiceProviderEngagementRepository engagementRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceProviderEngagementMapper engagementMapper;

    @Override
    @Transactional
    public List<ServiceProviderEngagementDTO> getAllServiceProviderEngagements(int page, int size) {
        logger.info("Fetching service provider engagements with page: {} and size: {}", page, size);

        // Fetch paginated results using Spring Data JPA
        Pageable pageable = PageRequest.of(page, size);
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll(pageable).getContent();

        logger.debug("Number of service provider engagements fetched: {}", engagements.size());

        // Check if no engagements are found and log a warning
        if (engagements.isEmpty()) {
            logger.warn("No service provider engagements found on the requested page.");
            return new ArrayList<>(); // Return empty list if no engagements found
        }

        // Map entities to DTOs
        return engagements.stream()
                .map(engagement -> engagementMapper.serviceProviderEngagementToDTO(engagement))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderEngagementDTO getServiceProviderEngagementById(Long id) {
        logger.info("Fetching service provider engagement by ID: {}", id);

        return engagementRepository.findById(id)
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .orElseThrow(() -> {
                    logger.error("No service provider engagement found with ID: {}", id);
                    return new RuntimeException("Service Provider Engagement not found.");
                });
    }

    @Override
    @Transactional
    public String addServiceProviderEngagement(ServiceProviderEngagementDTO dto) {
        logger.info("Adding new service provider engagement");

        // Fetch the ServiceProvider
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                .orElseThrow(() -> {
                    logger.error("ServiceProvider with ID {} not found.", dto.getServiceProviderId());
                    return new RuntimeException("Service Provider not found.");
                });

        // Fetch the Customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found.", dto.getCustomerId());
                    return new RuntimeException("Customer not found.");
                });

        // Map DTO to entity and set relationships
        ServiceProviderEngagement engagement = engagementMapper.dtoToServiceProviderEngagement(dto);
        engagement.setServiceProvider(serviceProvider);
        engagement.setCustomer(customer);

        engagementRepository.save(engagement);
        logger.debug("Persisted new service provider engagement with ID: {}", engagement.getId());
        return "Service Provider Engagement added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderEngagement(ServiceProviderEngagementDTO dto) {
        logger.info("Updating service provider engagement with ID: {}", dto.getId());

        // Check if the service provider engagement exists
        if (engagementRepository.existsById(dto.getId())) {

            // Map DTO to entity and update
            ServiceProviderEngagement existingEngagement = engagementMapper.dtoToServiceProviderEngagement(dto);

            // Save the updated service provider engagement
            engagementRepository.save(existingEngagement);

            logger.info("Service provider engagement updated with ID: {}", dto.getId());
            return ServiceProviderConstants.ENGAGEMENT_UPDATED;
        } else {
            logger.error("Service provider engagement not found for update with ID: {}", dto.getId());
            return ServiceProviderConstants.ENGAGEMENT_NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deleteServiceProviderEngagement(Long id) {
        logger.info("Deactivating service provider engagement with ID: {}", id);

        // Check if the service provider engagement exists
        if (engagementRepository.existsById(id)) {

            // Fetch the engagement from the repository
            ServiceProviderEngagement engagement = engagementRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Service provider engagement not found with ID: {}", id);
                        return new RuntimeException("Service Provider Engagement not found.");
                    });

            // Mark engagement as completed (e.g., set `isActive` to false or similar)
            engagement.completeEngagement(); // Example method to deactivate engagement
            engagementRepository.save(engagement); // Save the updated engagement

            logger.info("Service provider engagement with ID {} deactivated", id);
            return ServiceProviderConstants.ENGAGEMENT_DELETED;
        } else {
            logger.error("Service provider engagement not found for deactivation with ID: {}", id);
            return ServiceProviderConstants.ENGAGEMENT_NOT_FOUND;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByServiceProviderId(Long serviceProviderId) {
        logger.info("Fetching service provider engagements by ServiceProvider ID: {}", serviceProviderId);

        // Fetch all engagements and filter by serviceProviderId
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll();
        List<ServiceProviderEngagement> filteredEngagements = engagements.stream()
                .filter(e -> e.getServiceProvider() != null
                        && e.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .collect(Collectors.toList());

        // Check if no engagements found
        if (filteredEngagements.isEmpty()) {
            throw new RuntimeException("No data found for ServiceProvider ID: " + serviceProviderId);
        }

        // Convert to DTO
        return filteredEngagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .collect(Collectors.toList());
    }

}
