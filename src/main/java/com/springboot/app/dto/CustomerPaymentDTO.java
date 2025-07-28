package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
    private Long customerId;
    private double baseAmount; // Fixed monthly amount (30,000)
    private double discountAmount; // Discount applied due to vacation
    private double finalAmount; // Amount to be paid after discount
    private LocalDate paymentMonth;
    private LocalDate startDate_P;
    private LocalDate endDate_P;
    // private Date paymentOn;
    private LocalDate paymentOn;
    private LocalDateTime generatedOn;
    private String transactionId;
    private PaymentMode paymentMode;
    private Long couponId; // ✅ add this
    private Double couponDiscount;

}
