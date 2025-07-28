package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

import com.springboot.app.enums.CouponStatus;

@Entity
@Table(name = "COUPONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COUPON_CODE", length = 30, unique = true, nullable = false)
    private String couponCode;

    @Column(name = "MAX_DISC_AMOUNT")
    private Integer maxDiscountAmount;

    @Column(name = "DISCOUNT_PERCENT")
    private Integer discountPercent;

    @Column(name = "FIXED_DISC_AMT")
    private Integer fixedDiscountAmount;

    @Column(name = "VALIDITY_PERIOD")
    private Integer validityPeriod;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "EXPIRY_DATE")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Column(name = "COUNTER")
    private Long counter;

    @Column(name = "CREATED_BY", length = 30)
    private String createdBy;

    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "MODIFIED_BY", length = 30)
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30)
    private CouponStatus status;

    @Column(name = "TOTAL_AVAILED_AMT")
    private Long totalAvailedAmount;

    @Column(name = "COUNTRY_CODE")
    private Integer countryCode;

    @Column(name = "IS_VALID")
    private Integer isValid; // 1: Valid, 0: Invalid

    @Column(name = "TO_BE_AVAILED_BY")
    private Integer toBeAvailedBy; // 1: Customer, 2: ServiceProvider

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "CURRENCY_CODE", length = 3)
    private String currencyCode;
}
