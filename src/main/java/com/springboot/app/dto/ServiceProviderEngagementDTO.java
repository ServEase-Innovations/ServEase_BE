package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

import com.springboot.app.enums.EngagementStatus;
import com.springboot.app.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderEngagementDTO {

    private Long id;
    private Long serviceProviderId;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String engagements;
    private String timeslot;
    private boolean isActive;
    private double monthlyAmount;
    private PaymentMode paymentMode;
    private String bookingType;
    private LocalDateTime bookingDate;
    private List<Map<String, Object>> responsibilities;
    private String serviceType;
    private String mealType;
    private String noOfPersons;
    private String experience;
    private String childAge;
    private String serviceeType;
    private String customerName;
    private String serviceProviderName;
    private EngagementStatus engagementStatus;
    private List<String> availableTimeSlots;
}
