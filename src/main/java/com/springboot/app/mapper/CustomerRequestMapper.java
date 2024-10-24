package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.entity.CustomerRequest;

@Mapper(componentModel = "spring", uses = CustomerRequestCommentMapper.class)
public interface CustomerRequestMapper {
    CustomerRequestMapper INSTANCE = Mappers.getMapper(CustomerRequestMapper.class);

    CustomerRequestDTO customerRequestToDTO(CustomerRequest customerRequest);

    CustomerRequest dtoToCustomerRequest(CustomerRequestDTO customerRequestDTO);
}
