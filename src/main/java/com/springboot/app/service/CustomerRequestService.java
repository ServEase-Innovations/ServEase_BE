package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.CustomerRequestDTO;

public interface CustomerRequestService {
    List<CustomerRequestDTO> getAll();

    CustomerRequestDTO getByRequestId(Long requestId);

    List<CustomerRequestDTO> getAllOpenRequests();

    List<CustomerRequestDTO> findAllPotentialCustomers();

    String insert(CustomerRequestDTO customerRequestDTO);

    String update(CustomerRequestDTO customerRequestDTO);
}
