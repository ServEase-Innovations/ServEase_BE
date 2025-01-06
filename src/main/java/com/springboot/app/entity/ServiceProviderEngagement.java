package com.springboot.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime; // Import LocalDateTime

import com.springboot.app.enums.PaymentMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @JoinColumn(name = "serviceProviderId", nullable = true)
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column // Optional: Adding @Column for consistency
    private LocalDate endDate;
    // private LocalDateTime endDate;

    @Column(length = 255)
    private String engagements;

    @Column(length = 50)
    private String timeslot;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private double monthlyAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column(length = 255)
    private String bookingType;

    @Column(columnDefinition = "TEXT") // Store as a JSON string
    private String responsibilities;

    @Column(length = 255)
    private String serviceType; // The type of service provided

    @Column(length = 255)
    private String mealType; // The meal type (e.g., vegetarian, non-vegetarian, etc.)

    @Column(length = 255)
    private String noOfPersons; // Number of persons involved in the engagement (as a string)

    @Column(length = 255)
    private String experience; // Experience of the service provider (as a string)

    @Column(length = 255)
    private String childAge;

    @Column(length = 255)
    private String serviceeType;

    // Automatically set isActive field on creation
    @PrePersist
    public void prePersist() {
        this.isActive = true; // Set to true by default when the record is created
        this.bookingDate = LocalDateTime.now(); // Set current date-time
    }

    // Mark engagement as completed
    public void completeEngagement() {
        // this.endDate = LocalDateTime.now(); // Set end date to current date-time
        this.isActive = false; // Mark as inactive
    }
}
