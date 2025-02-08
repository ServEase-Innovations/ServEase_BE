package com.springboot.app.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorDTO {

    private Long vendorId;
    private String companyName;
    private String registrationId;
    private String emailId;
    private Long phoneNo;
    private String address;
    private boolean isActive;
    private String password;
    private String username;
    private Timestamp createdDate;
}
