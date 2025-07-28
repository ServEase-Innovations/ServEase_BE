package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.enums.BookingType;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.mapper.CustomerHolidaysMapper;
import com.springboot.app.repository.CustomerHolidaysRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerHolidaysServiceImpl implements CustomerHolidaysService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerHolidaysServiceImpl.class);
    private final CustomerHolidaysMapper customerHolidaysMapper;
    private final CustomerHolidaysRepository customerHolidaysRepository;
    private final CustomerRepository customerRepository;
    private final ServiceProviderEngagementRepository engagementRepository;

    public CustomerHolidaysServiceImpl(CustomerHolidaysMapper customerHolidaysMapper,
            CustomerHolidaysRepository customerHolidaysRepository,
            CustomerRepository customerRepository,
            ServiceProviderEngagementRepository engagementRepository) {
        this.customerHolidaysMapper = customerHolidaysMapper;
        this.customerHolidaysRepository = customerHolidaysRepository;
        this.customerRepository = customerRepository;
        this.engagementRepository = engagementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerHolidaysDTO> getAllHolidays(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all holidays with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<CustomerHolidays> holidays = customerHolidaysRepository.findAll(pageable).getContent();

        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} holidays from the database.", holidays.size());
        }
        return holidays.stream()
                .map(customerHolidaysMapper::customerHolidaysToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerHolidaysDTO getHolidayById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching holiday by ID: {}", id);
        }
        Optional<CustomerHolidays> holidayOptional = customerHolidaysRepository.findById(id);
        if (holidayOptional.isPresent()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found holiday with ID: {}", id);
            }
            return customerHolidaysMapper.customerHolidaysToDTO(holidayOptional.get());
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No holiday found with ID: {}", id);
            }
            return null;
        }
    }

    // @Override
    // @Transactional
    // public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
    // if (logger.isInfoEnabled()) {
    // logger.info("Adding new holiday for customer ID: {}",
    // customerHolidaysDTO.getCustomerId());
    // }

    // Optional<Customer> customerOptional =
    // customerRepository.findById(customerHolidaysDTO.getCustomerId());
    // if (customerOptional.isEmpty()) {
    // throw new EntityNotFoundException(
    // "Customer with ID " + customerHolidaysDTO.getCustomerId() + " not found.");
    // }

    // // ✅ Fetch active engagement for the customer
    // Optional<ServiceProviderEngagement> engagementOptional = engagementRepository
    // .findFirstByCustomer_CustomerIdAndIsActiveTrue(customerHolidaysDTO.getCustomerId());

    // if (engagementOptional.isEmpty()) {
    // throw new EntityNotFoundException("No active engagement found for
    // customer.");
    // }

    // ServiceProviderEngagement engagement = engagementOptional.get();
    // LocalDate engagementStart = engagement.getStartDate();
    // LocalDate engagementEnd = engagement.getEndDate();

    // // ✅ Use dates from DTO
    // LocalDate holidayStart = customerHolidaysDTO.getStartDate();
    // LocalDate holidayEnd = customerHolidaysDTO.getEndDate();

    // if (holidayStart == null || holidayEnd == null) {
    // throw new IllegalArgumentException("Holiday start and end dates must not be
    // null.");
    // }

    // if (holidayEnd.isBefore(holidayStart)) {
    // throw new IllegalArgumentException("Holiday end date must be after or equal
    // to start date.");
    // }

    // // ✅ Validate that holiday is within engagement period
    // if (holidayStart.isBefore(engagementStart) ||
    // holidayEnd.isAfter(engagementEnd)) {
    // throw new IllegalArgumentException(
    // String.format("Holiday must fall within engagement period: %s to %s",
    // engagementStart,
    // engagementEnd));
    // }

    // // ✅ Map and save holiday
    // CustomerHolidays holiday = new CustomerHolidays();
    // holiday.setCustomer(customerOptional.get());
    // holiday.setStartDate(holidayStart);
    // holiday.setEndDate(holidayEnd);
    // holiday.setBookingDate(LocalDateTime.now());
    // holiday.setActive(true);

    // customerHolidaysRepository.save(holiday);

    // return CustomerConstants.ADDED;
    // }

    @Override
    @Transactional
    public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new holiday for customer ID: {}", customerHolidaysDTO.getCustomerId());
        }

        Long customerId = customerHolidaysDTO.getCustomerId();
        HousekeepingRole serviceType = customerHolidaysDTO.getServiceType(); // From DTO

        if (serviceType == null) {
            throw new IllegalArgumentException("Service type must be provided to apply a holiday.");
        }

        // ✅ Find engagement by customerId + serviceType + isActive = true
        Optional<ServiceProviderEngagement> engagementOptional = engagementRepository
                .findFirstByCustomer_CustomerIdAndServiceTypeAndIsActiveTrue(customerId, serviceType);

        if (engagementOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    "No active engagement found for customer with service type: " + serviceType);
        }

        ServiceProviderEngagement engagement = engagementOptional.get();

        // ✅ Reject ON_DEMAND
        if (engagement.getBookingType() == BookingType.ON_DEMAND) {
            throw new IllegalArgumentException("Cannot apply holiday for ON_DEMAND booking.");
        }

        LocalDate engagementStart = engagement.getStartDate();
        LocalDate engagementEnd = engagement.getEndDate();

        LocalDate holidayStart = customerHolidaysDTO.getStartDate();
        LocalDate holidayEnd = customerHolidaysDTO.getEndDate();

        if (holidayStart == null || holidayEnd == null) {
            throw new IllegalArgumentException("Holiday start and end dates must not be null.");
        }

        if (holidayEnd.isBefore(holidayStart)) {
            throw new IllegalArgumentException("Holiday end date must be after or equal to start date.");
        }

        // ✅ Calculate total holiday duration
        long holidayDays = ChronoUnit.DAYS.between(holidayStart, holidayEnd) + 1;

        if (holidayDays < 10) {
            throw new IllegalArgumentException("Holidays less than 10 days are not eligible for discounts.");
        }

        if (holidayStart.isBefore(engagementStart) ||
                (engagementEnd != null && holidayEnd.isAfter(engagementEnd))) {
            throw new IllegalArgumentException(String.format(
                    "Holiday must fall within engagement period: %s to %s",
                    engagementStart,
                    engagementEnd != null ? engagementEnd : "Ongoing"));
        }

        CustomerHolidays holiday = new CustomerHolidays();
        holiday.setCustomer(engagement.getCustomer());
        holiday.setStartDate(holidayStart);
        holiday.setEndDate(holidayEnd);
        holiday.setBookingDate(LocalDateTime.now());
        holiday.setActive(true);
        holiday.setServiceType(serviceType); // ✅ Store service type if your table supports it

        customerHolidaysRepository.save(holiday);

        return CustomerConstants.ADDED;
    }

    // @Override
    // @Transactional
    // public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
    // if (logger.isInfoEnabled()) {
    // logger.info("Adding new holiday for customer ID: {}",
    // customerHolidaysDTO.getCustomerId());
    // }

    // Optional<Customer> customerOptional =
    // customerRepository.findById(customerHolidaysDTO.getCustomerId());
    // if (customerOptional.isEmpty()) {
    // throw new EntityNotFoundException(
    // "Customer with ID " + customerHolidaysDTO.getCustomerId() + " not found.");
    // }

    // // ✅ Fetch active engagement for the customer
    // Optional<ServiceProviderEngagement> engagementOptional = engagementRepository
    // .findFirstByCustomer_CustomerIdAndIsActiveTrue(customerHolidaysDTO.getCustomerId());

    // if (engagementOptional.isEmpty()) {
    // throw new EntityNotFoundException("No active engagement found for
    // customer.");
    // }

    // ServiceProviderEngagement engagement = engagementOptional.get();
    // LocalDate engagementStart = engagement.getStartDate();
    // LocalDate engagementEnd = engagement.getEndDate();

    // // ✅ Create a holiday entity using engagement dates instead of DTO input
    // CustomerHolidays holiday = new CustomerHolidays();
    // holiday.setCustomer(customerOptional.get());
    // holiday.setStartDate(engagementStart); // From engagement
    // holiday.setEndDate(engagementEnd); // From engagement
    // holiday.setBookingDate(LocalDateTime.now());
    // holiday.setActive(true); // Ensure the holiday is marked active

    // customerHolidaysRepository.save(holiday);

    // return CustomerConstants.ADDED;
    // }

    // @Override
    // @Transactional
    // public String addNewHoliday(CustomerHolidaysDTO customerHolidaysDTO) {
    // if (logger.isInfoEnabled()) {
    // logger.info("Adding new holiday for customer ID: {}",
    // customerHolidaysDTO.getCustomerId());
    // }
    // Optional<Customer> customerOptional =
    // customerRepository.findById(customerHolidaysDTO.getCustomerId());
    // if (customerOptional.isEmpty()) {
    // throw new EntityNotFoundException(
    // "Customer with ID " + customerHolidaysDTO.getCustomerId() + " not found.");
    // }

    // if
    // (customerHolidaysDTO.getEndDate().isBefore(customerHolidaysDTO.getStartDate()))
    // {
    // throw new IllegalArgumentException("End date must be after or equal to start
    // date.");
    // }

    // CustomerHolidays holiday =
    // customerHolidaysMapper.dtoToCustomerHolidays(customerHolidaysDTO);
    // holiday.setCustomer(customerOptional.get());
    // holiday.setActive(true); // Ensure the holiday is marked active
    // customerHolidaysRepository.save(holiday);

    // return CustomerConstants.ADDED;
    // }

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
        if (logger.isInfoEnabled()) {
            logger.info("Modifying holiday with ID: {}", customerHolidaysDTO.getId());
        }

        Optional<CustomerHolidays> existingHolidayOptional = customerHolidaysRepository
                .findById(customerHolidaysDTO.getId());
        if (existingHolidayOptional.isPresent()) {
            CustomerHolidays existingHoliday = existingHolidayOptional.get();
            CustomerHolidays updatedHoliday = customerHolidaysMapper.dtoToCustomerHolidays(customerHolidaysDTO);
            updatedHoliday.setId(existingHoliday.getId());
            customerHolidaysRepository.save(updatedHoliday);

            if (logger.isDebugEnabled()) {
                logger.debug("Modified holiday with ID: {}", customerHolidaysDTO.getId());
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Holiday not found with ID: {}", customerHolidaysDTO.getId());
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deactivateHoliday(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deactivating holiday with ID: {}", id);
        }
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
