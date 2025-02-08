package com.springboot.app.mapper;

import com.springboot.app.dto.LeaveBalanceDTO;
import com.springboot.app.entity.LeaveBalance;
import com.springboot.app.entity.ServiceProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LeaveBalanceMapper {

    LeaveBalanceMapper INSTANCE = Mappers.getMapper(LeaveBalanceMapper.class);

    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "leaveType", target = "leaveType")
    LeaveBalanceDTO leaveBalanceToDTO(LeaveBalance leaveBalance);

    @Mapping(target = "serviceProvider", expression = "java(mapServiceProviderId(leaveBalanceDTO.getServiceProviderId()))")
    @Mapping(source = "leaveType", target = "leaveType")
    LeaveBalance dtoToLeaveBalance(LeaveBalanceDTO leaveBalanceDTO);

    default ServiceProvider mapServiceProviderId(Long serviceProviderId) {
        if (serviceProviderId == null) {
            return null;
        }
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceproviderId(serviceProviderId);
        return serviceProvider;
    }
}
