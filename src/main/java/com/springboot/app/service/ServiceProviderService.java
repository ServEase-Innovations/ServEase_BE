package com.springboot.app.service;

import java.util.List;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.dto.ServiceProviderRequestDTO;

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
