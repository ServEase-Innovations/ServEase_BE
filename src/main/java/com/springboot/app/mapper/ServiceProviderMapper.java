package com.springboot.app.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    // Map ServiceProvider entity to ServiceProviderDTO
    // @Mapping(target = "profilePic", source = "profilePic")
    // @Mapping(target = "profilePicUrl", expression =
    // "java(mapToBase64(serviceProvider.getProfilePic()))")
    @Mapping(target = "username", ignore = true) // Explicitly ignore
    @Mapping(target = "password", ignore = true) // Explicitly ignore
    // @Mapping(target = "occupiedTimeSlots", expression =
    // "java(calculateOccupiedTimes(serviceProvider.getTimeslot()))")
    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    // Map ServiceProviderDTO to ServiceProvider entity
    // @Mapping(target = "profilePic", source = "profilePic")
    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

    void updateServiceProviderFromDTO(ServiceProviderDTO serviceProviderDTO,
            @MappingTarget ServiceProvider existingServiceProvider);

    // Maps a list of ServiceProviders to a list of ServiceProviderDTOs
    List<ServiceProviderDTO> serviceProvidersToDTOs(List<ServiceProvider> serviceProviders);

    // Add this helper method to calculate available times from the timeslot
    default List<String> calculateAvailableTimes(String busyTimeRange) {
        // If the timeslot is null, return an empty list
        if (busyTimeRange == null || busyTimeRange.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> availableTimes = new ArrayList<>();
        String[] range = busyTimeRange.split("-");
        if (range.length == 2) {
            int startHour = parseHour(range[0]);
            int endHour = parseHour(range[1]);

            for (int hour = 0; hour < 24; hour++) {
                if (hour < startHour || hour >= endHour) {
                    availableTimes.add(String.format("%02d:00", hour));
                }
            }
        }
        return availableTimes;
    }

    // Helper method to parse hours from the "HH:mm" format
    default int parseHour(String time) {
        String[] parts = time.split(":");
        if (parts.length == 2) {
            return Integer.parseInt(parts[0]);
        }
        return 0; // Default to 0 if parsing fails
    }

}
// @Mapping(target = "availableTimeSlots", expression =
// "java(calculateAvailableTimes(serviceProvider.getTimeslot()))")
