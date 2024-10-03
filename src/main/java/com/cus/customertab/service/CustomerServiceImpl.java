package com.cus.customertab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.entity.Customer;
import com.cus.customertab.mapper.CustomerMapper;

import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    @Transactional
    public List<CustomerDTO> getAllCustomers() {
        Session session = sessionFactory.getCurrentSession();
        List<Customer> customers = session.createQuery("from Customer", Customer.class).list();
        
        // Mapping the list of Customer entities to a list of CustomerDTOs
        return customers.stream()
                        .map(customerMapper::customerToDTO)
                        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO getCustomerById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);
        
        if (customer != null) {
            return customerMapper.customerToDTO(customer);
        }
        return null;
    }

    @Override
    @Transactional
    public String saveCustomer(CustomerDTO customerDTO) {
        Session session = sessionFactory.getCurrentSession();
        
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        customer.setActive(true); 
        
        session.persist(customer);
        return CustomerConstants.CUSTOMER_ADDED;
    }

    @Override
    @Transactional
    public String updateCustomer(CustomerDTO customerDTO) {
        Session session = sessionFactory.getCurrentSession();
        
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        session.merge(customer);
        
        return CustomerConstants.CUSTOMER_UPDATED;
    }

    @Override
    @Transactional
    public String deleteCustomer(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);
        
        if (customer != null) {
            customer.setActive(false);  // Soft delete by deactivating the customer
            session.merge(customer);
            return CustomerConstants.CUSTOMER_DELETED;
        } else {
            return CustomerConstants.CUSTOMER_NOT_FOUND;
        }
    }
}
