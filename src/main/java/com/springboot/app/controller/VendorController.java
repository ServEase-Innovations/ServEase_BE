package com.springboot.app.controller;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.VendorDTO;
import com.springboot.app.service.VendorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/vendors")
@Api(value = "Vendor Management", tags = "Vendor Management API")
public class VendorController {

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // API to retrieve all vendors
    @GetMapping("/all")
    @ApiOperation(value = ServiceProviderConstants.RETRIEVE_ALL_VENDOR_DESC, response = List.class)
    public ResponseEntity<List<VendorDTO>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        if (size == null) {
            size = ServiceProviderConstants.DEFAULT_PAGE_SIZE; // Default page size if not provided
        }
        List<VendorDTO> vendors = vendorService.getAllVendorDTOs(page, size);
        // if (vendors.isEmpty()) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(Collections.singletonList(new VendorDTO())); // Empty response if no vendors found
        // }
        return ResponseEntity.ok(vendors);
    }

    // API to get vendor by id
    @GetMapping("/get/{id}")
    @ApiOperation(value = ServiceProviderConstants.GET_VENDOR_BY_ID_DESC, response = VendorDTO.class)
    public ResponseEntity<VendorDTO> getVendorById(
            @ApiParam(value = "ID of the vendor to retrieve", required = true) @PathVariable Long id) {
        VendorDTO vendorDTO = vendorService.getVendorDTOById(id);
        if (vendorDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Vendor not found
        }
        return ResponseEntity.ok(vendorDTO);
    }

    @GetMapping("/getByCompanyName")
    @ApiOperation(value = "Retrieve vendor by company name", response = VendorDTO.class)
    public ResponseEntity<VendorDTO> getVendorByCompanyName(
            @ApiParam(value = "Company name of the vendor to retrieve", required = true) @RequestParam String companyName) {
        Optional<VendorDTO> vendorDTOOptional = vendorService.getVendorDTOByCompanyName(companyName);

        if (vendorDTOOptional.isPresent()) {
            return ResponseEntity.ok(vendorDTOOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Vendor not found
        }
    }

    // API to add a new vendor
    @PostMapping("/add")
    @ApiOperation(value = ServiceProviderConstants.ADD_NEW_VENDOR_DESC)
    public ResponseEntity<String> addVendor(@RequestBody VendorDTO vendorDTO) {
        try {
            // Save the vendor and retrieve the ID
            Long vendorId = vendorService.saveVendorDTO(vendorDTO);

            // Return the ID in the response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Vendor added successfully with ID: " + vendorId);
        } catch (IllegalArgumentException ex) {
            // Return conflict if the vendor already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ServiceProviderConstants.VENDOR_ALREADY_EXISTS);
        } catch (Exception ex) {
            // Return internal server error for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceProviderConstants.VENDOR_ERROR);
        }
    }

    // API to update vendor
    @PutMapping("/update/{id}")
    @ApiOperation(value = ServiceProviderConstants.UPDATE_VENDOR_DESC)
    public ResponseEntity<String> updateVendor(
            @ApiParam(value = "ID of the vendor to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated vendor object", required = true) @RequestBody VendorDTO vendorDTO) {
        vendorDTO.setVendorId(id);
        String updateResponse = vendorService.updateVendorDTO(vendorDTO);
        if (updateResponse.equals(ServiceProviderConstants.VENDOR_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.VENDOR_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.VENDOR_UPDATED);
    }

    // API to delete vendor
    @PatchMapping("/delete/{id}")
    @ApiOperation(value = ServiceProviderConstants.DELETE_VENDOR_DESC)
    public ResponseEntity<String> deleteVendor(
            @ApiParam(value = "ID of the vendor to deactivate", required = true) @PathVariable Long id) {
        String deleteResponse = vendorService.deleteVendorDTO(id);
        if (deleteResponse.equals(ServiceProviderConstants.VENDOR_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.VENDOR_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.VENDOR_DELETED);
    }
}
