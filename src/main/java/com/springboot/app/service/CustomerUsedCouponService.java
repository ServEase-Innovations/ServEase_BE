package com.springboot.app.service;

import com.springboot.app.dto.CustomerUsedCouponDTO;
import java.util.List;

public interface CustomerUsedCouponService {
    String saveCustomerUsedCoupon(CustomerUsedCouponDTO dto);

    List<CustomerUsedCouponDTO> findAll(int page, Integer size);
}
