package com.cus.customertab.service;

import java.util.List;

import com.cus.customertab.dto.ServiceProviderRequestDTO;



public interface ServiceProviderRequestService {
    // Retrieve all ServiceProviderRequestDTOs
    List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs();

    // Retrieve a single ServiceProviderRequestDTO by its ID
    ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id);

    // Save a new ServiceProviderRequestDTO (create a new service provider request)
    void saveServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO);

    // Update an existing ServiceProviderRequestDTO
    void updateServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO);

    // Delete a ServiceProviderRequestDTO by its ID
    void deleteServiceProviderRequestDTO(Long id);

}
