package com.springboot.app.service;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.mapper.CustomerRequestMapper;

@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    // To get all customer requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAll() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> requests = session
                .createQuery(CustomerConstants.GET_ALL_CUSTOMER_REQUESTS, CustomerRequest.class).getResultList();
        return requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    // To get a customer request by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestDTO getByRequestId(Long requestId) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = session.get(CustomerRequest.class, requestId);
        return customerRequestMapper.customerRequestToDTO(request);
    }

    // To get all open requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAllOpenRequests() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> openRequests = session
                .createQuery(CustomerConstants.GET_OPEN_CUSTOMER_REQUESTS, CustomerRequest.class).getResultList();
        return openRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    // To find all potential customers
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> findAllPotentialCustomers() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> potentialCustomers = session
                .createQuery(CustomerConstants.GET_POTENTIAL_CUSTOMERS, CustomerRequest.class).getResultList();
        return potentialCustomers.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    // To add a new customer request
    @Override
    @Transactional
    public String insert(CustomerRequestDTO customerRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        session.persist(request);
        return CustomerConstants.ADDED;
    }

    // To update a customer request
    @Override
    @Transactional
    public String update(CustomerRequestDTO customerRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest existingRequest = session.get(CustomerRequest.class, customerRequestDTO.getRequestId());
        CustomerRequest updatedRequest = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        updatedRequest.setRequestId(existingRequest.getRequestId());
        session.merge(updatedRequest);
        return CustomerConstants.UPDATED;
    }
}
