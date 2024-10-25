package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.constant.ServiceProviderConstants; // Import the constants class

import com.springboot.app.dto.ServiceProviderDTO;

import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
import com.springboot.app.mapper.ServiceProviderMapper; // Import the mapper
import org.hibernate.query.Query;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper; // Inject service provider mapper

    @Override
    @Transactional
    public List<ServiceProviderDTO> getAllServiceProviderDTOs() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProvider> serviceProviders = session.createQuery("from ServiceProvider", ServiceProvider.class)
                .list();

        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderDTO getServiceProviderDTOById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider == null) {
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
        }

        return serviceProviderMapper.serviceProviderToDTO(serviceProvider);
    }

    @Override
    @Transactional
    public void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
        session.persist(serviceProvider);
    }

    @Override
    @Transactional
    public void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider existingServiceProvider = session.get(ServiceProvider.class,
                serviceProviderDTO.getServiceproviderId());

        if (existingServiceProvider == null) {
            throw new RuntimeException(
                    ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + serviceProviderDTO.getServiceproviderId());
        }

        existingServiceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
        session.merge(existingServiceProvider);
    }

    @Override
    @Transactional
    public void deleteServiceProviderDTO(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider != null) {
            serviceProvider.deactivate();
            session.merge(serviceProvider);
        } else {
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
        }
    }

    @Override
    @Transactional
    public List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender,
            Speciality speciality, HousekeepingRole housekeepingRole) {

        Session session = sessionFactory.getCurrentSession();

        // Start building the base HQL query
        StringBuilder hql = new StringBuilder("FROM ServiceProvider WHERE 1=1");

        // Dynamically append conditions based on non-null parameters
        if (language != null) {
            hql.append(" AND languageKnown = :language");
        }
        if (rating != null) {
            hql.append(" AND rating = :rating");
        }
        if (gender != null) {
            hql.append(" AND gender = :gender");
        }
        if (speciality != null) {
            hql.append(" AND speciality = :speciality");
        }
        if (housekeepingRole != null) {
            hql.append(" AND housekeepingRole = :housekeepingRole");
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

        // Execute the query and get the results
        List<ServiceProvider> serviceProviders = query.getResultList();

        // Convert the list of ServiceProvider entities to a list of ServiceProviderDTOs
        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO)
                .collect(Collectors.toList());
    }

}
