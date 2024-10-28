package com.cus.customertab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.mapper.CustomerRequestMapper;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRequestServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    // To get all customer requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAll(int page, int size) {
        logger.info("Fetching all customer requests with page: {} and size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> requests = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_ALL_CUSTOMER_REQUESTS,
                page,
                size,
                CustomerRequest.class);
        logger.debug("Number of customer requests fetched: {}", requests.size());
        return requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    // To get a customer request by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestDTO getByRequestId(Long requestId) {
        logger.info("Fetching customer request by ID: {}", requestId);
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = session.get(CustomerRequest.class, requestId);
        if (request != null) {
            logger.debug("Customer request found with ID: {}", requestId);
            return customerRequestMapper.customerRequestToDTO(request);
        } else {
            logger.error("Customer request not found with ID: {}", requestId);
            return null;
        }
    }

    // To get all open requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAllOpenRequests(int page, int size) {
        logger.info("Fetching all open customer requests with page: {} and size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> openRequests = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_OPEN_CUSTOMER_REQUESTS,
                page,
                size,
                CustomerRequest.class);
        logger.debug("Number of open customer requests fetched: {}", openRequests.size());
        return openRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> findAllPotentialCustomers(int page, int size) {
        logger.info("Fetching all potential customers with page: {} and size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequest> potentialCustomers = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_POTENTIAL_CUSTOMERS,
                page,
                size,
                CustomerRequest.class);
        logger.debug("Number of potential customers fetched: {}", potentialCustomers.size());
        return potentialCustomers.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    // To add a new customer request
    @Override
    @Transactional
    public String insert(CustomerRequestDTO customerRequestDTO) {
        logger.info("Inserting new customer request: {}", customerRequestDTO);
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest request = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        session.persist(request);
        logger.info("Customer request inserted with ID: {}", request.getRequestId());
        return CustomerConstants.ADDED;
    }

    // To update a customer request
    @Override
    @Transactional
    public String update(CustomerRequestDTO customerRequestDTO) {
        logger.info("Updating customer request with ID: {}", customerRequestDTO.getRequestId());
        Session session = sessionFactory.getCurrentSession();
        CustomerRequest existingRequest = session.get(CustomerRequest.class, customerRequestDTO.getRequestId());
        if (existingRequest != null) {
            CustomerRequest updatedRequest = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
            updatedRequest.setRequestId(existingRequest.getRequestId());
            session.merge(updatedRequest);
            logger.info("Customer request updated successfully with ID: {}", customerRequestDTO.getRequestId());
            return CustomerConstants.UPDATED;
        } else {
            logger.error("Customer request not found for update with ID: {}", customerRequestDTO.getRequestId());
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getRequestFilters(ServiceType serviceType, Gender gender,
            String area, Integer pincode, String locality, String apartment_name,
            int page, int size) {

        logger.info(
                "Fetching customer requests with filters: serviceType={}, gender={}, area={}, pincode={}, locality={}, apartment_name={}, page={}, size={}",
                serviceType, gender, area, pincode, locality, apartment_name, page, size);

        Session session = sessionFactory.getCurrentSession();
        StringBuilder queryBuilder = new StringBuilder("FROM CustomerRequest cr WHERE 1=1");

        // Append conditions based on non-null parameters
        if (serviceType != null)
            queryBuilder.append(" AND cr.serviceType = :serviceType");
        if (gender != null)
            queryBuilder.append(" AND cr.gender = :gender");
        if (area != null)
            queryBuilder.append(" AND cr.area = :area");
        if (pincode != null)
            queryBuilder.append(" AND cr.pincode = :pincode");
        if (locality != null)
            queryBuilder.append(" AND cr.locality = :locality");
        if (apartment_name != null)
            queryBuilder.append(" AND cr.apartment_name = :apartment_name");

        Query<CustomerRequest> query = session.createQuery(queryBuilder.toString(), CustomerRequest.class);

        // Set parameters for non-null values
        if (serviceType != null)
            query.setParameter("serviceType", serviceType);
        if (gender != null)
            query.setParameter("gender", gender);
        if (area != null)
            query.setParameter("area", area);
        if (pincode != null)
            query.setParameter("pincode", pincode);
        if (locality != null)
            query.setParameter("locality", locality);
        if (apartment_name != null)
            query.setParameter("apartment_name", apartment_name);

        // Set pagination parameters
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<CustomerRequestDTO> result = query.getResultList().stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());

        logger.debug("Number of customer requests fetched with filters: {}", result.size());
        return result;
    }
}
