package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.config.PaginationHelper;
import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
import com.springboot.app.mapper.ServiceProviderMapper;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    @Autowired
    private UserCredentialsService userCredentialsService; // Inject the UserCredentialsService

    @Override
    @Transactional
    public List<ServiceProviderDTO> getAllServiceProviderDTOs(int page, int size) {
        logger.info("Fetching service providers with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();

        // Using PaginationHelper to get paginated results
        List<ServiceProvider> serviceProviders = PaginationHelper.getPaginatedResults(
                session,
                "FROM ServiceProvider", // Use uppercase for consistency with HQL
                page,
                size,
                ServiceProvider.class);

        logger.debug("Fetched {} service provider(s) from the database.", serviceProviders.size());

        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderDTO getServiceProviderDTOById(Long id) {
        logger.info("Fetching service provider with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider == null) {
            logger.warn("Service provider with ID {} not found", id);
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
        }

        logger.debug("Found service provider: {}", serviceProvider);
        return serviceProviderMapper.serviceProviderToDTO(serviceProvider);
    }

    @Override
    @Transactional
    public void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        logger.info("Saving a new service provider: {}", serviceProviderDTO.getUsername());

        // Step 1: Register the user credentials using the injected service
        UserCredentialsDTO userDTO = new UserCredentialsDTO(
                serviceProviderDTO.getUsername(),
                serviceProviderDTO.getPassword(),
                true, // isActive
                0, // noOfTries
                null, // disableTill
                false, // isTempLocked
                serviceProviderDTO.getMobileNo().toString(),
                null // lastLogin
        );

        // Register the user credentials first
        String registrationResponse = userCredentialsService.saveUserCredentials(userDTO);
        logger.info("User registration response: {}", registrationResponse);

        // If registration failed, throw an error
        if (!"Registration successful!".equalsIgnoreCase(registrationResponse)) {
            logger.error("User registration failed for username: {}", serviceProviderDTO.getUsername());
            throw new RuntimeException("User registration failed: " + registrationResponse);
        }

        // Step 2: Save or update the service provider entity
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);

        serviceProvider.setActive(true);
        // serviceProvider.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        // serviceProvider.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // Use merge() instead of persist() to handle both new and existing entities
        session.merge(serviceProvider);

        logger.debug("Service provider saved successfully: {}", serviceProvider);
    }

    @Override
    @Transactional
    public void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        logger.info("Updating service provider with ID: {}", serviceProviderDTO.getServiceproviderId());
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider existingServiceProvider = session.get(ServiceProvider.class,
                serviceProviderDTO.getServiceproviderId());

        if (existingServiceProvider == null) {
            logger.warn("Service provider with ID {} not found for update", serviceProviderDTO.getServiceproviderId());
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND
                    + serviceProviderDTO.getServiceproviderId());
        }

        existingServiceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
        session.merge(existingServiceProvider);
        logger.debug("Service provider updated: {}", existingServiceProvider);
    }

    @Override
    @Transactional
    public void deleteServiceProviderDTO(Long id) {
        logger.info("Deleting (deactivating) service provider with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider != null) {
            serviceProvider.deactivate();
            session.merge(serviceProvider);
            logger.debug("Service provider with ID {} deactivated", id);
        } else {
            logger.warn("Service provider with ID {} not found for deletion", id);
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
        }
    }

    @Override
    @Transactional
    public List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender,
            Speciality speciality, HousekeepingRole housekeepingRole, Integer minAge, Integer maxAge) {
        logger.info("Filtering service providers with specified criteria");
        Session session = sessionFactory.getCurrentSession();

        // Start building the base HQL query
        StringBuilder hql = new StringBuilder("FROM ServiceProvider WHERE 1=1");

        // Dynamically append conditions based on non-null parameters
        if (language != null) {
            hql.append(" AND languageKnown = :language");
            logger.debug("Filtering by language: {}", language);
        }
        if (rating != null) {
            hql.append(" AND rating = :rating");
            logger.debug("Filtering by rating: {}", rating);
        }
        if (gender != null) {
            hql.append(" AND gender = :gender");
            logger.debug("Filtering by gender: {}", gender);
        }
        if (speciality != null) {
            hql.append(" AND speciality = :speciality");
            logger.debug("Filtering by speciality: {}", speciality);
        }
        if (housekeepingRole != null) {
            hql.append(" AND housekeepingRole = :housekeepingRole");
            logger.debug("Filtering by housekeeping role: {}", housekeepingRole);
        }
        // Add age range filtering
        if (minAge != null) {
            hql.append(" AND age >= :minAge");
            logger.debug("Filtering by minimum age: {}", minAge);
        }
        if (maxAge != null) {
            hql.append(" AND age <= :maxAge");
            logger.debug("Filtering by maximum age: {}", maxAge);
        }

        // Create the query from the dynamically built HQL
        Query<ServiceProvider> query = session.createQuery(hql.toString(), ServiceProvider.class);

        // Set parameters if they are not null
        if (language != null) {
            query.setParameter("language", language);
        }
        if (rating != null) {
            query.setParameter("rating", rating);
        }
        if (gender != null) {
            query.setParameter("gender", gender);
        }
        if (speciality != null) {
            query.setParameter("speciality", speciality);
        }
        if (housekeepingRole != null) {
            query.setParameter("housekeepingRole", housekeepingRole);
        }
        if (minAge != null) {
            query.setParameter("minAge", minAge);
        }
        if (maxAge != null) {
            query.setParameter("maxAge", maxAge);
        }

        // Execute the query and get the results
        List<ServiceProvider> serviceProviders = query.getResultList();
        logger.debug("Found {} service providers matching the criteria", serviceProviders.size());

        // Convert the list of ServiceProvider entities to a list of ServiceProviderDTOs
        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ServiceProviderDTO> getServiceProvidersByFilter(Integer pincode, String street, String locality) {
        logger.info("Fetching service providers with filter - Pincode: {}, Street: {}, Locality: {}", pincode, street,
                locality);

        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("FROM ServiceProvider WHERE ");

        // Dynamically build query based on provided parameter
        if (pincode != null) {
            hql.append("pincode = :pincode");
            logger.debug("Filtering by pincode: {}", pincode);
        } else if (street != null) {
            hql.append("street LIKE :street");
            logger.debug("Filtering by street: {}", street);
        } else if (locality != null) {
            hql.append("locality LIKE :locality");
            logger.debug("Filtering by locality: {}", locality);
        } else {
            logger.warn("No filter parameters provided. Returning all service providers.");
            hql.append("1=1"); // Return all service providers if no filters are applied
        }

        Query<ServiceProvider> query = session.createQuery(hql.toString(), ServiceProvider.class);

        // Set query parameters
        if (pincode != null) {
            query.setParameter("pincode", pincode);
        } else if (street != null) {
            query.setParameter("street", "%" + street + "%");
        } else if (locality != null) {
            query.setParameter("locality", "%" + locality + "%");
        }

        List<ServiceProvider> serviceProviders = query.getResultList();

        logger.debug("Found {} service provider(s) matching the criteria.", serviceProviders.size());

        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ServiceProviderDTO> getServiceProvidersByOrFilter(Integer pincode, String street, String locality) {
        logger.info("Fetching service providers with OR filter - Pincode: {}, Street: {}, Locality: {}", pincode,
                street, locality);

        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("FROM ServiceProvider WHERE ");

        // Dynamically build query based on provided parameter using OR
        boolean hasCondition = false;
        if (pincode != null) {
            hql.append("pincode = :pincode");
            hasCondition = true;
            logger.debug("Filtering by pincode: {}", pincode);
        }
        if (street != null) {
            if (hasCondition) {
                hql.append(" OR ");
            }
            hql.append("street LIKE :street");
            hasCondition = true;
            logger.debug("Filtering by street: {}", street);
        }
        if (locality != null) {
            if (hasCondition) {
                hql.append(" OR ");
            }
            hql.append("locality LIKE :locality");
            logger.debug("Filtering by locality: {}", locality);
        }
        if (!hasCondition) {
            logger.warn("No filter parameters provided. Returning all service providers.");
            hql.append("1=1"); // Return all service providers if no filters are applied
        }

        Query<ServiceProvider> query = session.createQuery(hql.toString(), ServiceProvider.class);

        // Set query parameters
        if (pincode != null) {
            query.setParameter("pincode", pincode);
        }
        if (street != null) {
            query.setParameter("street", "%" + street + "%");
        }
        if (locality != null) {
            query.setParameter("locality", "%" + locality + "%");
        }

        List<ServiceProvider> serviceProviders = query.getResultList();

        logger.debug("Found {} service provider(s) matching the criteria.", serviceProviders.size());

        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

}
