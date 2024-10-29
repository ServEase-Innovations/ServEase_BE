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
import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.mapper.ServiceProviderRequestMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderRequestServiceImpl implements ServiceProviderRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderRequestServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderRequestMapper serviceProviderRequestMapper; // Inject request mapper

    @Override
    @Transactional
    public List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs(int page, int size) {
        logger.info("Fetching service provider requests with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();

        // Using PaginationHelper to get paginated results
        List<ServiceProviderRequest> requests = PaginationHelper.getPaginatedResults(
                session,
                "FROM ServiceProviderRequest", // Use uppercase for consistency with HQL
                page,
                size,
                ServiceProviderRequest.class);

        logger.debug("Fetched {} request(s) from the database.", requests.size());

        return requests.stream()
                .map(serviceProviderRequestMapper::serviceProviderRequestToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id) {
        logger.info("Fetching service provider request with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = session.get(ServiceProviderRequest.class, id);

        if (request == null) {
            logger.warn("Service provider request with ID {} not found", id);
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND + id);
        }

        logger.debug("Found service provider request: {}", request);
        return serviceProviderRequestMapper.serviceProviderRequestToDTO(request);
    }

    @Override
    @Transactional
    public void saveServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
        logger.info("Saving a new service provider request");
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = serviceProviderRequestMapper
                .dtoToServiceProviderRequest(serviceProviderRequestDTO);
        session.persist(request);
        logger.debug("Service provider request saved: {}", request);
    }

    @Override
    @Transactional
    public void updateServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
        logger.info("Updating service provider request with ID: {}", serviceProviderRequestDTO.getRequestId());
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest existingRequest = session.get(ServiceProviderRequest.class,
                serviceProviderRequestDTO.getRequestId());

        if (existingRequest == null) {
            logger.warn("Service provider request with ID {} not found for update",
                    serviceProviderRequestDTO.getRequestId());
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
                    + serviceProviderRequestDTO.getRequestId());
        }

        existingRequest = serviceProviderRequestMapper.dtoToServiceProviderRequest(serviceProviderRequestDTO);
        session.merge(existingRequest);
        logger.debug("Service provider request updated: {}", existingRequest);
    }

    @Override
    @Transactional
    public void deleteServiceProviderRequestDTO(Long id) {
        logger.info("Deleting (resolving) service provider request with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderRequest request = session.get(ServiceProviderRequest.class, id);

        if (request != null) {
            request.setIsResolved(ServiceProviderConstants.REQUEST_RESOLVED);
            session.merge(request);
            logger.debug("Service provider request with ID {} marked as resolved", id);
        } else {
            logger.warn("Service provider request with ID {} not found for deletion", id);
            throw new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND + id);
        }
    }
}

/*
 * package com.springboot.app.service;
 * 
 * import java.util.List;
 * import java.util.stream.Collectors;
 * 
 * import org.hibernate.Session;
 * import org.hibernate.SessionFactory;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.stereotype.Service;
 * 
 * import com.springboot.app.constant.ServiceProviderConstants;
 * import com.springboot.app.dto.ServiceProviderRequestDTO;
 * import com.springboot.app.entity.ServiceProviderRequest;
 * import com.springboot.app.mapper.ServiceProviderRequestMapper;
 * 
 * import jakarta.transaction.Transactional;
 * 
 * @Service
 * public class ServiceProviderRequestServiceImpl implements
 * ServiceProviderRequestService {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(ServiceProviderRequestServiceImpl.class);
 * 
 * @Autowired
 * private SessionFactory sessionFactory;
 * 
 * @Autowired
 * private ServiceProviderRequestMapper serviceProviderRequestMapper; // Inject
 * request mapper
 * 
 * @Override
 * 
 * @Transactional
 * public List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs() {
 * Session session = sessionFactory.getCurrentSession();
 * List<ServiceProviderRequest> requests = session
 * .createQuery("from ServiceProviderRequest",
 * ServiceProviderRequest.class).list();
 * 
 * return requests.stream()
 * .map(serviceProviderRequestMapper::serviceProviderRequestToDTO)
 * .collect(Collectors.toList());
 * }
 * 
 * @Override
 * 
 * @Transactional
 * public ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequest request = session.get(ServiceProviderRequest.class,
 * id);
 * 
 * if (request == null) {
 * throw new
 * RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
 * + id);
 * }
 * 
 * return serviceProviderRequestMapper.serviceProviderRequestToDTO(request);
 * }
 * 
 * @Override
 * 
 * @Transactional
 * public void saveServiceProviderRequestDTO(ServiceProviderRequestDTO
 * serviceProviderRequestDTO) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequest request = serviceProviderRequestMapper
 * .dtoToServiceProviderRequest(serviceProviderRequestDTO);
 * session.persist(request);
 * }
 * 
 * @Override
 * 
 * @Transactional
 * public void updateServiceProviderRequestDTO(ServiceProviderRequestDTO
 * serviceProviderRequestDTO) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequest existingRequest =
 * session.get(ServiceProviderRequest.class,
 * serviceProviderRequestDTO.getRequestId());
 * 
 * if (existingRequest == null) {
 * throw new
 * RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
 * + serviceProviderRequestDTO.getRequestId());
 * }
 * 
 * existingRequest = serviceProviderRequestMapper.dtoToServiceProviderRequest(
 * serviceProviderRequestDTO);
 * session.merge(existingRequest);
 * }
 * 
 * @Override
 * 
 * @Transactional
 * public void deleteServiceProviderRequestDTO(Long id) {
 * Session session = sessionFactory.getCurrentSession();
 * ServiceProviderRequest request = session.get(ServiceProviderRequest.class,
 * id);
 * 
 * if (request != null) {
 * request.setIsResolved(ServiceProviderConstants.REQUEST_RESOLVED);
 * session.merge(request);
 * } else {
 * throw new
 * RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
 * + id);
 * }
 * }
 * 
 * }
 */
