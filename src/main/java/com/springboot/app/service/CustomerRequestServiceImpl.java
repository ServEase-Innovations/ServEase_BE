
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
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.mapper.CustomerRequestMapper;
import com.springboot.app.repository.CustomerRequestRepository;

@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRequestServiceImpl.class);

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    // To get all customer requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAll(int page, int size) {
        logger.info("Fetching all customer requests with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerRequest> requests = customerRequestRepository.findAll(pageable).getContent();

        return requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    // To get a customer request by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestDTO getByRequestId(Long requestId) {
        return customerRequestRepository.findById(requestId)
                .map(customerRequestMapper::customerRequestToDTO)
                .orElse(null);
    }

    // To get all open requests
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getAllOpenRequests(int page, int size) {
        // Example of custom query to filter open requests
        List<CustomerRequest> openRequests = customerRequestRepository.findAll()
                .stream()
                .filter(cr -> "NO".equals(cr.getIsResolved())) // Assuming "NO" means open
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
        return openRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> findAllPotentialCustomers(int page, int size) {
        // Example of custom query to filter potential customers
        List<CustomerRequest> potentialCustomers = customerRequestRepository.findAll()
                .stream()
                .filter(cr -> "YES".equals(cr.getIsPotential())) // Assuming "YES" means potential
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
        return potentialCustomers.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    // To add a new customer request
    @Override
    @Transactional
    public String insert(CustomerRequestDTO customerRequestDTO) {
        CustomerRequest request = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
        customerRequestRepository.save(request);
        return "ADDED";
    }

    // To update a customer request
    @Override
    @Transactional
    public String update(CustomerRequestDTO customerRequestDTO) {
        if (customerRequestRepository.existsById(customerRequestDTO.getRequestId())) {
            CustomerRequest updatedRequest = customerRequestMapper.dtoToCustomerRequest(customerRequestDTO);
            customerRequestRepository.save(updatedRequest);
            return "UPDATED";
        } else {
            return "NOT_FOUND";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestDTO> getRequestFilters(
            HousekeepingRole housekeepingRole, Gender gender,
            String area, Integer pincode, String locality,
            String apartment_name, int page, int size) {
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
                .collect(Collectors.toList());

        return filteredRequests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.toList());
    }

    // To get and categorize all customer requests
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<CustomerRequestDTO>> getBookingHistory(int page, int size) {
        logger.info("Fetching and categorizing customer requests with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerRequest> requests = customerRequestRepository.findAll(pageable).getContent();

        if (requests.isEmpty()) {
            return null; 
        }

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Categorize requests
        Map<String, List<CustomerRequestDTO>> categorizedRequests = requests.stream()
                .map(customerRequestMapper::customerRequestToDTO)
                .collect(Collectors.groupingBy(request -> {
                    LocalDate startDate = request.getStartDate().toLocalDateTime().toLocalDate();
                    LocalDate endDate = request.getEndDate() != null ? request.getEndDate().toLocalDateTime().toLocalDate() : null;

                    if ( startDate == null || startDate.isAfter(currentDate)) {
                        return "future";
                    } else if (endDate == null || endDate.isAfter(currentDate) || endDate.isEqual(currentDate)) {
                        return "current";
                    } else {
                        return "past";
                    }
                }));

        return categorizedRequests;
    }
}
