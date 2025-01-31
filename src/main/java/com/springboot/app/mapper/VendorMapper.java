package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
//port org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
//import org.mapstruct.Mapping;

import com.springboot.app.dto.VendorDTO;
import com.springboot.app.entity.Vendor;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);

    // Map Vendor entity to VendorDTO
    // Mapping(target = "companyName", expression =
    // "java(vendor.getCompanyName().toUpperCase())")
    @Mapping(target = "username", ignore = true) // Explicitly ignore
    @Mapping(target = "password", ignore = true) // Explicitly ignore
    VendorDTO vendorToDTO(Vendor vendor);

    // Map VendorDTO to Vendor entity
    // Mapping(target = "companyName", expression =
    // "java(vendorDTO.getCompanyName().toLowerCase())")
    // @Mapping(source = "createdDate", target = "createdDate")
    Vendor dtoToVendor(VendorDTO vendorDTO);

    // Update existing Vendor entity with data from VendorDTO
    // @Mapping(source = "createdDate", target = "createdDate")
    void updateVendorFromDTO(VendorDTO vendorDTO, @MappingTarget Vendor existingVendor);
}
