package com.cus.customertab.service;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;
import com.cus.customertab.mapper.CustomerRequestMapper;

@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    // To get all requests
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CustomerRequestDTO>> getAll() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> requests = session.createQuery(CustomerConstants.GET_ALL_CUSTOMER_REQUESTS, CustomerRequest.class).getResultList();
        List<CustomerRequestDTO> dtoList = requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList(); // Map each CustomerRequest to CustomerRequestDTO
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // To get request by id
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerRequestDTO> getByRequestId(Long requestId) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = session.get(CustomerRequest.class, requestId);
        
        if (request != null) {
            return new ResponseEntity<>(customerRequestMapper.customerRequestToDTO(request), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // To fetch all open requests
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CustomerRequestDTO>> getAllOpenRequests() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> openRequests = session.createQuery(CustomerConstants.GET_OPEN_CUSTOMER_REQUESTS, CustomerRequest.class).getResultList();
        List<CustomerRequestDTO> dtoList = openRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // To fetch all potential customers
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CustomerRequestDTO>> findAllPotentialCustomers() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> potentialCustomers = session.createQuery(CustomerConstants.GET_POTENTIAL_CUSTOMERS, CustomerRequest.class).getResultList();
        List<CustomerRequestDTO> dtoList = potentialCustomers.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // To add a new request
    @Override
    @Transactional
    public ResponseEntity<String> insert(CustomerRequestDTO customerRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        session.persist(request);
        return new ResponseEntity<>(CustomerConstants.CUSTOMER_REQUEST_ADDED, HttpStatus.CREATED);
    }

    // To update
    @Override
    @Transactional
    public ResponseEntity<String> update(CustomerRequestDTO customerRequestDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest existingRequest = session.get(CustomerRequest.class, customerRequestDTO.getRequestId());

        if (existingRequest == null) {
            return new ResponseEntity<>(CustomerConstants.CUSTOMER_REQUEST_UPDATED , HttpStatus.OK);
        }
        // Use the mapper to map updated fields
        CustomerRequest updatedRequest = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        // Set the ID of the existing request to the new request to ensure it updates the correct entity
        updatedRequest.setRequestId(existingRequest.getRequestId());
        session.merge(updatedRequest);

        return new ResponseEntity<>(CustomerConstants.CUSTOMER_REQUEST_UPDATED, HttpStatus.OK);
    }

}
