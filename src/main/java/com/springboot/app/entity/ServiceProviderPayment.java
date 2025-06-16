package com.springboot.app.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.springboot.app.enums.Currency;
import com.springboot.app.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_provider_payment")
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

    @Column
    private Date paymentOn;

    @Column(nullable = false)
    private String transactionId;

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

    @Column
    private int month;

    @Column
    private int year;

    @Column(nullable = false)
    private int monthlyAmount;

    @PrePersist
    protected void onCreate() {
        if (this.paymentOn == null) {
            this.paymentOn = Date.valueOf(LocalDate.now());
        }
        LocalDate localDate = this.paymentOn.toLocalDate();
        this.month = localDate.getMonthValue();
        this.year = localDate.getYear();
    }

}
