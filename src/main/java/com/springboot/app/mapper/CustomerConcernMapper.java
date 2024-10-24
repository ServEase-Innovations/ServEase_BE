package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerConcernDTO;
import com.springboot.app.entity.CustomerConcern;

@Mapper(componentModel = "spring")
public interface CustomerConcernMapper {
    CustomerConcernMapper INSTANCE = Mappers.getMapper(CustomerConcernMapper.class);

    CustomerConcernDTO customerConcernToDTO(CustomerConcern customerConcern);

    CustomerConcern dtoToCustomerConcern(CustomerConcernDTO customerConcernDTO);
}
