package com.springboot.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

}
