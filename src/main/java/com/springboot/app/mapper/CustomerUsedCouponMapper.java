package com.springboot.app.mapper;

import com.springboot.app.dto.CustomerCouponIdDTO;
import com.springboot.app.dto.CustomerUsedCouponDTO;
import com.springboot.app.entity.CustomerCouponId;
import com.springboot.app.entity.CustomerUsedCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerUsedCouponMapper {

    @Mapping(target = "id", source = "id")
    CustomerUsedCoupon dtoToEntity(CustomerUsedCouponDTO dto);

    @Mapping(target = "id", source = "id")
    CustomerUsedCouponDTO entityToDto(CustomerUsedCoupon entity);

    default CustomerCouponId map(CustomerCouponIdDTO dto) {
        return new CustomerCouponId(dto.getCustomerId(), dto.getCouponId());
    }

    default CustomerCouponIdDTO map(CustomerCouponId entity) {
        return new CustomerCouponIdDTO(entity.getCustomerId(), entity.getCouponId());
    }
}
