package com.cus.customertab.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CustomerConcern")
public class CustomerConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long customerId; 

    @Column(length = 500)
    private String feedback;

    @Column(nullable = false, updatable = false)
    private Timestamp createdOn; 

    @Column
    private Timestamp modifiedOn; 

    @Column(nullable = false)
    private String isResolved = "NO";

    @Column
    private Timestamp resolvedOn;

    @Column
    private Long resolvedBy;

    @Column(nullable = false)
    private String resolutionRequired = "NO";

    @Column(nullable = false)
    private Integer rating;

    @PrePersist
    protected void onCreate() {
        createdOn = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = Timestamp.valueOf(LocalDateTime.now());
    }
}
