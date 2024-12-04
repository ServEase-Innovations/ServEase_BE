package com.springboot.app.entity;

import java.sql.Date;
import java.sql.Timestamp;
import com.springboot.app.enums.Currency;
import com.springboot.app.enums.PaymentMode;
import com.springboot.app.listener.ServiceProviderPaymentListener;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_provider_payment")
@EntityListeners(ServiceProviderPaymentListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serviceProviderId", nullable = false)
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    private Timestamp settledOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode;

    @Column(nullable = false)
    private double noOfDays;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String UpiId;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int monthlyAmount;

    // @PrePersist
    // public void calculateFields() {
    // // Calculate noOfDays based on endDate - startDate
    // this.noOfDays = ChronoUnit.DAYS.between(startDate.toLocalDate(),
    // endDate.toLocalDate());

    // // Get the number of days in the month of the endDate
    // int daysInMonth =
    // endDate.toLocalDate().getMonth().length(endDate.toLocalDate().isLeapYear());

    // //Calculate amount
    // this.amount = (int) ((double) monthlyAmount / daysInMonth * this.noOfDays);
    // }
}
