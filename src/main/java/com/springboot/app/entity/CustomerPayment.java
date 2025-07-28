package com.springboot.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.springboot.app.enums.PaymentMode;

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

    @Column(nullable = false)
    private LocalDate startDate_P; // Payment coverage start date

    @Column(nullable = false)
    private LocalDate endDate_P; // Payment coverage end date

    @Column(nullable = false)
    private LocalDate paymentOn;
    // private Date paymentOn;

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedOn; // Timestamp of payment generation

    @Column(nullable = false, unique = true)
    private String transactionId; // Unique transaction ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode; // Mode of payment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", referencedColumnName = "id")
    private Coupon coupon; // Nullable

    @Column(name = "coupon_discount_amount")
    private Double couponDiscountAmount; // Nullable

}
