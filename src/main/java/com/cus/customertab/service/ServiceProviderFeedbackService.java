package com.cus.customertab.service;

import java.util.List;

import com.cus.customertab.dto.ServiceProviderFeedbackDTO;



public interface ServiceProviderFeedbackService {

    // Retrieve all ServiceProviderFeedbackDTOs
    List<ServiceProviderFeedbackDTO> getAllServiceProviderFeedbackDTOs();

    // Retrieve a single ServiceProviderFeedbackDTO by its ID
    ServiceProviderFeedbackDTO getServiceProviderFeedbackDTOById(Long id);

    // Save a new ServiceProviderFeedbackDTO (create new feedback)
    void saveServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO);

    // Update an existing ServiceProviderFeedbackDTO
    void updateServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO);

    // Delete a ServiceProviderFeedbackDTO by its ID
    void deleteServiceProviderFeedbackDTO(Long id);

}
