package com.cus.customertab.service;

import com.cus.customertab.dto.CustomerConcernDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerConcernService {

    ResponseEntity<List<CustomerConcernDTO>> getAllConcerns();
    ResponseEntity<CustomerConcernDTO> getConcernById(Long id);
    ResponseEntity<String> addNewConcern(CustomerConcernDTO customerConcernDTO);
    ResponseEntity<String> modifyConcern(CustomerConcernDTO customerConcernDTO);
    ResponseEntity<String> deleteConcern(Long id);
}
