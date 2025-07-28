package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "SERVICE_PROVIDER_USED_COUPONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderUsedCoupon {

    @EmbeddedId
    private ServiceProviderCouponId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("serviceProviderId") // Match the field name in composite key
    @JoinColumn(name = "service_provider_id") // Match DB column name
    private ServiceProvider serviceProvider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id") // Match DB column name
    private Coupon coupon;

    @Column(name = "availed_on")
    private Timestamp availedOn;

    @Column(name = "availed_amount")
    private Integer availedAmount;
}
