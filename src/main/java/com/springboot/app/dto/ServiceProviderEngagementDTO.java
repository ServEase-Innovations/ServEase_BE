package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

import com.springboot.app.enums.BookingType;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.PaymentMode;
import com.springboot.app.enums.TaskStatus;
import com.springboot.app.enums.UserRole;

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
    private boolean isActive = true;
    private Double monthlyAmount;
    private PaymentMode paymentMode;
    private BookingType bookingType;
    private HousekeepingRole serviceType;
    private LocalDateTime bookingDate;
    private List<Map<String, Object>> responsibilities;
    private HousekeepingRole housekeepingRole;
    private String mealType;
    private String noOfPersons;
    private String experience;
    private String childAge;

    private String customerName;
    private String serviceProviderName;
    private String address;
    private TaskStatus taskStatus = TaskStatus.NOT_STARTED;
    private UserRole role;
    private LocalDateTime modifiedDate;
    private List<String> availableTimeSlots;
}
