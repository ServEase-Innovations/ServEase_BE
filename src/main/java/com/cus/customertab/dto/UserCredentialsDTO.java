package com.cus.customertab.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialsDTO {

    public String username;
    private String password;
    public boolean isActive;
    public int noOfTries;
    public Timestamp disableTill;
    public boolean isTempLocked;
    public String phoneNumber;
    public Timestamp lastLogin;
}