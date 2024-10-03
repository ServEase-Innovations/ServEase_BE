package com.cus.customertab.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.cus.customertab.dto.CustomerRequestDTO;

public interface CustomerRequestService {
    ResponseEntity<List<CustomerRequestDTO>> getAll();
    ResponseEntity<CustomerRequestDTO> getByRequestId(Long requestId);
    ResponseEntity<List<CustomerRequestDTO>> getAllOpenRequests();
    ResponseEntity<List<CustomerRequestDTO>> findAllPotentialCustomers();
    ResponseEntity<String> insert(CustomerRequestDTO customerRequestDTO);
    ResponseEntity<String> update(CustomerRequestDTO customerRequestDTO);
}
