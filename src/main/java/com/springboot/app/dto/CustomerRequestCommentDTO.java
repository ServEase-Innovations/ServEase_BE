package com.springboot.app.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestCommentDTO {
    private Long id;
    private Long requestId;
    private String comment;
    private String commentedBy;
    private Timestamp commentedOn;
}