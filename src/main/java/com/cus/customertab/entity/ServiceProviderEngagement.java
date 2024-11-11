package com.cus.customertab.entity;

import java.time.LocalDateTime;
import com.cus.customertab.enums.PaymentMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "serviceprovider_engagement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderEngagement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serviceproviderId", nullable = false)
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column 
    private LocalDateTime endDate;

    @Column
    private double monthlyAmount;

    @Column
    private PaymentMode paymentMode;

    @Column(length = 255)
    private String engagements;

    @Column(length = 50)
    private String timeslot;

    @Column(nullable = false)
    private boolean isActive;

    // Automatically set isActive field on creation
    @PrePersist
    public void prePersist() {
        this.isActive = true;
        this.startDate = LocalDateTime.now(); 
    }

    // Mark engagement as completed
    public void completeEngagement() {
        this.endDate = LocalDateTime.now(); 
        this.isActive = false; 
    }
}
