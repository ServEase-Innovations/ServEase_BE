package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;
// import com.cus.customertab.enums.Gender;
// import com.cus.customertab.enums.Habit;
// import com.cus.customertab.enums.ServiceType;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {
    CustomerRequestMapper INSTANCE = Mappers.getMapper(CustomerRequestMapper.class);

    CustomerRequestDTO customerRequestToDTO(CustomerRequest customerRequest);
    CustomerRequest dtoToCustomerRequest(CustomerRequestDTO customerRequestDTO);

    // Default methods for mapping enums from Strings
    // default ServiceType mapServiceType(String serviceType) {
    //     return serviceType != null ? ServiceType.valueOf(serviceType.toUpperCase()) : null;
    // }

    // default Gender mapGender(String gender) {
    //     return gender != null ? Gender.valueOf(gender.toUpperCase()) : null; 
    // }

    // default Habit mapCookingHabit(String cookingHabit) {
    //     return cookingHabit != null ? Habit.valueOf(cookingHabit.toUpperCase()) : null; 
    // }

    // default Habit mapDietryHabit(String dietaryHabit) {
    //     return dietaryHabit != null ? Habit.valueOf(dietaryHabit.toUpperCase()) : null; 
    // }

    // public static <E extends Enum<E>> E getEnumSafe(Class<E> enumClass, String value) {
    //     if (value == null || value.isEmpty()) {
    //         return null;
    //     }
        
    //     try {
    //         return Enum.valueOf(enumClass, value);
    //     } catch (IllegalArgumentException e) {
    //         // Handle the case where the value is not a valid enum constant
    //         System.out.println("Invalid value for enum " + enumClass.getSimpleName() + ": " + value);
    //         return null;
    //     }
    // }
}
