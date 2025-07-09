package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.mapper.CustomerHolidaysMapper;
import com.springboot.app.repository.CustomerHolidaysRepository;
import com.springboot.app.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerHolidaysServiceImpl implements CustomerHolidaysService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerHolidaysServiceImpl.class);

    private final CustomerHolidaysMapper customerHolidaysMapper;
    private final CustomerHolidaysRepository customerHolidaysRepository;
    private final CustomerRepository customerRepository;

    public CustomerHolidaysServiceImpl(CustomerHolidaysMapper customerHolidaysMapper,
            CustomerHolidaysRepository customerHolidaysRepository,
            CustomerRepository customerRepository) {
        this.customerHolidaysMapper = customerHolidaysMapper;
        this.customerHolidaysRepository = customerHolidaysRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerHolidaysDTO> getAllHolidays(int page, int size) {
        logger.info("Fetching all holidays with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerHolidays> holidays = customerHolidaysRepository.findAll(pageable).getContent();

        logger.debug("Fetched {} holidays from the database.", holidays.size());

        return holidays.stream()
                .map(customerHolidaysMapper::customerHolidaysToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerHolidaysDTO getHolidayById(Long id) {
        logger.info("Fetching holiday by ID: {}", id);

        Optional<CustomerHolidays> holidayOptional = customerHolidaysRepository.findById(id);
        if (holidayOptional.isPresent()) {
            logger.debug("Found holiday with ID: {}", id);
            return customerHolidaysMapper.customerHolidaysToDTO(holidayOptional.get());
        } else {
            logger.error("No holiday found with ID: {}", id);
            return null;
        }
    }

    @Override
    @Transactional
    public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
        Optional<Customer> customerOptional = customerRepository.findById(customerHolidaysDTO.getCustomerId());
        if (customerOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "Customer with ID " + customerHolidaysDTO.getCustomerId() + " not found.");
        }

        if (customerHolidaysDTO.getEndDate().isBefore(customerHolidaysDTO.getStartDate())) {
            throw new IllegalArgumentException("End date must be after or equal to start date.");
        }

        CustomerHolidays holiday = customerHolidaysMapper.dtoToCustomerHolidays(customerHolidaysDTO);
        holiday.setCustomer(customerOptional.get());
        holiday.setActive(true); // Ensure the holiday is marked active
        customerHolidaysRepository.save(holiday);

        return CustomerConstants.ADDED;
    }

    // @Override
    // @Transactional
    // public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
    // Optional<Customer> customerOptional =
    // customerRepository.findById(customerHolidaysDTO.getCustomerId());
    // if (customerOptional.isEmpty()) {
    // throw new EntityNotFoundException(
    // "Customer with ID " + customerHolidaysDTO.getCustomerId() + " not found.");
    // }

    // CustomerHolidays holiday =
    // customerHolidaysMapper.dtoToCustomerHolidays(customerHolidaysDTO);
    // holiday.setCustomer(customerOptional.get()); // Set the customer entity
    // customerHolidaysRepository.save(holiday);

    // return CustomerConstants.ADDED;
    // }

    @Override
    @Transactional
    public String modifyHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
        logger.info("Modifying holiday with ID: {}", customerHolidaysDTO.getId());

        Optional<CustomerHolidays> existingHolidayOptional = customerHolidaysRepository
                .findById(customerHolidaysDTO.getId());
        if (existingHolidayOptional.isPresent()) {
            CustomerHolidays existingHoliday = existingHolidayOptional.get();
            CustomerHolidays updatedHoliday = customerHolidaysMapper.dtoToCustomerHolidays(customerHolidaysDTO);
            updatedHoliday.setId(existingHoliday.getId());
            customerHolidaysRepository.save(updatedHoliday);

            logger.debug("Modified holiday with ID: {}", customerHolidaysDTO.getId());
            return CustomerConstants.UPDATED;
        } else {
            logger.warn("Holiday not found with ID: {}", customerHolidaysDTO.getId());
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deactivateHoliday(Long id) {
        Optional<CustomerHolidays> existingHolidayOptional = customerHolidaysRepository.findById(id);
        if (existingHolidayOptional.isPresent()) {
            CustomerHolidays holiday = existingHolidayOptional.get();
            holiday.setActive(false); // Mark as inactive instead of deleting
            customerHolidaysRepository.save(holiday); // Save the updated entity
            return CustomerConstants.UPDATED;
        } else {
            return CustomerConstants.NOT_FOUND;
        }
    }

}
