package com.cus.customertab.service;

import java.util.List;

import com.cus.customertab.dto.ServiceProviderDTO;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.enums.Speciality;



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

  List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender,
      Speciality speciality, ServiceType housekeepingRole, Integer minAge, Integer maxAge);

}