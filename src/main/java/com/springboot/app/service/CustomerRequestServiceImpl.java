
package com.springboot.app.service;

import java.util.Map;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Status;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.mapper.CustomerRequestMapper;
import com.springboot.app.repository.CustomerRequestRepository;
import java.util.Collections;

@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRequestServiceImpl.class);

    private final CustomerRequestRepository customerRequestRepository;
    private final CustomerRequestMapper customerRequestMapper;


    @Autowired
    public CustomerRequestServiceImpl(CustomerRequestRepository customerRequestRepository,
            CustomerRequestMapper customerRequestMapper) {
        this.customerRequestRepository = customerRequestRepository;
        this.customerRequestMapper = customerRequestMapper;
    }

    @Autowired
    private CustomerConstants customerconstants;

    // To get all customer requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAll(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all customer requests with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<CustomerRequest> requests = customerRequestRepository.findAll(pageable).getContent();

        return requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    // To get a customer request by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestDTO getByRequestId(Long requestId) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching customer request by ID: {}", requestId);
        }
        return customerRequestRepository.findById(requestId)
                .map(customerRequestMapper::customerRequestToDTO)
                .orElse(null);
    }

    // To get all open requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAllOpenRequests(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all open customer requests with pagination - page: {}, size: {}", page, size);
        }
        // Example of custom query to filter open requests
        List<CustomerRequest> openRequests = customerRequestRepository.findAll()
                .stream()
                .filter(cr -> "NO".equals(cr.getIsResolved())) // Assuming "NO" means open
                .skip((long) page * size)
                .limit(size)
                .toList();
        if (openRequests.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No open customer requests found for the given criteria.");
            }
            return Collections.emptyList();
        }
        return openRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> findAllPotentialCustomers(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all potential customers with pagination - page: {}, size: {}", page, size);
        }
        // Example of custom query to filter potential customers
        List<CustomerRequest> potentialCustomers = customerRequestRepository.findAll()
                .stream()
                .filter(cr -> "YES".equals(cr.getIsPotential())) // Assuming "YES" means potential
                .skip((long) page * size)
                .limit(size)
                .toList();
        if (potentialCustomers.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No potential customers found for the given criteria.");
            }
            return Collections.emptyList();
        }
        return potentialCustomers.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    // To add a new customer request
    @Override
    @Transactional
    public String insert(CustomerRequestDTO customerRequestDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new customer request for customer ID: {}", customerRequestDTO.getCustomerId());
        }
        CustomerRequest request = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        customerRequestRepository.save(request);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new customer request with ID: {}", request.getRequestId());
        }
        return CustomerConstants.ADDED;
    }

    // To update a customer request
    @Override
    @Transactional
    public String update(CustomerRequestDTO customerRequestDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating customer request with ID: {}", customerRequestDTO.getRequestId());
        }
        if (customerRequestRepository.existsById(customerRequestDTO.getRequestId())) {
            CustomerRequest updatedRequest = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
            customerRequestRepository.save(updatedRequest);
            if (logger.isDebugEnabled()) {
                logger.debug("Updated customer request with ID: {}", updatedRequest.getRequestId());
            }
            return CustomerConstants.UPDATED;
        } else {
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getRequestFilters(
            HousekeepingRole housekeepingRole, Gender gender,
            String area, Integer pincode, String locality,
            String apartment_name, int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching customer requests with filters");
        }
              
                
        // You can add custom queries here based on filters.
        List<CustomerRequest> filteredRequests = customerRequestRepository.findAll()
                .stream()
                .filter(cr -> (housekeepingRole == null || cr.getHousekeepingRole() == housekeepingRole) &&
                        (gender == null || cr.getGender() == gender) &&
                        (area == null || cr.getArea().equals(area)) &&
                        (pincode == null || cr.getPincode().equals(pincode)) &&
                        (locality == null || cr.getLocality().equals(locality)) &&
                        (apartment_name == null || cr.getApartment_name().equals(apartment_name)))
                .skip((long) page * size)
                .limit(size)
                .toList();
        if (filteredRequests.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No customer requests found for the given filters.");
            }
            return Collections.emptyList();
        }
        return filteredRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void updateStatus(Long requestId, Status status) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating status of customer request with id: {}", requestId);
        }
        CustomerRequest customerRequest = customerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("CustomerRequest not found with id: " + requestId));
        customerRequest.setStatus(status);
        if (logger.isDebugEnabled()) {
            logger.debug("Setting status to: {}", status);
        }
        customerRequest.setModifiedDate(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        customerRequestRepository.save(customerRequest);
        if (logger.isDebugEnabled()) {
            logger.debug("Updated customer request with ID: {}", requestId);
        }
    }

    // To get and categorize all customer requests
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<CustomerRequestDTO>> getBookingHistory(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching and categorizing customer requests with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<CustomerRequest> requests = customerRequestRepository.findAll(pageable).getContent();

        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        // Get current date
        LocalDate currentDate = LocalDate.now();
        if (logger.isDebugEnabled()) {
            logger.debug("Current date for categorization: {}", currentDate);
        }
        return requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.groupingBy(request -> {
                    LocalDate startDate = request.getStartDate().toLocalDateTime().toLocalDate();
                    LocalDate endDate = request.getEndDate() != null
                            ? request.getEndDate().toLocalDateTime().toLocalDate()
                            : null;

                    if (startDate == null || startDate.isAfter(currentDate)) {
                        return "future";
                    } else if (endDate == null || endDate.isAfter(currentDate) || endDate.isEqual(currentDate)) {
                        return "current";
                    } else {
                        return "past";
                    }
                }));
    }

}
