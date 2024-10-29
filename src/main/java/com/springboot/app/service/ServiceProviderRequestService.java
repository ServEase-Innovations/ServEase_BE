package com.springboot.app.service;

import java.util.List;

import com.springboot.app.dto.ServiceProviderRequestDTO;

public interface ServiceProviderRequestService {
    // Retrieve all ServiceProviderRequestDTOs
    List<ServiceProviderRequestDTO> getAllServiceProviderRequestDTOs(int page, int size);

    // Retrieve a single ServiceProviderRequestDTO by its ID
    ServiceProviderRequestDTO getServiceProviderRequestDTOById(Long id);

    // Save a new ServiceProviderRequestDTO (create a new service provider request)
    void saveServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO);

    // Update an existing ServiceProviderRequestDTO
    void updateServiceProviderRequestDTO(ServiceProviderRequestDTO serviceProviderRequestDTO);

    // Delete a ServiceProviderRequestDTO by its ID
    void deleteServiceProviderRequestDTO(Long id);

}
