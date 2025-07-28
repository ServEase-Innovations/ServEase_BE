package com.springboot.app.mapper;

import com.springboot.app.dto.ServiceProviderCouponIdDTO;
import com.springboot.app.dto.ServiceProviderUsedCouponDTO;
import com.springboot.app.entity.ServiceProviderCouponId;
import com.springboot.app.entity.ServiceProviderUsedCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceProviderUsedCouponMapper {

    @Mapping(target = "id", source = "id")
    ServiceProviderUsedCoupon dtoToEntity(ServiceProviderUsedCouponDTO dto);

    @Mapping(target = "id", source = "id")
    ServiceProviderUsedCouponDTO entityToDto(ServiceProviderUsedCoupon entity);

    default ServiceProviderCouponId map(ServiceProviderCouponIdDTO dto) {
        return new ServiceProviderCouponId(dto.getServiceProviderId(), dto.getCouponId());
    }

    default ServiceProviderCouponIdDTO map(ServiceProviderCouponId entity) {
        return new ServiceProviderCouponIdDTO(entity.getServiceProviderId(), entity.getCouponId());
    }
}
