package com.cus.customertab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cus.customertab.constants.ServiceProviderConstants;
import com.cus.customertab.dto.ServiceProviderDTO;
import com.cus.customertab.entity.ServiceProvider;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.enums.Speciality;
import com.cus.customertab.mapper.ServiceProviderMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    @Override
    @Transactional
    public List<ServiceProviderDTO> getAllServiceProviderDTOs() {
        logger.info("Fetching all service providers");
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProvider> serviceProviders = session.createQuery("from ServiceProvider", ServiceProvider.class)
                .list();
        logger.debug("Found {} service providers", serviceProviders.size());

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
        logger.info("Saving a new service provider");
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
        session.persist(serviceProvider);
        logger.debug("Service provider saved: {}", serviceProvider);
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
            Speciality speciality, ServiceType housekeepingRole, Integer minAge, Integer maxAge) {
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
}
