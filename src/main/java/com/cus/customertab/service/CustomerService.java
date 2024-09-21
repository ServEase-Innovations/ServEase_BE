package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.entity.Customer;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void saveCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(Long id);
}
