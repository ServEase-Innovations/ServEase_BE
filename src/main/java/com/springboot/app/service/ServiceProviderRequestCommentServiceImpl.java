package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.mapper.ServiceProviderRequestCommentMapper;
import com.springboot.app.repository.ServiceProviderRequestCommentRepository;
import com.springboot.app.repository.ServiceProviderRequestRepository;

@Service
public class ServiceProviderRequestCommentServiceImpl implements ServiceProviderRequestCommentService {

    @Autowired
    private ServiceProviderRequestCommentRepository serviceProviderRequestCommentRepository;

    @Autowired
    private ServiceProviderRequestRepository serviceProviderRequestRepository;

    @Autowired
    private ServiceProviderRequestCommentMapper serviceProviderRequestCommentMapper;

    @Override
    @Transactional
    public List<ServiceProviderRequestCommentDTO> getAllServiceProviderRequestComments(int page, int size) {
        // Pagination using Spring Data JPA
        List<ServiceProviderRequestComment> comments = serviceProviderRequestCommentRepository
                .findAll(PageRequest.of(page, size))
                .getContent();

        return comments.stream()
                .map(serviceProviderRequestCommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderRequestCommentDTO getServiceProviderRequestCommentById(Long id) {
        ServiceProviderRequestComment comment = serviceProviderRequestCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request comment not found with ID: " + id));
        return serviceProviderRequestCommentMapper.toDTO(comment);
    }

    @Override
    @Transactional
    public void saveServiceProviderRequestComment(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        ServiceProviderRequest serviceProviderRequest = serviceProviderRequestRepository
                .findById(serviceProviderRequestCommentDTO.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("ServiceProviderRequest with ID "
                        + serviceProviderRequestCommentDTO.getRequestId() + " does not exist."));

        ServiceProviderRequestComment comment = serviceProviderRequestCommentMapper
                .toEntity(serviceProviderRequestCommentDTO);
        comment.setServiceProviderRequest(serviceProviderRequest);
        serviceProviderRequest.getComments().add(comment);

        serviceProviderRequestCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateServiceProviderRequestComment(Long id,
            ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
        // Find existing comment by ID
        ServiceProviderRequestComment existingComment = serviceProviderRequestCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request comment not found with ID: " + id));

        // Use the mapper to update the entity from the DTO
        serviceProviderRequestCommentMapper.updateEntityFromDTO(serviceProviderRequestCommentDTO, existingComment);

        // Save the updated entity (merge ensures that it updates the existing entity in
        // the DB)
        serviceProviderRequestCommentRepository.save(existingComment); // or merge if using JPA directly
    }

    @Override
    @Transactional
    public void deleteServiceProviderRequestComment(Long id) {
        ServiceProviderRequestComment existingComment = serviceProviderRequestCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request comment not found with ID: " + id));

        serviceProviderRequestCommentRepository.delete(existingComment);
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