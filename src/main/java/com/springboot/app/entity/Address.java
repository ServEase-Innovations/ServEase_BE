package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String field1; // e.g. Building/House No.
    private String field2; // e.g. Street/Landmark
    private String ctArea; // City / Area
    private String pinNo; // Pincode
    private String state;
    private String country;
}
