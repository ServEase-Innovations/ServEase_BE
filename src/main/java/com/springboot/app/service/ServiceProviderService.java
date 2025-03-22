package com.springboot.app.service;

import java.util.List;
import java.util.Map;

import com.springboot.app.dto.ServiceProviderDTO;

import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Habit;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
//import org.springframework.lang.Nullable;

public interface ServiceProviderService {
  // Retrieve all ServiceProviderDTOs
  List<ServiceProviderDTO> getAllServiceProviderDTOs(int page, int size, String location);

  // Retrieve a single ServiceProviderDTO by its ID
  ServiceProviderDTO getServiceProviderDTOById(Long id);

  // Retrieve all ServiceProviderDTO by its vendorId
  List<ServiceProviderDTO> getServiceProvidersByVendorId(Long vendorId);

  // Save a new ServiceProviderDTO (create a new service provider)
  void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Update an existing ServiceProviderDTO
  String updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO);

  // Delete a ServiceProviderDTO by its ID
  String deleteServiceProviderDTO(Long id);

  List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender, Speciality speciality,
      HousekeepingRole housekeepingRole, Integer minAge, Integer maxAge, String timeslot, Habit diet);

  List<ServiceProviderDTO> getServiceProvidersByFilter(Integer pincode, String street, String locality);

  List<ServiceProviderDTO> getServiceProvidersByOrFilter(Integer pincode, String street, String locality);

  Map<String, Object> calculateExpectedSalary(Long serviceProviderId);

  List<ServiceProviderDTO> getServiceProvidersByRole(HousekeepingRole role);

  String uploadExcelRecords(String filename);

  // List<String> calculateAvailableTimes(String timeslot);

  List<ServiceProviderDTO> findNearbyProviders(double latitude, double longitude, int precision);

}