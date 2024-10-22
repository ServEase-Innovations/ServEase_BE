package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.entity.ServiceProviderRequest;

@Mapper(componentModel = "spring", uses = ServiceProviderRequestCommentMapper.class)
public interface ServiceProviderRequestMapper {

    ServiceProviderRequestMapper INSTANCE = Mappers.getMapper(ServiceProviderRequestMapper.class);

    ServiceProviderRequestDTO serviceProviderRequestToDTO(ServiceProviderRequest serviceProviderRequest);

    ServiceProviderRequest dtoToServiceProviderRequest(ServiceProviderRequestDTO serviceProviderRequestDTO);
}
