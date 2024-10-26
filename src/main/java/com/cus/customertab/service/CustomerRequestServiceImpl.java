package com.cus.customertab.service;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.mapper.CustomerRequestMapper;
import org.hibernate.query.Query;

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

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getRequestFilters(ServiceType serviceType, Gender gender,
            String area, Integer pincode, String locality, String apartment_name) {

        Session session = sessionFactory.getCurrentSession();

        StringBuilder queryBuilder = new StringBuilder("FROM CustomerRequest cr WHERE 1=1");

        // Append conditions based on non-null parameters
        if (serviceType != null) {
            queryBuilder.append(" AND cr.serviceType = :serviceType");
        }
        if (gender != null) {
            queryBuilder.append(" AND cr.gender = :gender");
        }
        if (area != null) {
            queryBuilder.append(" AND cr.area = :area");
        }
        if (pincode != null) {
            queryBuilder.append(" AND cr.pincode = :pincode");
        }
        if (locality != null) {
            queryBuilder.append(" AND cr.locality = :locality");
        }
        if (apartment_name != null) {
            queryBuilder.append(" AND cr.apartment_name = :apartment_name");
        }

        Query<CustomerRequest> query = session.createQuery(queryBuilder.toString(), CustomerRequest.class);

        // Set parameters for non-null values
        if (serviceType != null) {
            query.setParameter("serviceType", serviceType);
        }
        if (gender != null) {
            query.setParameter("gender", gender);
        }
        if (area != null) {
            query.setParameter("area", area);
        }
        if (pincode != null) {
            query.setParameter("pincode", pincode);
        }
        if (locality != null) {
            query.setParameter("locality", locality);
        }
        if (apartment_name != null) {
            query.setParameter("apartment_name", apartment_name);
        }

        // Execute query and map results to DTO
        return query.getResultList().stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

}
