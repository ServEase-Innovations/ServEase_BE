
package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.mapper.ServiceProviderMapper; // Import the mapper

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper; // Inject the mapper

    @Override
    @Transactional
    public List<ServiceProviderDTO> getAllServiceProviderDTOs() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProvider> serviceProviders = session.createQuery("from ServiceProvider", ServiceProvider.class).list();
        
        // Convert each ServiceProvider entity to DTO and return the list of DTOs
        return serviceProviders.stream()
                .map(serviceProviderMapper::serviceProviderToDTO) // Use the injected mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderDTO getServiceProviderDTOById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider == null) {
            throw new RuntimeException("ServiceProvider not found with id: " + id);
        }

        // Convert entity to DTO and return it
        return serviceProviderMapper.serviceProviderToDTO(serviceProvider); // Use the injected mapper
    }

    @Override
    @Transactional
    public void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        Session session = sessionFactory.getCurrentSession();
        // Convert DTO to entity
        ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO); // Use the injected mapper

        session.persist(serviceProvider);
    }

    @Override
    @Transactional
    public void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider existingServiceProvider = session.get(ServiceProvider.class, serviceProviderDTO.getServiceproviderId());

        if (existingServiceProvider == null) {
            throw new RuntimeException("ServiceProvider not found with id: " + serviceProviderDTO.getServiceproviderId());
        }

        // Use the mapper to map updated fields
        existingServiceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO); // Use the injected mapper
        
        // Merge the updated entity back into the session
        session.merge(existingServiceProvider);
    }

    @Override
    @Transactional
    public void deleteServiceProviderDTO(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProvider serviceProvider = session.get(ServiceProvider.class, id);

        if (serviceProvider != null) {
            serviceProvider.deactivate();  // Set `isActive` to false instead of deleting the record
            session.merge(serviceProvider);
        } else {
            throw new RuntimeException("ServiceProvider not found with id: " + id);
        }
    }
}




