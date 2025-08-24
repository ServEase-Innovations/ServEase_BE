package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.app.enums.HousekeepingRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerHolidaysDTO {

    private Long id;
    private Long engagementId; // Mandatory: linked to engagement
    private Long customerId; // Optional: customer reference
    private LocalDateTime applyHolidayDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private HousekeepingRole serviceType;
}
