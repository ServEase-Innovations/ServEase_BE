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

 
    //to get all customers
    @Override
    @Transactional
    public List<CustomerDTO> getAllCustomers() {
        Session session = sessionFactory.getCurrentSession();
        List<Customer> customers = session.createQuery(CustomerConstants.GET_ALL_CUSTOMER, Customer.class).list();

        return customers.stream()
                .map(customer -> {
                    CustomerDTO dto = customerMapper.customerToDTO(customer);
                    // Convert the profile picture from byte[] to Base64
                    dto.setProfilePicUrl(customerMapper.mapToBase64(customer.getProfilePic()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    //to get customer by id
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

    //to add a new customer
    @Override
    @Transactional
    public String saveCustomer(CustomerDTO customerDTO) {
        Session session = sessionFactory.getCurrentSession();

        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        customer.setActive(true);
        session.persist(customer);
        return CustomerConstants.ADDED;
    }

    //to update customer
    @Override
    @Transactional
    public String updateCustomer(CustomerDTO customerDTO) {
        Session session = sessionFactory.getCurrentSession();

        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        session.merge(customer);

        return CustomerConstants.UPDATED;
    }

    //to delete customer
    @Override
    @Transactional
    public String deleteCustomer(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);

        if (customer != null) {
            customer.setActive(false);
            session.merge(customer);
            return CustomerConstants.DELETED;
        } else {
            return CustomerConstants.NOT_FOUND;
        }
    }
    
}
