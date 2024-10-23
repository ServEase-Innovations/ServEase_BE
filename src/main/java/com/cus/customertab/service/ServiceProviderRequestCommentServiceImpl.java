package com.cus.customertab.service;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cus.customertab.dto.ServiceProviderRequestCommentDTO;
import com.cus.customertab.entity.ServiceProviderRequest;
import com.cus.customertab.entity.ServiceProviderRequestComment;
import com.cus.customertab.mapper.ServiceProviderRequestCommentMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderRequestCommentServiceImpl implements ServiceProviderRequestCommentService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderRequestCommentMapper serviceProviderRequestCommentMapper;

    @Override
    @Transactional
    public List<ServiceProviderRequestCommentDTO> getAllServiceProviderRequestComments() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProviderRequestComment> requests = session
                .createQuery("FROM ServiceProviderRequestComment", ServiceProviderRequestComment.class).list();
        return requests.stream()
                .map(serviceProviderRequestCommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderRequestCommentDTO getServiceProviderRequestCommentById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment request = session.get(ServiceProviderRequestComment.class, id);
        if (request == null) {
            throw new RuntimeException("Request not found with ID: " + id);
        }
        return serviceProviderRequestCommentMapper.toDTO(request);
    }

    // To add a new request comment
    @Override
    @Transactional
    public void saveServiceProviderRequestComment(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        Session session = sessionFactory.getCurrentSession();
        if (serviceProviderRequestCommentDTO.getRequestId() == null) {
            throw new IllegalArgumentException("ServiceProviderRequestId is required.");
        }
        ServiceProviderRequest serviceProviderRequest = session.get(ServiceProviderRequest.class,
                serviceProviderRequestCommentDTO.getRequestId());

        if (serviceProviderRequest == null) {
            throw new IllegalArgumentException(
                    "ServiceProviderRequest with ID " + serviceProviderRequestCommentDTO.getRequestId()
                            + " does not exist.");
        }
        ServiceProviderRequestComment comment = serviceProviderRequestCommentMapper
                .toEntity(serviceProviderRequestCommentDTO);
        serviceProviderRequest.getComments().add(comment);
        comment.setServiceProviderRequest(serviceProviderRequest);
        session.persist(comment);

    }

    // To update a request comment
    @Override
    @Transactional
    public void updateServiceProviderRequestComment(Long id,
            ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment existingComment = session.get(ServiceProviderRequestComment.class, id);
        ServiceProviderRequestComment updatedComment = serviceProviderRequestCommentMapper
                .toEntity(serviceProviderRequestCommentDTO);
        if (existingComment != null) {
            updatedComment.setId(existingComment.getId());
            session.merge(updatedComment);
        }

    }

    // To delete a request comment
    @Override
    @Transactional
    public void deleteServiceProviderRequestComment(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment existingComment = session.get(ServiceProviderRequestComment.class, id);
        if (existingComment != null) {
            session.remove(existingComment);
        }

    }

}