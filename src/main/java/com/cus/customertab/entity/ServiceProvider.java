package com.cus.customertab.entity;

import java.sql.Timestamp;

import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.enums.Speciality;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "serviceprovider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long serviceproviderId;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, length = 10)
    private Long mobileNo;

    @Column(length = 10)
    private Long alternateNo;

    @Column(nullable = false)
    private String emailId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String buildingName;

    @Column(nullable = false)
    private String locality;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 6)
    private Integer pincode;

    @Column(nullable = false)

    private String currentLocation;
    private String nearbyLocation;

    private Timestamp enrolledDate;

    @Lob
    private byte[] profilePic;

    private String idNo;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private ServiceType housekeepingRole;

    private double rating;

    @Enumerated(EnumType.STRING)
    private LanguageKnown languageKnown;

    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @Column
    private Integer age;

    // to automatically set data and isActive field
    @PrePersist
    public void prePersist() {
        this.enrolledDate = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
    }

    // to deactivate
    public void deactivate() {
        this.isActive = false;
    }

}
