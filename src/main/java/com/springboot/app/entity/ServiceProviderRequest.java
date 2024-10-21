package com.springboot.app.entity;

import java.util.List;
import jakarta.persistence.CascadeType;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "serviceproviderrequest")

public class ServiceProviderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long requestId;

    @Column
    private Long serviceProviderId;

    @Column(nullable = false, updatable = false)
    private Timestamp createdOn;

    @Column(nullable = false, updatable = false)
    private Timestamp modifiedOn;

    @Column
    private Long supervisorId;

    @Column(nullable = false)
    private String isResolved = "NO";

    @Column
    private Long resolvedBy;

    @Column(nullable = true, updatable = true)
    private Timestamp resolvedOn;

    @Column(nullable = false)
    private String timeSlotlist;

    @Column
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousekeepingRole housekeepingRole;

    @Column(nullable = false)
    private String isPotential = "NO";
    // Bidirectional relationship
    @OneToMany(mappedBy = "serviceProviderRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ServiceProviderRequestComment> comments;

    @PrePersist
    protected void onCreate() {
        createdOn = Timestamp.valueOf(LocalDateTime.now());
        modifiedOn = Timestamp.valueOf(LocalDateTime.now()); // Set modifiedOn during creation
        resolvedOn = Timestamp.valueOf(LocalDateTime.now());

    }

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = Timestamp.valueOf(LocalDateTime.now()); // Update modifiedOn whenever the entity is updated
    }

}
