package com.springboot.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.UserRole;

public interface ServiceProviderEngagementService {

    // Get a list of all ServiceProviderEngagements
    List<ServiceProviderEngagementDTO> getAllServiceProviderEngagements(int page, int size);

    // Get a specific ServiceProviderEngagement by ID
    ServiceProviderEngagementDTO getServiceProviderEngagementById(Long id);

    // Add a new ServiceProviderEngagement
    String addServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO);

    // Update an existing ServiceProviderEngagement
    String updateServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO);

    // Delete a ServiceProviderEngagement by ID
    String deleteServiceProviderEngagement(Long id);

    // Get a specific ServiceProvider by ID
    List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByServiceProviderId(Long serviceProviderId);

    // Get a specific Customer by ID
    List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByCustomerId(Long customerId);

    public Map<String, List<ServiceProviderEngagementDTO>> getServiceProviderBookingHistory(int page, int size);

    List<ServiceProviderEngagementDTO> getEngagementsByExactDateTimeslotAndHousekeepingRole(
            LocalDate startDate, LocalDate endDate, String timeslot, HousekeepingRole housekeepingRole);

}
