package com.springboot.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.app.enums.HousekeepingRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_holidays")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerHolidays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Engagement is the main reference
    @ManyToOne
    @JoinColumn(name = "engagement_id", nullable = false)
    private ServiceProviderEngagement engagement;

    // Customer is optional
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime applyHolidayDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private HousekeepingRole serviceType;

    // Automatically set isActive field on creation
    @PrePersist
    public void prePersist() {
        this.isActive = true; // Set to true by default when the record is created
        this.applyHolidayDate = LocalDateTime.now(); // Set current date-time
    }

    // Mark holiday as completed
    public void completeHoliday() {
        this.isActive = false; // Mark as inactive
    }
}
