package com.springboot.app.service;

import com.springboot.app.dto.CouponDTO;
import java.util.List;

public interface CouponService {

    String createCoupon(CouponDTO couponDTO);

    CouponDTO getCouponById(Long id);

    List<CouponDTO> getAllCoupons(int page, int size);

    String updateCoupon(Long id, CouponDTO couponDTO);

    String deleteCoupon(Long id);

    CouponDTO getByCouponCode(String code); // Optional, if needed
}
