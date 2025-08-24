package com.springboot.app.service;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.PaymentMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.util.Date;
import java.sql.Date;

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
        // CustomerPaymentDTO calculateAndSavePayment(Long customerId, double
        // baseAmount);
        // CustomerPaymentDTO calculateAndSavePayment(Long customerId, double
        // baseAmount,
        // LocalDate startDate_P, LocalDate endDate_P,
        // PaymentMode paymentMode, Long couponId, HousekeepingRole serviceType);

        CustomerPaymentDTO calculatePayment(Long engagementId, Long customerId, double baseAmount,
                        LocalDate startDate_P, LocalDate endDate_P,
                        PaymentMode paymentMode, Long couponId, HousekeepingRole serviceType);

        // List<CustomerPaymentDTO> getPaymentsByDateRange(Date startDate, Date
        // endDate);

        List<CustomerPaymentDTO> getPaymentsByMonthAndYear(int month, int year);

        List<CustomerPaymentDTO> getPaymentsByFinancialYear(int year);

        List<CustomerPaymentDTO> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);
}
