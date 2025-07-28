package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "CUSTOMER_USED_COUPONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUsedCoupon {

    @EmbeddedId
    private CustomerCouponId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id") // match DB column
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id") // match DB column
    private Coupon coupon;

    @Column(name = "availed_on")
    private Timestamp availedOn;

    @Column(name = "availed_amount")
    private Integer availedAmount;
}
