package com.springboot.app.service;

import java.util.List;

import com.springboot.app.dto.ServiceProviderRequestCommentDTO;

public interface ServiceProviderRequestCommentService {
    List<ServiceProviderRequestCommentDTO> getAllServiceProviderRequestComments();

    ServiceProviderRequestCommentDTO getServiceProviderRequestCommentById(Long id);

    void saveServiceProviderRequestComment(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO);

    void updateServiceProviderRequestComment(Long id,
            ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO);

    void deleteServiceProviderRequestComment(Long id);
}
