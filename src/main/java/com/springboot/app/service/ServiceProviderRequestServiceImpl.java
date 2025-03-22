package com.springboot.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.mapper.ServiceProviderRequestMapper;
import com.springboot.app.repository.ServiceProviderRequestRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

@Service
public class ServiceProviderRequestServiceImpl implements ServiceProviderRequestService {

        private static final Logger logger = LoggerFactory.getLogger(ServiceProviderRequestServiceImpl.class);

        private final ServiceProviderRequestRepository serviceProviderRequestRepository;
        private final ServiceProviderRequestMapper serviceProviderRequestMapper;

        @Autowired
        public ServiceProviderRequestServiceImpl(ServiceProviderRequestRepository serviceProviderRequestRepository,
                        ServiceProviderRequestMapper serviceProviderRequestMapper) {
                this.serviceProviderRequestRepository = serviceProviderRequestRepository;
                this.serviceProviderRequestMapper = serviceProviderRequestMapper;
        }

        @Override
        @Transactional
        public List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs(int page, int size) {
                logger.info("Fetching service provider requests with pagination - page: {}, size: {}", page, size);

                // Using pagination directly with Spring Data JPA
                Page<ServiceProviderRequest> requestsPage = serviceProviderRequestRepository
                                .findAll(PageRequest.of(page, size));
                logger.debug("Fetched {} request(s) from the database.", requestsPage.getSize());

                return requestsPage.stream()
                                .map(serviceProviderRequestMapper::serviceProviderRequestToDTO)
                                .toList();
        }

        @Override
        @Transactional
        public ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id) {
                logger.info("Fetching service provider request with ID: {}", id);

                ServiceProviderRequest request = serviceProviderRequestRepository.findById(id)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
                                                                                + id));

                logger.debug("Found service provider request: {}", request);
                return serviceProviderRequestMapper.serviceProviderRequestToDTO(request);
        }

        @Override
        @Transactional
        public void saveServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
                logger.info("Saving a new service provider request");

                // Map the DTO to Entity and save it using JPA
                ServiceProviderRequest request = serviceProviderRequestMapper
                                .dtoToServiceProviderRequest(serviceProviderRequestDTO);
                serviceProviderRequestRepository.save(request); // Save with JPA repository
                logger.debug("Service provider request saved: {}", request);
        }

        @Override
        @Transactional
        public void updateServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO) {
                logger.info("Updating service provider request with ID: {}", serviceProviderRequestDTO.getRequestId());

                // Fetch the existing request from JPA
                ServiceProviderRequest existingRequest = serviceProviderRequestRepository
                                .findById(serviceProviderRequestDTO.getRequestId())
                                .orElseThrow(() -> new RuntimeException(
                                                ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
                                                                + serviceProviderRequestDTO.getRequestId()));

                // Map the DTO to Entity and update it using JPA
                ServiceProviderRequest updatedRequest = serviceProviderRequestMapper
                                .dtoToServiceProviderRequest(serviceProviderRequestDTO);
                updatedRequest.setRequestId(existingRequest.getRequestId()); // Preserve the original ID
                serviceProviderRequestRepository.save(updatedRequest); // Save the updated entity
                logger.debug("Service provider request updated: {}", updatedRequest);
        }

        @Override
        @Transactional
        public void deleteServiceProviderRequestDTO(Long id) {
                logger.info("Deleting (resolving) service provider request with ID: {}", id);

                ServiceProviderRequest request = serviceProviderRequestRepository.findById(id)
                                .orElseThrow(
                                                () -> new RuntimeException(
                                                                ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_NOT_FOUND
                                                                                + id));

                // Mark the request as resolved and save the update
                request.setIsResolved(ServiceProviderConstants.REQUEST_RESOLVED);
                serviceProviderRequestRepository.save(request); // Save the updated entity
                logger.debug("Service provider request with ID {} marked as resolved", id);
        }
}
