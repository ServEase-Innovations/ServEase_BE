package com.cus.customertab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerConcernDTO {

    private Long id;
    private Long customerId; 
    private String feedback; 
    private Timestamp createdOn; 
    private Timestamp modifiedOn; 
    private String isResolved = "NO"; 
    private Timestamp resolvedOn; 
    private Long resolvedBy; 
    private String resolutionRequired = "NO"; 
    private Integer rating; 
}
