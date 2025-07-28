package com.springboot.app.dto;

import lombok.*;
import java.util.Date;

import com.springboot.app.enums.CouponStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {

    private Long id;
    private String couponCode;
    private Integer maxDiscountAmount;
    private Integer discountPercent;
    private Integer fixedDiscountAmount;
    private Integer validityPeriod;
    private Date startDate;
    private Date expiryDate;
    private Long counter;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;
    private CouponStatus status;
    private Long totalAvailedAmount;
    private Integer countryCode;
    private Integer isValid;
    private Integer toBeAvailedBy;
    private String description;
    private String currencyCode;
}
