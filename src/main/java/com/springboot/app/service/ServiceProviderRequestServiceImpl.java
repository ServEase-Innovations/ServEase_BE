package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.mapper.ServiceProviderRequestMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderRequestServiceImpl implements ServiceProviderRequestService {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ServiceProviderRequestMapper serviceProviderRequestMapper; // Inject request mapper

    @Override
    @Transactional
    public List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProviderRequest> requests = session
                .createQuery("from ServiceProviderRequest", ServiceProviderRequest.class).list();

        return requests.stream()
                .map(serviceProviderRequestMapper::serviceProviderRequestToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = session.get(ServiceProviderRequest.class, id);

        if (request == null) {
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND + id);
        }

        return serviceProviderRequestMapper.serviceProviderRequestToDTO(request);
    }

    @Override
    @Transactional
    public void saveServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = serviceProviderRequestMapper
                .dtoToServiceProviderRequest(serviceProviderRequestDTO);
        session.persist(request);
    }

    @Override
    @Transactional
    public void updateServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest existingRequest = session.get(ServiceProviderRequest.class,
                serviceProviderRequestDTO.getRequestId());

        if (existingRequest == null) {
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
                    + serviceProviderRequestDTO.getRequestId());
        }

        existingRequest = serviceProviderRequestMapper.dtoToServiceProviderRequest(serviceProviderRequestDTO);
        session.merge(existingRequest);
    }

    @Override
    @Transactional
    public void deleteServiceProviderRequestDTO(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = session.get(ServiceProviderRequest.class, id);

        if (request != null) {
            request.setIsResolved(ServiceProviderConstants.REQUEST_RESOLVED);
            session.merge(request);
        } else {
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND + id);
        }
    }

}
