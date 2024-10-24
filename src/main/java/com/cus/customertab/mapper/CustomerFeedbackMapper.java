package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.CustomerFeedbackDTO;
import com.cus.customertab.entity.CustomerFeedback;
import com.cus.customertab.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface CustomerFeedbackMapper {
    CustomerFeedbackMapper INSTANCE = Mappers.getMapper(CustomerFeedbackMapper.class);

    // Map from CustomerFeedback entity to CustomerFeedbackDTO
    @Mapping(target = "serviceProviderId", source = "serviceProvider.serviceProviderId") 
    CustomerFeedbackDTO customerFeedbackToDTO(CustomerFeedback customerFeedback);

    // Map from CustomerFeedbackDTO to CustomerFeedback entity
    @Mapping(target = "serviceProvider.serviceProviderId", source = "serviceProviderId") 
    CustomerFeedback dtoToCustomerFeedback(CustomerFeedbackDTO customerFeedbackDTO);

    // Default mapping method for converting Long to ServiceProvider
    default ServiceProvider mapIdToServiceProvider(Long id) {
        if (id == null) {
            return null;
        }
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceProviderId(id); 
        return serviceProvider;
    }
}