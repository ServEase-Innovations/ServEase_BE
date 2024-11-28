package com.springboot.app.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Habit;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.HousekeepingRole;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CustomerRequest")
public class CustomerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousekeepingRole housekeepingRole;

    @Column(nullable = false)
    private String timeSlotlist;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String ageRange;

    @Column
    private String area;

    @Column
    private String locality;

    @Column
    private String apartment_name;

    @Column(nullable = false, length = 6)
    private Integer pincode;

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
    private Long supervisorId;

    @Column(nullable = false)
    private String isPotential = "NO";

    @Column
    private Long modifiedBy;

    @OneToMany(mappedBy = "customerRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CustomerRequestComment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdDate = Timestamp.valueOf(LocalDateTime.now());
        assignedDate = Timestamp.valueOf(LocalDateTime.now());
    }

    protected void onUpdate() {
        modifiedDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
