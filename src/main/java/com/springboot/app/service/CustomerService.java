package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.CustomerDTO;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Long id);

    String saveCustomer(CustomerDTO customerDTO);

    String updateCustomer(CustomerDTO customerDTO);

    String deleteCustomer(Long id);
}
