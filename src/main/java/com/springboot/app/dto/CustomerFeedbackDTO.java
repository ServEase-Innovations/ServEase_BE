package com.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedbackDTO {
    private Long id;
    private Long customerId;
    private Long serviceProviderId;
    private Integer rating;
    private String comment;
    private Timestamp commentedOn;
}
