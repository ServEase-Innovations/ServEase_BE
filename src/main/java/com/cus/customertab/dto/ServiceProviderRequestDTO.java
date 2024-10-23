package com.cus.customertab.dto;

import java.util.Set;

import com.cus.customertab.entity.ServiceProviderRequestComment;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.ServiceType;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderRequestDTO {
    private Long requestId;
    private Long serviceProviderId;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private Long supervisorId;
    private String isResolved;
    private Long resolvedBy;
    private Timestamp resolvedOn;
    private String timeSlotlist;
    private Integer age;
    private Gender gender;
    private ServiceType housekeepingRole;
    private String isPotential;
    private Set<ServiceProviderRequestComment> comments;
}
