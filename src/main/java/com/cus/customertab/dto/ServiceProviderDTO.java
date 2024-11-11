package com.cus.customertab.dto;

import java.sql.Timestamp;

import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.LanguageKnown;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.enums.Speciality;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ServiceProviderDTO {

    private Long serviceproviderId;

    private String firstName;

    private String middleName;

    private String lastName;

    private Long mobileNo;

    private Long alternateNo;

    private String emailId;

    private Gender gender;

    private String buildingName;

    private String locality;

    private String street;

    private Integer pincode;

    private String currentLocation;

    private String nearbyLocation;

    private Timestamp enrolledDate;

    private byte[] profilePic;

    private boolean isActive;

    private ServiceType housekeepingRole;

    // private DocumentType KYC;

    private String idNo;

    private double rating;
    private LanguageKnown languageKnown;
    private Speciality speciality;
    private Integer age;

}
