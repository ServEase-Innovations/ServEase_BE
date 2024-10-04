package com.cus.customertab.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.Habit;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CustomerRequest")
public class CustomerRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long requestId;

    @Column
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String timeSlotlist;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String ageRange;

    @Enumerated(EnumType.STRING)
    private Habit cookingHabit; 

    @Enumerated(EnumType.STRING)
    private Habit dietryHabit;

    @Column
    private Long serviceProviderId;

    @Column(nullable = false, updatable = false)
    private Timestamp assignedDate;

    @Column(nullable = false, updatable = false)
    private Timestamp createdDate;

    @Column
    private Timestamp modifiedDate;

    @Enumerated(EnumType.STRING)
    private LanguageKnown languageKnown;

    @Column(nullable = false)
    private String isResolved = "NO";

    @Column
    private Long commentBy;

    @Column(length = 255)
    private String comments;

    @Column
    private Long supervisorId;

    @Column(nullable = false)
    private String isPotential = "NO";

    @Column
    private Long modifiedBy;

    @Column
    private Timestamp commentsOn;

    @PrePersist
    protected void onCreate(){
        createdDate = Timestamp.valueOf(LocalDateTime.now());
        assignedDate = Timestamp.valueOf(LocalDateTime.now());
    }
    
    protected void onUpdate(){
        modifiedDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
