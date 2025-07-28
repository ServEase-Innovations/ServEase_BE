package com.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderUsedCouponDTO {
    private ServiceProviderCouponIdDTO id;
    private Timestamp availedOn;
    private Integer availedAmount;
}
