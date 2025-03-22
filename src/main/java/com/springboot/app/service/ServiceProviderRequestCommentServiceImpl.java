package com.springboot.app.service;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.entity.ServiceProviderRequest;
import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.mapper.ServiceProviderRequestCommentMapper;
import com.springboot.app.repository.ServiceProviderRequestCommentRepository;
import com.springboot.app.repository.ServiceProviderRequestRepository;

@Service
public class ServiceProviderRequestCommentServiceImpl implements ServiceProviderRequestCommentService {

        private final ServiceProviderRequestCommentRepository serviceProviderRequestCommentRepository;
        private final ServiceProviderRequestRepository serviceProviderRequestRepository;
        private final ServiceProviderRequestCommentMapper serviceProviderRequestCommentMapper;

        @Autowired
        public ServiceProviderRequestCommentServiceImpl(
                        ServiceProviderRequestCommentRepository serviceProviderRequestCommentRepository,
                        ServiceProviderRequestRepository serviceProviderRequestRepository,
                        ServiceProviderRequestCommentMapper serviceProviderRequestCommentMapper) {
                this.serviceProviderRequestCommentRepository = serviceProviderRequestCommentRepository;
                this.serviceProviderRequestRepository = serviceProviderRequestRepository;
                this.serviceProviderRequestCommentMapper = serviceProviderRequestCommentMapper;
        }

        @Override
        @Transactional
        public List<ServiceProviderRequestCommentDTO> getAllServiceProviderRequestComments(int page, int size) {
                // Pagination using Spring Data JPA
                List<ServiceProviderRequestComment> comments = serviceProviderRequestCommentRepository
                                .findAll(PageRequest.of(page, size))
                                .getContent();

                return comments.stream()
                                .map(serviceProviderRequestCommentMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional
        public ServiceProviderRequestCommentDTO getServiceProviderRequestCommentById(Long id) {
                ServiceProviderRequestComment comment = serviceProviderRequestCommentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                ServiceProviderConstants.COMMENT_NOT_FOUND_MSG + id));
                return serviceProviderRequestCommentMapper.toDTO(comment);
        }

        @Override
        @Transactional
        public void saveServiceProviderRequestComment(
                        ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO) {
                ServiceProviderRequest serviceProviderRequest = serviceProviderRequestRepository
                                .findById(serviceProviderRequestCommentDTO.getRequestId())
                                .orElseThrow(() -> new IllegalArgumentException("ServiceProviderRequest with ID "
                                                + serviceProviderRequestCommentDTO.getRequestId()
                                                + " does not exist."));

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
                serviceProviderRequestCommentMapper.updateEntityFromDTO(serviceProviderRequestCommentDTO,
                                existingComment);

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