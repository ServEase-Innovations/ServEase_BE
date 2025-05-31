package com.springboot.app.controller;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.service.CustomerPaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class CustomerPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPaymentController.class);
    private final CustomerPaymentService customerPaymentService;

    public CustomerPaymentController(CustomerPaymentService customerPaymentService) {
        this.customerPaymentService = customerPaymentService;
    }

    /**
     * Get all payments for a customer.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getPaymentsByCustomerId(@PathVariable Long customerId) {
        logger.info("Fetching payments for customer ID: {}", customerId);
        List<CustomerPaymentDTO> payments = customerPaymentService.getPaymentsByCustomerId(customerId);
        
        return ResponseEntity.ok(payments);
}


    /**
     * Get payment details for a customer for a specific month.
     */
    @GetMapping("/{customerId}/month")
    public ResponseEntity<CustomerPaymentDTO> getPaymentByCustomerIdAndMonth(
            @PathVariable Long customerId,
            @RequestParam("month") String month) {
        LocalDate paymentMonth = LocalDate.parse(month);
        logger.info("Fetching payment for customer ID: {} and month: {}", customerId,
                paymentMonth);

        Optional<CustomerPaymentDTO> payment = customerPaymentService.getPaymentByCustomerIdAndMonth(customerId,
                paymentMonth);
        return ResponseEntity.ok(payment.orElse(null));
    }

    /**
     * Calculate and save payment for a customer.
     */
    // @PostMapping("/{customerId}/calculate")
    // public ResponseEntity<CustomerPaymentDTO>
    // calculateAndSavePayment(@PathVariable Long customerId) {
    // logger.info("Calculating payment for customer ID: {}", customerId);
    // CustomerPaymentDTO paymentDTO =
    // customerPaymentService.calculateAndSavePayment(customerId);
    // return ResponseEntity.ok(paymentDTO);
    // }

    @PostMapping("/calculate-payment")
    public ResponseEntity<?> calculatePayment(@RequestParam Long customerId, @RequestParam double baseAmount) {
        try {
            CustomerPaymentDTO paymentDTO = customerPaymentService.calculateAndSavePayment(customerId, baseAmount);
            if (paymentDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to calculate payment for customer with ID: " + customerId);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentDTO);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer with ID " + customerId + " not found.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while calculating the payment: " + ex.getMessage());
        }
    }

}
