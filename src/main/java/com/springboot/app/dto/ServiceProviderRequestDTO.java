package com.springboot.app.dto;

import java.util.Set;

import java.sql.Timestamp;

import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;

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
    private HousekeepingRole housekeepingRole;
    private String isPotential;
    private Set<ServiceProviderRequestComment> comments;
}
