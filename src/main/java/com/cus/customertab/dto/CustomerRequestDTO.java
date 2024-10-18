package com.cus.customertab.dto;

import com.cus.customertab.entity.CustomerRequestComment;
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.Habit;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private Long requestId;
    private Long customerId;
    private ServiceType serviceType;
    private String timeSlotlist;
    private Gender gender;
    private LanguageKnown languageKnown;
    private String ageRange;
    private Habit cookingHabit;
    private Habit dietryHabit;
    private Long serviceProviderId;
    private Timestamp assignedDate;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private String isResolved = "NO";
    private Long supervisorId;
    private String isPotential = "NO";
    private Long modifiedBy;
    private List<CustomerRequestComment> comments;
}