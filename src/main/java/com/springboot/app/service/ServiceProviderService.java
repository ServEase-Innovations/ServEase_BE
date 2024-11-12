package com.springboot.app.service;

import java.util.List;

import com.springboot.app.dto.ServiceProviderDTO;

import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

public interface ServiceProviderService {
  // Retrieve all ServiceProviderDTOs
  List<ServiceProviderDTO> getAllServiceProviderDTOs(int page, int size);

  // Retrieve a single ServiceProviderDTO by its ID
  ServiceProviderDTO getServiceProviderDTOById(Long id);

  // Save a new ServiceProviderDTO (create a new service provider)
  void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Update an existing ServiceProviderDTO
  void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Delete a ServiceProviderDTO by its ID
  void deleteServiceProviderDTO(Long id);

  List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender, Speciality speciality,
      HousekeepingRole housekeepingRole, Integer minAge, Integer maxAge);

  List<ServiceProviderDTO> getServiceProvidersByFilter(Integer pincode, String street, String locality);

  List<ServiceProviderDTO> getServiceProvidersByOrFilter(Integer pincode, String street, String locality);

}