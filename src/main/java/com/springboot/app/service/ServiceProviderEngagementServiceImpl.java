package com.springboot.app.service;

import com.springboot.app.config.PaginationHelper;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.mapper.ServiceProviderEngagementMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceProviderEngagementServiceImpl implements ServiceProviderEngagementService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderEngagementServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderEngagementMapper serviceProviderEngagementMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getAllServiceProviderEngagements(int page, int size) {
        logger.info("Fetching service provider engagements with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();

        // Assuming `PaginationHelper.getPaginatedResults` 
        List<ServiceProviderEngagement> engagements = PaginationHelper.getPaginatedResults(
                session,
                "from ServiceProviderEngagement",
                page,
                size,
                ServiceProviderEngagement.class);

        logger.debug("Fetched {} service provider engagements from the database.", engagements.size());

        return engagements.stream()
                .map(serviceProviderEngagementMapper::serviceProviderEngagementToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderEngagementDTO getServiceProviderEngagementById(Long id) {
        logger.info("Fetching service provider engagement by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderEngagement engagement = session.get(ServiceProviderEngagement.class, id);

        if (engagement != null) {
            logger.debug("Found service provider engagement with ID: {}", id);
        } else {
            logger.error("No service provider engagement found with ID: {}", id);
        }
        return serviceProviderEngagementMapper.serviceProviderEngagementToDTO(engagement);
    }

    @Override
    @Transactional
    public String addServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        logger.info("Adding new service provider engagement");
        Session session = sessionFactory.getCurrentSession();

        Long serviceProviderId = serviceProviderEngagementDTO.getServiceProviderId();
        if (serviceProviderId == null) {
            logger.error("Service Provider ID must not be null.");
            return "Service Provider ID must not be null.";
        }

        Long customerId = serviceProviderEngagementDTO.getCustomerId();
        if (customerId == null) {
            logger.error("Customer ID must not be null.");
            return "Customer ID must not be null.";
        }

        // Fetch the existing ServiceProvider using its ID
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, serviceProviderId);
        if (serviceProvider == null) {
            logger.error("ServiceProvider with ID {} not found.", serviceProviderId);
            return "Service Provider not found.";
        }

        // Fetch the existing Customer using its ID
        Customer customer = session.get(Customer.class, customerId);
        if (customer == null) {
            logger.error("Customer with ID {} not found.", customerId);
            return "Customer not found.";
        }

        // Create the engagement and associate it with the fetched ServiceProvider and
        // Customer
        ServiceProviderEngagement engagement = serviceProviderEngagementMapper
                .dtoToServiceProviderEngagement(serviceProviderEngagementDTO);
        engagement.setServiceProvider(serviceProvider); // Set the fetched ServiceProvider
        engagement.setCustomer(customer); // Set the fetched Customer

        session.persist(engagement);
        logger.debug("Persisted new service provider engagement with ID: {}", engagement.getId());
        return "Service Provider Engagement added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        logger.info("Updating service provider engagement with ID: {}", serviceProviderEngagementDTO.getId());
        Session session = sessionFactory.getCurrentSession();

        ServiceProviderEngagement existingEngagement = session.get(ServiceProviderEngagement.class,
                serviceProviderEngagementDTO.getId());
        if (existingEngagement == null) {
            logger.warn("Service provider engagement not found with ID: {}", serviceProviderEngagementDTO.getId());
            return "Service Provider Engagement not found.";
        }
        ServiceProviderEngagement updatedEngagement = serviceProviderEngagementMapper
                .dtoToServiceProviderEngagement(serviceProviderEngagementDTO);
        updatedEngagement.setId(existingEngagement.getId());
        session.merge(updatedEngagement);
        logger.debug("Updated service provider engagement with ID: {}", serviceProviderEngagementDTO.getId());
        return "Service Provider Engagement updated successfully.";
    }

    @Override
    @Transactional
    public String deleteServiceProviderEngagement(Long id) {
        logger.info("Deactivating service provider engagement with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderEngagement existingEngagement = session.get(ServiceProviderEngagement.class, id);

        if (existingEngagement != null) {
            existingEngagement.completeEngagement(); // Mark as completed, which sets isActive to false
            session.merge(existingEngagement); // Update the engagement in the session
            logger.debug("Service provider engagement with ID {} deactivated", id);
            return "Service Provider Engagement deactivated successfully.";
        } else {
            logger.error("Service provider engagement not found with ID: {}", id);
            throw new RuntimeException("Service Provider Engagement not found with ID: " + id);
        }
    }

}
