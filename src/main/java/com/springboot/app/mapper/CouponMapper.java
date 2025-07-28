package com.springboot.app.mapper;

import com.springboot.app.dto.CouponDTO;
import com.springboot.app.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponMapper INSTANCE = Mappers.getMapper(CouponMapper.class);

    CouponDTO couponToDTO(Coupon coupon);

    Coupon dtoToCoupon(CouponDTO couponDTO);
}
