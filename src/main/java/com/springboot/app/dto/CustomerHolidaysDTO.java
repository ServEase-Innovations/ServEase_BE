package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.HousekeepingRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerHolidaysDTO {

    private Long id;
    private Long customerId;
    private LocalDateTime bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private HousekeepingRole serviceType;

}
