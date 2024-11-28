package com.springboot.app.service;

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

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getAllServiceProviderEngagements(int page, int size) {
        logger.info("Fetching service provider engagements with pagination - page: {}, size: {}", page, size);

        return engagementRepository.findAll(PageRequest.of(page, size)).stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
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

        ServiceProviderEngagement engagement = engagementRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Service provider engagement not found with ID: {}", dto.getId());
                    return new RuntimeException("Service Provider Engagement not found.");
                });

        engagementMapper.updateEntityFromDTO(dto, engagement);
        engagementRepository.save(engagement);
        logger.debug("Updated service provider engagement with ID: {}", dto.getId());
        return "Service Provider Engagement updated successfully.";
    }

    @Override
    @Transactional
    public String deleteServiceProviderEngagement(Long id) {
        logger.info("Deactivating service provider engagement with ID: {}", id);

        ServiceProviderEngagement engagement = engagementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Service provider engagement not found with ID: {}", id);
                    return new RuntimeException("Service Provider Engagement not found.");
                });

        engagement.completeEngagement(); // Mark as completed (e.g., set `isActive` to false)
        engagementRepository.save(engagement); // Update engagement in the repository
        logger.debug("Service provider engagement with ID {} deactivated", id);
        return "Service Provider Engagement deactivated successfully.";
    }
}
