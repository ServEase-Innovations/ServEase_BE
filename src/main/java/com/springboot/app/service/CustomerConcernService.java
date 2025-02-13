package com.springboot.app.service;

import com.springboot.app.dto.CustomerConcernDTO;
import java.util.List;

public interface CustomerConcernService {
    List<CustomerConcernDTO> getAllConcerns(int page, int size);

    CustomerConcernDTO getConcernById(Long id);

    String addNewConcern(CustomerConcernDTO customerConcernDTO);

    String modifyConcern(CustomerConcernDTO customerConcernDTO);

    String deleteConcern(Long id);
}