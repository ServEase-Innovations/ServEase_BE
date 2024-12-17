package com.springboot.app.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.springboot.app.enums.DocumentType;
//import com.springboot.app.enums.DocumentType;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Habit;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

//import jakarta.persistence.Column;
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

    // private byte[] profilePic;
    private String profilePic;

    private boolean isActive;

    private HousekeepingRole housekeepingRole;

    private Habit diet;

    private Habit cookingSpeciality;

    private DocumentType KYC;

    private String idNo;
    // private String profilePicUrl;
    private double rating;
    private LanguageKnown languageKnown;
    private Speciality speciality;
    private Integer age;
    private String info;
    private LocalDate DOB;
    private double expectedSalary = 0.0;
    private Integer experience;
    private String username;
    private String password;

}
