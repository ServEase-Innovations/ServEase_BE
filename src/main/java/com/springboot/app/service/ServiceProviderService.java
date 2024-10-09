package com.springboot.app.service;

import java.util.List;

import com.springboot.app.dto.ServiceProviderDTO;

public interface ServiceProviderService {
  // Retrieve all ServiceProviderDTOs
  List<ServiceProviderDTO> getAllServiceProviderDTOs();

  // Retrieve a single ServiceProviderDTO by its ID
  ServiceProviderDTO getServiceProviderDTOById(Long id);

  // Save a new ServiceProviderDTO (create a new service provider)
  void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Update an existing ServiceProviderDTO
  void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Delete a ServiceProviderDTO by its ID
  void deleteServiceProviderDTO(Long id);

}