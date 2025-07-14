package com.springboot.app.entity;

import java.sql.Timestamp;
import com.springboot.app.enums.DocumentType;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

import ch.hsr.geohash.GeoHash;
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

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(length = 10, unique = true)
    private Long mobileNo;

    @Column(length = 10)
    private Long alternateNo;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
    private String buildingName;

    @Column
    private String locality;

    @Column
    private String street;

    @Column(length = 6)
    private Integer pincode;

    @Column
    private String currentLocation;

    private Timestamp enrolledDate;

    @Enumerated(EnumType.STRING)
    private LanguageKnown languageKnown;

    @Column
    private String profilePic;

    @Enumerated(EnumType.STRING)
    private DocumentType KYC;

    private String idNo;

    private double rating;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @PrePersist
    public void prePersist() {
        this.enrolledDate = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
    }

}