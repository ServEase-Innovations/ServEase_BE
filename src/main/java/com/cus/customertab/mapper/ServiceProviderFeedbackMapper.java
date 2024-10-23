package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderFeedbackDTO;
import com.cus.customertab.entity.ServiceProviderFeedback;


@Mapper(componentModel = "spring")
public interface ServiceProviderFeedbackMapper {

    ServiceProviderFeedbackMapper INSTANCE = Mappers.getMapper(ServiceProviderFeedbackMapper.class);

    ServiceProviderFeedbackDTO toDTO(ServiceProviderFeedback serviceProviderFeedback);

    ServiceProviderFeedback toEntity(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO);
}