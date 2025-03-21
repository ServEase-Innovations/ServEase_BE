package com.springboot.app.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPaymentDTO {

    private Long id;
    private Long customerId;
    private double baseAmount; // Fixed monthly amount (30,000)
    private double discountAmount; // Discount applied due to vacation
    private double finalAmount; // Amount to be paid after discount
    private LocalDate paymentMonth;

}
