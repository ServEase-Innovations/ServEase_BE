package com.springboot.app.dto;

import java.sql.Timestamp;

import com.springboot.app.enums.Gender;

import com.springboot.app.enums.DocumentType;

import com.springboot.app.enums.LanguageKnown;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private Long customerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long mobileNo;
    private Long alternateNo;
    private String emailId;
    private Gender gender;
    private LanguageKnown languageKnown;
    private String buildingName;
    private String locality;
    private String street;
    private Integer pincode;
    private byte[] profilePic;
    private String currentLocation;
    private DocumentType KYC;
    private String idNo;
    private boolean isActive;
    private Double rating; // Field to store the average rating

    private Timestamp enrolledDate;
}
