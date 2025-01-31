package com.springboot.app.mapper;

import com.springboot.app.dto.ServiceProviderLeaveDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderLeave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceProviderLeaveMapper {

    ServiceProviderLeaveMapper INSTANCE = Mappers.getMapper(ServiceProviderLeaveMapper.class);

    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceproviderId")
    @Mapping(source = "backupBy.serviceproviderId", target = "backupBy")
    ServiceProviderLeaveDTO serviceProviderLeaveToDTO(ServiceProviderLeave serviceProviderLeave);

    @Mapping(target = "serviceProvider", expression = "java(mapServiceProviderId(serviceProviderLeaveDTO.getServiceproviderId()))")
    @Mapping(target = "backupBy", expression = "java(mapServiceProviderId(serviceProviderLeaveDTO.getBackupBy()))")
    ServiceProviderLeave dtoToServiceProviderLeave(ServiceProviderLeaveDTO serviceProviderLeaveDTO);

    default ServiceProvider mapServiceProviderId(Long serviceproviderId) {
        if (serviceproviderId == null) {
            return null;
        }
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceproviderId(serviceproviderId);
        return serviceProvider;
    }

    default Long mapServiceProviderToId(ServiceProvider serviceProvider) {
        if (serviceProvider == null) {
            return null;
        }
        return serviceProvider.getServiceproviderId();
    }
}
