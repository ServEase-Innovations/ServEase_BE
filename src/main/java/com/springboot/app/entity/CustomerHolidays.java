package com.springboot.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.app.enums.HousekeepingRole;

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

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

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
        this.bookingDate = LocalDateTime.now(); // Set current date-time
    }

    // Mark engagement as completed
    public void completeEngagement() {
        this.isActive = false; // Mark as inactive
    }

}
