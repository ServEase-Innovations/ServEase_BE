package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.ServiceType;

public interface CustomerRequestService {
    List<CustomerRequestDTO> getAll(int page, int size);
    CustomerRequestDTO getByRequestId(Long requestId);
    List<CustomerRequestDTO> getAllOpenRequests(int page, int size);
    List<CustomerRequestDTO> findAllPotentialCustomers(int page, int size);
    String insert(CustomerRequestDTO customerRequestDTO);
    String update(CustomerRequestDTO customerRequestDTO);
    
    List<CustomerRequestDTO> getRequestFilters(ServiceType serviceType, Gender gender,
            String area, Integer pincode, String locality, String apartment_name, int page, int size);
}
