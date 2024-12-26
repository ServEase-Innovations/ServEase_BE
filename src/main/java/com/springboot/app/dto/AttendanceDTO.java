package com.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    private Long id;

    private Long serviceProviderId; // Reference to ServiceProvider

    private Long customerId; // Reference to Customer

    // private LocalDate date;

    // private LocalTime time;
    private LocalDateTime attendanceStatus;

    private boolean isAttended = false;

    private boolean isCustomerAgreed = true;
}
