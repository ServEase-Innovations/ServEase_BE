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

import com.springboot.app.mapper.ServiceProviderMapper; // Import the mapper

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

}
