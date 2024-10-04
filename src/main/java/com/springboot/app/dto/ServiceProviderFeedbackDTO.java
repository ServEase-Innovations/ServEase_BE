package com.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ServiceProviderFeedbackDTO {
    private Long id;
    private Long customerId;
    private Long housekeepingId;
    private Double rating;
    private String feedback;

}
