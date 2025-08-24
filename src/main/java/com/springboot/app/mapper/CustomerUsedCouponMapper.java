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

    // Convert DTO -> Entity (with engagementId instead of customerId)
    default CustomerCouponId map(CustomerCouponIdDTO dto) {
        if (dto == null) {
            return null;
        }
        return new CustomerCouponId(dto.getEngagementId(), dto.getCouponId());
    }

    // Convert Entity -> DTO (with engagementId instead of customerId)
    default CustomerCouponIdDTO map(CustomerCouponId entity) {
        if (entity == null) {
            return null;
        }
        return new CustomerCouponIdDTO(entity.getEngagementId(), entity.getCouponId());
    }
}
