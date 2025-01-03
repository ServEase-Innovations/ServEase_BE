package com.springboot.app.dto;

import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.springboot.app.enums.PaymentMode;

//import jakarta.persistence.Column;
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
    private List<Map<String, Object>> responsibilities;
}
