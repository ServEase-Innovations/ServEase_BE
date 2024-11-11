package com.cus.customertab.dto;

import java.time.LocalDateTime;

import com.cus.customertab.enums.PaymentMode;

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
    private Long serviceproviderId;
    private Long customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double monthlyAmount;
    private PaymentMode paymentMode;
    private String engagements;
    private String timeslot;
    private boolean isActive;
}