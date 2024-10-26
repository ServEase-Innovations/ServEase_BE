package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.CustomerDTO;

public interface CustomerService {

    // List<CustomerDTO> getAllCustomers();
    List<CustomerDTO> getAllCustomers(int page, int size);
    CustomerDTO getCustomerById(Long id);
    String saveCustomer(CustomerDTO customerDTO);
    String updateCustomer(CustomerDTO customerDTO);
    String deleteCustomer(Long id);

}
