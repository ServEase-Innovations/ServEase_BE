package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerCouponIdDTO;
import com.springboot.app.entity.CustomerCouponId;

@Mapper(componentModel = "spring")
public interface CustomerCouponIdMapper {
    CustomerCouponIdMapper INSTANCE = Mappers.getMapper(CustomerCouponIdMapper.class);

    CustomerCouponIdDTO toDTO(CustomerCouponId id);

    CustomerCouponId toEntity(CustomerCouponIdDTO dto);
}
