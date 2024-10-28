package com.cus.customertab.service;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.entity.Customer;
import com.cus.customertab.mapper.CustomerMapper;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerMapper customerMapper;


    //to get all customers
    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers(int page, int size) {
        logger.info("Fetching all customers with page: {} and size: {}",page, size);
        Session session = sessionFactory.getCurrentSession();
        List<Customer> customers = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_ALL_CUSTOMER,
                page,
                size,
                Customer.class
        );
        logger.debug("Number of customers fetched: {}", customers.size());
        return customers.stream()
                .map(customer -> {
                    CustomerDTO dto = customerMapper.customerToDTO(customer);
                    dto.setProfilePicUrl(customerMapper.mapToBase64(customer.getProfilePic()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO getCustomerById(Long id) {
        logger.info("Fetching customer by id: {}", id);
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);
        if (customer != null) {
            logger.debug("Customer found with id: {}", id);
            return customerMapper.customerToDTO(customer);
        }
        else {
            logger.error("Customer not found with id: {}", id);
            return null;
        }
    }

    //to add a new customer
    @Override
    @Transactional
    public String saveCustomer(CustomerDTO customerDTO) {
        logger.info("Saving a new customer: {}", customerDTO);
        Session session = sessionFactory.getCurrentSession();
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        customer.setActive(true);
        session.persist(customer);
        logger.info("Customer saved with id: {}", customer.getCustomerId());
        return CustomerConstants.ADDED;
    }

    //to update customer
    @Override
    @Transactional
    public String updateCustomer(CustomerDTO customerDTO) {
        logger.info("Updating customer with id: {}", customerDTO.getCustomerId());
        Session session = sessionFactory.getCurrentSession();
        Customer customer = customerMapper.dtoToCustomer(customerDTO);
        session.merge(customer);
        logger.info("Customer Updated with id: {}", customer.getCustomerId());
        return CustomerConstants.UPDATED;
    }

    //to delete customer
    @Override
    @Transactional
    public String deleteCustomer(Long id) {
        logger.info("Deleting customer with id: {}", id);
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);

        if (customer != null) {
            customer.setActive(false);
            session.merge(customer);
            logger.info("Customer deactivated successfully with id: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Customer not found for deletion with id: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }

}
