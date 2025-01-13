package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.entity.CustomerRequest;

@Mapper(componentModel = "spring", uses = CustomerRequestCommentMapper.class)
public interface CustomerRequestMapper {
    CustomerRequestMapper INSTANCE = Mappers.getMapper(CustomerRequestMapper.class);

    @Mapping(source = "status", target = "status")
    CustomerRequestDTO customerRequestToDTO(CustomerRequest customerRequest);

    @Mapping(source = "status", target = "status")
    CustomerRequest dtoToCustomerRequest(CustomerRequestDTO customerRequestDTO);
}
