package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.app.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPaymentDTO {

    private Long id;
    private Long engagementId; // Mandatory: payment tied to engagement
    private Long customerId; // Optional: customer reference
    private double baseAmount; // Fixed monthly amount (e.g., 30,000)
    private double discountAmount; // Discount applied due to vacation
    private double finalAmount; // Amount to be paid after discount
    private LocalDate paymentMonth;
    private LocalDate startDate_P;
    private LocalDate endDate_P;
    private LocalDate paymentOn;
    private LocalDateTime generatedOn;
    private String transactionId;
    private PaymentMode paymentMode;
    private Long couponId; // Nullable
    private Double couponDiscount; // Nullable
}
