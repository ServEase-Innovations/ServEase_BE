package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.entity.ServiceProviderRequest;

@Mapper(componentModel = "spring")
public interface ServiceProviderRequestCommentMapper {

    ServiceProviderRequestCommentMapper INSTANCE = Mappers.getMapper(ServiceProviderRequestCommentMapper.class);

    @Mapping(source = "serviceProviderRequest.requestId", target = "requestId")
    ServiceProviderRequestCommentDTO toDTO(ServiceProviderRequestComment serviceProviderRequestComment);

    @Mapping(source = "requestId", target = "serviceProviderRequest.requestId")
    ServiceProviderRequestComment toEntity(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO);

    // Add this method to update existing entity from DTO
    @Mapping(source = "requestId", target = "serviceProviderRequest.requestId")
    void updateEntityFromDTO(ServiceProviderRequestCommentDTO dto, @MappingTarget ServiceProviderRequestComment entity);

    // Custom method to map the ServiceProviderRequest entity to the requestId field
    // in DTO
    default Long map(ServiceProviderRequest serviceProviderRequest) {
        return serviceProviderRequest != null ? serviceProviderRequest.getRequestId() : null;
    }
}
