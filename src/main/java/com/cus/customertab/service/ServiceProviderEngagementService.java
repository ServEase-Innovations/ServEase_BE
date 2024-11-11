package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.ServiceProviderEngagementDTO;

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
}