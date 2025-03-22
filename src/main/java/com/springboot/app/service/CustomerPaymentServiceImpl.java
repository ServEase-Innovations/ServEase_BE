package com.springboot.app.service;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.entity.CustomerPayment;
import com.springboot.app.mapper.CustomerPaymentMapper;
import com.springboot.app.repository.CustomerHolidaysRepository;
import com.springboot.app.repository.CustomerPaymentRepository;
import com.springboot.app.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPaymentServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerHolidaysRepository customerHolidaysRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final CustomerPaymentMapper customerPaymentMapper;

    public CustomerPaymentServiceImpl(CustomerRepository customerRepository,
            CustomerHolidaysRepository customerHolidaysRepository,
            CustomerPaymentRepository customerPaymentRepository,
            CustomerPaymentMapper customerPaymentMapper) {
        this.customerRepository = customerRepository;
        this.customerHolidaysRepository = customerHolidaysRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.customerPaymentMapper = customerPaymentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPaymentDTO> getPaymentsByCustomerId(Long customerId) {
        logger.info("Fetching payments for customer ID: {}", customerId);
        List<CustomerPayment> payments = customerPaymentRepository.findByCustomer_CustomerId(customerId);

        logger.debug("Fetched {} payments for customer ID: {}", payments.size(), customerId);
        return payments.stream()
                .map(customerPaymentMapper::customerPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerPaymentDTO> getPaymentByCustomerIdAndMonth(Long customerId, LocalDate paymentMonth) {
        logger.info("Fetching payment for customer ID: {} and month: {}", customerId, paymentMonth);
        return customerPaymentRepository.findByCustomer_CustomerIdAndPaymentMonth(customerId, paymentMonth)
                .map(customerPaymentMapper::customerPaymentToDTO);
    }

    @Transactional
    public CustomerPaymentDTO calculateAndSavePayment(Long customerId, double baseAmount) {
        logger.info("Calculating payment for customer ID: {}, Base Amount: {}", customerId, baseAmount);

        if (baseAmount <= 0) {
            throw new IllegalArgumentException("Base amount must be greater than zero.");
        }

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new EntityNotFoundException("Customer with ID " + customerId + " not found.");
        }

        Customer customer = customerOptional.get();
        double dailyRate = baseAmount / 30; // Calculate daily rate based on provided base amount

        // Fetch active holidays
        List<CustomerHolidays> holidays = customerHolidaysRepository.findByCustomer_CustomerIdAndIsActive(customerId,
                true);

        // Calculate total vacation days (including end date)
        int totalVacationDays = holidays.stream()
                .mapToInt(h -> (int) ChronoUnit.DAYS.between(h.getStartDate(), h.getEndDate()) + 1)
                .sum();

        // Determine discount percentage
        double discountPercentage = getDiscountPercentage(totalVacationDays);
        double discountAmount = (dailyRate * totalVacationDays) * (discountPercentage / 100);
        double finalAmount = baseAmount - discountAmount;

        // Get current month (1st day)
        LocalDate paymentMonth = LocalDate.now().withDayOfMonth(1);
        Optional<CustomerPayment> existingPayment = customerPaymentRepository.findByCustomer_CustomerIdAndPaymentMonth(
                customerId, paymentMonth);

        // Create or update payment record
        CustomerPayment payment = existingPayment.orElse(new CustomerPayment());
        payment.setCustomer(customer);
        payment.setBaseAmount(baseAmount);
        payment.setDiscountAmount(discountAmount);
        payment.setFinalAmount(finalAmount);
        payment.setPaymentMonth(paymentMonth);

        customerPaymentRepository.save(payment);

        logger.info("Saved payment for customer ID: {}, Final Amount: {}", customerId, finalAmount);

        return new CustomerPaymentDTO(payment.getId(), customerId, baseAmount, discountAmount, finalAmount,
                paymentMonth);
    }

    private double getDiscountPercentage(int days) {
        if (days >= 1 && days <= 7) {
            return 40;
        }
        if (days >= 8 && days <= 15) {
            return 50;
        }
        if (days > 15) {
            return 60;
        }
        return 0;
    }

}
