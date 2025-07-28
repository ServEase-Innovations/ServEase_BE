package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderCouponIdDTO;
import com.springboot.app.entity.ServiceProviderCouponId;

@Mapper(componentModel = "spring")
public interface ServiceProviderCouponIdMapper {

    ServiceProviderCouponIdMapper INSTANCE = Mappers.getMapper(ServiceProviderCouponIdMapper.class);

    ServiceProviderCouponIdDTO toDTO(ServiceProviderCouponId id);

    ServiceProviderCouponId toEntity(ServiceProviderCouponIdDTO dto);
}
