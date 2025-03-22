package com.springboot.app.service;

import com.springboot.app.dto.CustomerPaymentDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerPaymentService {

    // Fetch all payments for a specific customer
    List<CustomerPaymentDTO> getPaymentsByCustomerId(Long customerId);

    // Fetch a specific customer's payment for a given month
    Optional<CustomerPaymentDTO> getPaymentByCustomerIdAndMonth(Long customerId,
            LocalDate paymentMonth);

    // CustomerPaymentDTO calculateAndSavePayment(Long customerId); // <-- Add
    // thismethod
    CustomerPaymentDTO calculateAndSavePayment(Long customerId, double baseAmount);
}
