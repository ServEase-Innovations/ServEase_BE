package com.springboot.app.service;

import com.springboot.app.dto.ServiceProviderUsedCouponDTO;
import java.util.List;

public interface ServiceProviderUsedCouponService {
    String saveServiceProviderUsedCoupon(ServiceProviderUsedCouponDTO dto);

    List<ServiceProviderUsedCouponDTO> findAll(int page, Integer size);

}
