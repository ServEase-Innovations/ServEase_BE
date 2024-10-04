package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.cus.customertab.dto.CustomerConcernDTO;
import com.cus.customertab.entity.CustomerConcern;

@Mapper(componentModel = "spring")
public interface CustomerConcernMapper {
    CustomerConcernMapper INSTANCE = Mappers.getMapper(CustomerConcernMapper.class);

    CustomerConcernDTO customerConcernToDTO(CustomerConcern customerConcern);
    CustomerConcern dtoToCustomerConcern(CustomerConcernDTO customerConcernDTO);
}
