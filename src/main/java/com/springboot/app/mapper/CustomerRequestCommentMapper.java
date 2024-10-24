package com.springboot.app.mapper;

import com.springboot.app.dto.CustomerRequestCommentDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.entity.CustomerRequestComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerRequestCommentMapper {
    CustomerRequestCommentMapper INSTANCE = Mappers.getMapper(CustomerRequestCommentMapper.class);

    @Mapping(source = "customerRequest.requestId", target = "requestId")
    CustomerRequestCommentDTO customerRequestCommentToDTO(CustomerRequestComment comment);

    @Mapping(source = "requestId", target = "customerRequest.requestId")
    CustomerRequestComment dtoToCustomerRequestComment(CustomerRequestCommentDTO commentDTO);

    // Custom mapping method for CustomerRequest to Long
    default Long map(CustomerRequest customerRequest) {
        return customerRequest != null ? customerRequest.getRequestId() : null;
    }
}
