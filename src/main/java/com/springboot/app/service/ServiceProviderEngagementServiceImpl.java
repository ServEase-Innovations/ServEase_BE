package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.exception.ServiceProviderEngagementNotFoundException;
import com.springboot.app.mapper.ServiceProviderEngagementMapper;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import java.util.Collections;

@Service
public class ServiceProviderEngagementServiceImpl implements ServiceProviderEngagementService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderEngagementServiceImpl.class);

    private final ServiceProviderEngagementRepository engagementRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final CustomerRepository customerRepository;
    private final ServiceProviderEngagementMapper engagementMapper;

    @Autowired
    public ServiceProviderEngagementServiceImpl(ServiceProviderEngagementRepository engagementRepository,
            ServiceProviderRepository serviceProviderRepository,
            CustomerRepository customerRepository,
            ServiceProviderEngagementMapper engagementMapper) {
        this.engagementRepository = engagementRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.customerRepository = customerRepository;
        this.engagementMapper = engagementMapper;
    }

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
        // Map entities to DTOs
        return engagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
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

        ServiceProvider serviceProvider = null;

        // Check if ServiceProviderId is provided
        if (dto.getServiceProviderId() != null) {
            serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                    .orElseThrow(() -> {
                        logger.error("ServiceProvider with ID {} not found.", dto.getServiceProviderId());
                        return new RuntimeException("Service Provider not found.");
                    });
        } else {
            logger.warn("No ServiceProvider ID provided. Skipping ServiceProvider association.");
        }

        // Fetch the Customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found.", dto.getCustomerId());
                    return new RuntimeException("Customer not found.");
                });

        // Map DTO to entity and set relationships
        ServiceProviderEngagement engagement = engagementMapper.dtoToServiceProviderEngagement(dto);

        // Set relationships if ServiceProvider exists
        if (serviceProvider != null) {
            engagement.setServiceProvider(serviceProvider);
        }
        engagement.setCustomer(customer);
        if (dto.getEndDate() != null && dto.getEndDate().isBefore(LocalDate.now())) {
            engagement.setTimeslot("00:00-00:00");
        }

        engagementRepository.save(engagement);
        logger.debug("Persisted new service provider engagement with ID: {}", engagement.getId());
        return "Service Provider Engagement added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderEngagement(ServiceProviderEngagementDTO dto) {
        logger.info("Updating service provider engagement with ID: {}", dto.getId());

        return engagementRepository.findById(dto.getId())
                .map(existingEngagement -> {
                    // Fetch related entities from DB
                    Customer customer = customerRepository.findById(dto.getCustomerId())
                            .orElseThrow(
                                    () -> new RuntimeException("Customer not found with ID: " + dto.getCustomerId()));

                    ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Service Provider not found with ID: " + dto.getServiceProviderId()));

                    // Update fields from DTO
                    engagementMapper.updateEntityFromDTO(dto, existingEngagement);
                    existingEngagement.setCustomer(customer);
                    existingEngagement.setServiceProvider(serviceProvider);

                    // Save updated entity
                    engagementRepository.save(existingEngagement);
                    logger.info("Service provider engagement updated successfully with ID: {}", dto.getId());
                    return ServiceProviderConstants.ENGAGEMENT_UPDATED;
                })
                .orElse(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND);
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
                .filter(e -> e.getServiceProvider() != null &&
                        e.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .toList();

        // Check if no engagements found and throw a dedicated exception
        if (filteredEngagements.isEmpty()) {
            throw new ServiceProviderEngagementNotFoundException(
                    "No data found for ServiceProvider ID: " + serviceProviderId);
        }

        // Convert to DTO and return an unmodifiable list
        return filteredEngagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByCustomerId(Long customerId) {
        logger.info("Fetching service provider engagements by Customer ID: {}", customerId);

        List<ServiceProviderEngagement> filteredEngagements = engagementRepository.findAll().stream()
                .filter(e -> e.getCustomer() != null && e.getCustomer().getCustomerId().equals(customerId))
                .toList();

        if (filteredEngagements.isEmpty()) {
            throw new ServiceProviderEngagementNotFoundException("No data found for Customer ID: " + customerId);
        }

        return filteredEngagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ServiceProviderEngagementDTO>> getServiceProviderBookingHistory(int page, int size) {
        logger.info("Fetching and categorizing service provider engagements with pagination - page: {}, size: {}", page,
                size);

        Pageable pageable = PageRequest.of(page, size);
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll(pageable).getContent();

        if (engagements.isEmpty()) {
            return Collections.emptyMap();
        }

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Categorize engagements and return directly
        return engagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .collect(Collectors.groupingBy(engagement -> {
                    LocalDate startDate = engagement.getStartDate();
                    LocalDate endDate = engagement.getEndDate();

                    if (endDate == null) {
                        if (startDate != null && (startDate.isBefore(currentDate) || startDate.isEqual(currentDate))) {
                            return "current";
                        } else {
                            return "future";
                        }
                    } else {
                        if (endDate.isBefore(currentDate)) {
                            return "past";
                        } else if (startDate != null && startDate.isAfter(currentDate)) {
                            return "future";
                        } else {
                            return "current";
                        }
                    }
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getEngagementsByExactDateAndTimeslot(
            LocalDate startDate, LocalDate endDate, String timeslot) {

        logger.info("Fetching engagements for startDate: {}, endDate: {}, timeslot: {}", startDate, endDate, timeslot);

        List<ServiceProviderEngagement> engagements = engagementRepository.findByExactDateAndTimeslot(startDate,
                endDate, timeslot);

        if (engagements.isEmpty()) {
            logger.warn("No engagements found for the given filters.");
            return Collections.emptyList();
        }

        return engagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

}
