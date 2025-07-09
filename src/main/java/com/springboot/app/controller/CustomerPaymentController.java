package com.springboot.app.controller;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.enums.PaymentMode;
import com.springboot.app.service.CustomerPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;

import java.util.Optional;
import java.util.Collections;

@RestController
@RequestMapping("/api/payments")
public class CustomerPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPaymentController.class);
    private final CustomerPaymentService customerPaymentService;

    public CustomerPaymentController(CustomerPaymentService customerPaymentService) {
        this.customerPaymentService = customerPaymentService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CustomerPaymentDTO>> getPaymentsByCustomerId(@PathVariable Long customerId) {
        logger.info(CustomerConstants.FETCHING_PAYMENTS_FOR_CUSTOMER, customerId);
        List<CustomerPaymentDTO> payments = customerPaymentService.getPaymentsByCustomerId(customerId);
        if (payments.isEmpty()) {
            logger.info(CustomerConstants.NO_PAYMENTS_FOUND_FOR_CUSTOMER, customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{customerId}/month")
    public ResponseEntity<CustomerPaymentDTO> getPaymentByCustomerIdAndMonth(
            @PathVariable Long customerId,
            @RequestParam("month") String month) {
        LocalDate paymentMonth = LocalDate.parse(month);
        logger.info(CustomerConstants.FETCHING_PAYMENT_FOR_CUSTOMER_AND_MONTH, customerId, paymentMonth);

        Optional<CustomerPaymentDTO> payment = customerPaymentService.getPaymentByCustomerIdAndMonth(customerId,
                paymentMonth);
        return payment.map(ResponseEntity::ok).orElseGet(() -> {
            logger.info(CustomerConstants.NO_PAYMENT_FOUND_FOR_CUSTOMER_AND_MONTH, customerId, paymentMonth);
            return ResponseEntity.notFound().build();
        });
    }

  
    @GetMapping("/by-date")
    public ResponseEntity<List<CustomerPaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        logger.info(CustomerConstants.FETCHING_PAYMENTS_BETWEEN_DATES, startDate, endDate);
        List<CustomerPaymentDTO> payments = customerPaymentService.getPaymentsByDateRange(startDate, endDate);

        if (payments.isEmpty()) {
            logger.info(CustomerConstants.NO_PAYMENTS_FOUND_BETWEEN_DATES, startDate, endDate);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }
        return ResponseEntity.ok(payments);
    }

   
    @PostMapping("/calculate-payment")
    public ResponseEntity<CustomerPaymentDTO> calculatePayment(
            @RequestParam Long customerId,
            @RequestParam double baseAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate_P,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate_P,
            @RequestParam PaymentMode paymentMode) {

        CustomerPaymentDTO paymentDTO = customerPaymentService.calculateAndSavePayment(
                customerId, baseAmount, startDate_P, endDate_P, paymentMode);
        return ResponseEntity.ok(paymentDTO);
    }

    @GetMapping("/customer-payments/by-month-year")
    public ResponseEntity<List<CustomerPaymentDTO>> getPaymentsByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {

        List<CustomerPaymentDTO> payments = customerPaymentService.getPaymentsByMonthAndYear(month, year);
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer-payments/by-financial-year")
    public ResponseEntity<List<CustomerPaymentDTO>> getPaymentsByFinancialYear(@RequestParam int year) {
        List<CustomerPaymentDTO> payments = customerPaymentService.getPaymentsByFinancialYear(year);
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(payments);
    }

}
