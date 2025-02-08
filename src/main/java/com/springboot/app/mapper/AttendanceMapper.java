package com.springboot.app.mapper;

import com.springboot.app.dto.AttendanceDTO;
import com.springboot.app.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    // Map Attendance entity to AttendanceDTO

    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    AttendanceDTO attendanceToDTO(Attendance attendance);

    // Map AttendanceDTO to Attendance entity
    @Mapping(target = "serviceProvider", ignore = true) // Will be set manually
    @Mapping(target = "customer", ignore = true) // Will be set manually
    @Mapping(source = "attended", target = "attended")
    @Mapping(source = "customerAgreed", target = "customerAgreed") // Map 'isCustomerAgreed'
    @Mapping(source = "resolved", target = "resolved")
    Attendance dtoToAttendance(AttendanceDTO attendanceDTO);
}
