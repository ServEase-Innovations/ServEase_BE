package com.springboot.app.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String field1; // e.g. Building/House No.
    private String field2; // e.g. Street/Landmark
    private String ctArea; // City / Area
    private String pinNo; // Pincode
    private String state;
    private String country;
}
