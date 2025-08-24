package com.springboot.app.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCouponId implements Serializable {
    private Long engagementId; // changed from customerId
    private Long couponId;
}
