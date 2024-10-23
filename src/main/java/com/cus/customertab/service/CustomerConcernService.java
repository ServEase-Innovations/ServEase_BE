package com.cus.customertab.service;

import com.cus.customertab.dto.CustomerConcernDTO;
import java.util.List;

public interface CustomerConcernService {
    List<CustomerConcernDTO> getAllConcerns();
    CustomerConcernDTO getConcernById(Long id);
    String addNewConcern(CustomerConcernDTO customerConcernDTO);
    String modifyConcern(CustomerConcernDTO customerConcernDTO);
    String deleteConcern(Long id);
}