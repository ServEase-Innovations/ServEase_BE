package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.constant.ServiceProviderConstants; // Import the constants class
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderFeedback;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.mapper.ServiceProviderFeedbackMapper;
import com.springboot.app.mapper.ServiceProviderMapper; // Import the mapper
import com.springboot.app.mapper.ServiceProviderRequestMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper; // Inject service provider mapper

    @Autowired
    private ServiceProviderRequestMapper serviceProviderRequestMapper; // Inject request mapper

    @Autowired
    private ServiceProviderFeedbackMapper serviceProviderFeedbackMapper; // Inject the feedback mapper

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

    @Override
    @Transactional
    public List<ServiceProviderFeedbackDTO> getAllServiceProviderFeedbackDTOs() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProviderFeedback> feedbacks = session
                .createQuery("from ServiceProviderFeedback", ServiceProviderFeedback.class)
                .list();

        return feedbacks.stream()
                .map(serviceProviderFeedbackMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderFeedbackDTO getServiceProviderFeedbackDTOById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = session.get(ServiceProviderFeedback.class, id);

        if (feedback == null) {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + id);
        }

        return serviceProviderFeedbackMapper.toDTO(feedback);
    }

    @Override
    @Transactional
    public void saveServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO feedbackDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = serviceProviderFeedbackMapper.toEntity(feedbackDTO);
        session.persist(feedback);
    }

    @Override
    @Transactional
    public void updateServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO feedbackDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback existingFeedback = session.get(ServiceProviderFeedback.class, feedbackDTO.getId());

        if (existingFeedback == null) {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + feedbackDTO.getId());
        }

        existingFeedback = serviceProviderFeedbackMapper.toEntity(feedbackDTO);
        session.merge(existingFeedback);
    }

    @Override
    @Transactional
    public void deleteServiceProviderFeedbackDTO(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = session.get(ServiceProviderFeedback.class, id);

        if (feedback != null) {
            session.remove(feedback);
        } else {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + id);
        }
    }

}
