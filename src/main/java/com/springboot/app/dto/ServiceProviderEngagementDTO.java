package com.springboot.app.dto;

import java.time.LocalDateTime;

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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String engagements;
    private String timeslot;
    private boolean isActive;
    private double monthlyAmount;
    private PaymentMode paymentMode;
}
