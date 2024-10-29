package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.config.PaginationHelper;
import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.mapper.ServiceProviderRequestCommentMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderRequestCommentServiceImpl implements ServiceProviderRequestCommentService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderRequestCommentServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderRequestCommentMapper serviceProviderRequestCommentMapper;

    @Override
    @Transactional
    public List<ServiceProviderRequestCommentDTO> getAllServiceProviderRequestComments(int page, int size) {
        logger.info("Fetching service provider request comments with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();

        // Using PaginationHelper to get paginated results
        List<ServiceProviderRequestComment> requests = PaginationHelper.getPaginatedResults(
                session,
                "FROM ServiceProviderRequestComment",
                page,
                size,
                ServiceProviderRequestComment.class);

        logger.debug("Fetched {} comments from the database.", requests.size());
        return requests.stream()
                .map(serviceProviderRequestCommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderRequestCommentDTO getServiceProviderRequestCommentById(Long id) {
        logger.info("Fetching service provider request comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment request = session.get(ServiceProviderRequestComment.class, id);
        if (request == null) {
            logger.warn("Request comment with ID {} not found", id);
            throw new RuntimeException("Request not found with ID: " + id);
        }
        logger.debug("Found request comment: {}", request);
        return serviceProviderRequestCommentMapper.toDTO(request);
    }

    @Override
    @Transactional
    public void saveServiceProviderRequestComment(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        logger.info("Saving service provider request comment for request ID: {}",
                serviceProviderRequestCommentDTO.getRequestId());
        Session session = sessionFactory.getCurrentSession();
        if (serviceProviderRequestCommentDTO.getRequestId() == null) {
            logger.error("ServiceProviderRequestId is required");
            throw new IllegalArgumentException("ServiceProviderRequestId is required.");
        }
        ServiceProviderRequest serviceProviderRequest = session.get(ServiceProviderRequest.class,
                serviceProviderRequestCommentDTO.getRequestId());

        if (serviceProviderRequest == null) {
            logger.error("ServiceProviderRequest with ID {} does not exist",
                    serviceProviderRequestCommentDTO.getRequestId());
            throw new IllegalArgumentException(
                    "ServiceProviderRequest with ID " + serviceProviderRequestCommentDTO.getRequestId()
                            + " does not exist.");
        }
        ServiceProviderRequestComment comment = serviceProviderRequestCommentMapper
                .toEntity(serviceProviderRequestCommentDTO);
        serviceProviderRequest.getComments().add(comment);
        comment.setServiceProviderRequest(serviceProviderRequest);
        session.persist(comment);
        logger.debug("Service provider request comment saved: {}", comment);
    }

    @Override
    @Transactional
    public void updateServiceProviderRequestComment(Long id,
            ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        logger.info("Updating service provider request comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment existingComment = session.get(ServiceProviderRequestComment.class, id);
        if (existingComment == null) {
            logger.warn("Service provider request comment with ID {} not found for update", id);
            throw new RuntimeException("Request comment not found with ID: " + id);
        }
        ServiceProviderRequestComment updatedComment = serviceProviderRequestCommentMapper
                .toEntity(serviceProviderRequestCommentDTO);
        updatedComment.setId(existingComment.getId());
        session.merge(updatedComment);
        logger.debug("Service provider request comment updated: {}", updatedComment);
    }

    @Override
    @Transactional
    public void deleteServiceProviderRequestComment(Long id) {
        logger.info("Deleting service provider request comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequestComment existingComment = session.get(ServiceProviderRequestComment.class, id);
        if (existingComment != null) {
            session.remove(existingComment);
            logger.debug("Service provider request comment with ID {} deleted", id);
        } else {
            logger.warn("Service provider request comment with ID {} not found for deletion", id);
        }
    }
}

/*
 * package com.springboot.app.service;
 * 
 * import java.util.List;
 * import java.util.stream.Collectors;
 * import org.hibernate.Session;
 * import org.hibernate.SessionFactory;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.stereotype.Service;
 * import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
 * import com.springboot.app.entity.ServiceProviderRequest;
 * import com.springboot.app.entity.ServiceProviderRequestComment;
 * import com.springboot.app.mapper.ServiceProviderRequestCommentMapper;
 * 
 * import jakarta.transaction.Transactional;
 * 
 * @Service
 * public class ServiceProviderRequestCommentServiceImpl implements
 * ServiceProviderRequestCommentService {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(ServiceProviderRequestCommentServiceImpl.class);
 * 
 * @Autowired
 * private SessionFactory sessionFactory;
 * 
 * @Autowired
 * private ServiceProviderRequestCommentMapper
 * serviceProviderRequestCommentMapper;
 * 
 * @Override
 * 
 * @Transactional
 * public List<ServiceProviderRequestCommentDTO>
 * getAllServiceProviderRequestComments() {
 * Session session = sessionFactory.getCurrentSession();
 * List<ServiceProviderRequestComment> requests = session
 * .createQuery("FROM ServiceProviderRequestComment",
 * ServiceProviderRequestComment.class).list();
 * return requests.stream()
 * .map(serviceProviderRequestCommentMapper::toDTO)
 * .collect(Collectors.toList());
 * }
 * 
 * @Override
 * 
 * @Transactional
 * public ServiceProviderRequestCommentDTO
 * getServiceProviderRequestCommentById(Long id) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequestComment request =
 * session.get(ServiceProviderRequestComment.class, id);
 * if (request == null) {
 * throw new RuntimeException("Request not found with ID: " + id);
 * }
 * return serviceProviderRequestCommentMapper.toDTO(request);
 * }
 * 
 * // To add a new request comment
 * 
 * @Override
 * 
 * @Transactional
 * public void
 * saveServiceProviderRequestComment(ServiceProviderRequestCommentDTO
 * serviceProviderRequestCommentDTO) {
 * Session session = sessionFactory.getCurrentSession();
 * if (serviceProviderRequestCommentDTO.getRequestId() == null) {
 * throw new IllegalArgumentException("ServiceProviderRequestId is required.");
 * }
 * ServiceProviderRequest serviceProviderRequest =
 * session.get(ServiceProviderRequest.class,
 * serviceProviderRequestCommentDTO.getRequestId());
 * 
 * if (serviceProviderRequest == null) {
 * throw new IllegalArgumentException(
 * "ServiceProviderRequest with ID " +
 * serviceProviderRequestCommentDTO.getRequestId()
 * + " does not exist.");
 * }
 * ServiceProviderRequestComment comment = serviceProviderRequestCommentMapper
 * .toEntity(serviceProviderRequestCommentDTO);
 * serviceProviderRequest.getComments().add(comment);
 * comment.setServiceProviderRequest(serviceProviderRequest);
 * session.persist(comment);
 * 
 * }
 * 
 * // To update a request comment
 * 
 * @Override
 * 
 * @Transactional
 * public void updateServiceProviderRequestComment(Long id,
 * ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequestComment existingComment =
 * session.get(ServiceProviderRequestComment.class, id);
 * ServiceProviderRequestComment updatedComment =
 * serviceProviderRequestCommentMapper
 * .toEntity(serviceProviderRequestCommentDTO);
 * if (existingComment != null) {
 * updatedComment.setId(existingComment.getId());
 * session.merge(updatedComment);
 * }
 * 
 * }
 * 
 * // To delete a request comment
 * 
 * @Override
 * 
 * @Transactional
 * public void deleteServiceProviderRequestComment(Long id) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequestComment existingComment =
 * session.get(ServiceProviderRequestComment.class, id);
 * if (existingComment != null) {
 * session.remove(existingComment);
 * }
 * 
 * }
 * 
 * }
 */