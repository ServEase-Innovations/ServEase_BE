package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.Status;

public interface CustomerRequestService {
    List<CustomerRequestDTO> getAll(int page, int size);

    CustomerRequestDTO getByRequestId(Long requestId);

    List<CustomerRequestDTO> getAllOpenRequests(int page, int size);

    List<CustomerRequestDTO> findAllPotentialCustomers(int page, int size);

    String insert(CustomerRequestDTO customerRequestDTO);

    String update(CustomerRequestDTO customerRequestDTO);

    void updateStatus(Long requestId, Status status);

    List<CustomerRequestDTO> getRequestFilters(
            HousekeepingRole housekeepingRole, Gender gender,
            String area, Integer pincode, String locality, String apartment_name, int page, int size);
}
