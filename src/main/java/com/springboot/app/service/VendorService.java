package com.springboot.app.service;

import java.util.List;
import java.util.Optional;

import com.springboot.app.dto.VendorDTO;

public interface VendorService {

    // Retrieve all VendorDTOs with pagination
    List<VendorDTO> getAllVendorDTOs(int page, int size);

    // Retrieve a single VendorDTO by its ID
    VendorDTO getVendorDTOById(Long id);

    // Retrieve a VendorDTO by company name
    Optional<VendorDTO> getVendorDTOByCompanyName(String companyName);

    // Save a new VendorDTO (create a new vendor)
    public Long saveVendorDTO(VendorDTO vendorDTO);

    // Update an existing VendorDTO
    String updateVendorDTO(VendorDTO vendorDTO);

    // Delete a VendorDTO by its ID
    String deleteVendorDTO(Long id);
}
