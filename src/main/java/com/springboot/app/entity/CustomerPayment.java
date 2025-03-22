package com.springboot.app.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private double baseAmount; // Fixed monthly amount (30,000)

    @Column(nullable = false)
    private double discountAmount; // Discount applied due to vacation

    @Column(nullable = false)
    private double finalAmount; // Amount to be paid after discount

    @Column(nullable = false)
    private LocalDate paymentMonth;

}
