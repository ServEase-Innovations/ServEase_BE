package com.springboot.app.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String apartment;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
