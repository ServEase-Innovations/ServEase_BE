package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.dto.VendorDTO;
import com.springboot.app.entity.Vendor;
import com.springboot.app.enums.UserRole;
import com.springboot.app.exception.VendorRegistrationException;
import com.springboot.app.mapper.VendorMapper;
import com.springboot.app.repository.VendorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final UserCredentialsService userCredentialsService;

    @Autowired
    public VendorServiceImpl(VendorRepository vendorRepository,
            VendorMapper vendorMapper,
            UserCredentialsService userCredentialsService) {
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
        this.userCredentialsService = userCredentialsService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDTO> getAllVendorDTOs(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching vendors with page: {} and size: {}", page, size);
        }

        // Fetch paginated results using Spring Data JPA
        Pageable pageable = PageRequest.of(page, size);
        List<Vendor> vendors = vendorRepository.findAll(pageable).getContent();
        if (logger.isDebugEnabled()) {
            logger.debug("Number of vendors fetched: {}", vendors.size());
        }

        // Check if no vendors are found and log a warning
        if (vendors.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No vendors found on the requested page.");
            }
            return new ArrayList<>(); // Return empty list if no vendors found
        }

        // Map entities to DTOs
        return vendors.stream()
                .map(vendorMapper::vendorToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDTO getVendorDTOById(Long id) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching vendor by ID: {}", id);
        }

        return vendorRepository.findById(id)
                .map(vendorMapper::vendorToDTO)
                .orElseThrow(() -> {
                    if (logger.isErrorEnabled()) {

                        logger.error("No vendor found with ID: {}", id);
                    }
                    return new RuntimeException("Vendor not found.");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VendorDTO> getVendorDTOByCompanyName(String companyName) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching vendor by company name: {}", companyName);
        }

        Optional<Vendor> vendorOptional = vendorRepository.findByCompanyNameIgnoreCase(companyName);

        if (vendorOptional.isPresent()) {
            return Optional.of(vendorMapper.vendorToDTO(vendorOptional.get()));
        } else {
            return Optional.empty(); // Return empty if not found
        }
    }

    @Override
    @Transactional
    public Long saveVendorDTO(VendorDTO vendorDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Saving new vendor");
        }
        // Step 1: Validate and check if the vendor's email or phone number already
        // exists
        String email = vendorDTO.getEmailId();
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("EmailId is required to save a vendor.");
        }

        if (vendorRepository.existsByEmailId(email)) {
            throw new IllegalArgumentException("Vendor with this email already exists.");
        }

        Long phoneNo = vendorDTO.getPhoneNo();
        if (phoneNo == null) {
            throw new IllegalArgumentException("Phone number is required to save a vendor.");
        }

        if (vendorRepository.existsByPhoneNo(phoneNo)) {
            throw new IllegalArgumentException("Vendor with this phone number already exists.");
        }
        // Step 2: Set username from email
        vendorDTO.setUsername(email); //

        // Step 4: Register user credentials for vendor
        UserCredentialsDTO userDTO = new UserCredentialsDTO(
                vendorDTO.getEmailId(), // Use email as username
                vendorDTO.getPassword(), // Assume vendorDTO has a password field
                true, // Account is active
                0, // Login attempts
                null, // Last login date (if applicable)
                false, // Account is not locked
                vendorDTO.getPhoneNo().toString(), // Vendor's phone number
                null, // Any additional metadata (optional)
                UserRole.VENDOR.getValue()// Use the UserRole for Vendor
        );

        String registrationResponse = userCredentialsService.saveUserCredentials(userDTO);

        if (!"Registration successful!".equalsIgnoreCase(registrationResponse)) {
            throw new VendorRegistrationException("Vendor registration failed: " + registrationResponse);
        }

        // Step 4: Map VendorDTO to Vendor entity and save it
        Vendor vendor = vendorMapper.dtoToVendor(vendorDTO);
        vendor.setActive(true); // Set the vendor as active by default

        // Save the vendor and retrieve the ID
        Vendor savedVendor = vendorRepository.save(vendor);
        Long vendorId = savedVendor.getVendorId();

        logger.info("Vendor saved with ID: {}", vendorId);

        // Return the vendor's ID
        return vendorId;

    }

    @Override
    @Transactional
    public String updateVendorDTO(VendorDTO vendorDTO) {
        if (logger.isInfoEnabled()) {

            logger.info("Updating vendor with ID: {}", vendorDTO.getVendorId());
        }

        if (vendorRepository.existsById(vendorDTO.getVendorId())) {
            Vendor existingVendor = vendorMapper.dtoToVendor(vendorDTO);
            vendorRepository.save(existingVendor);
            if (logger.isInfoEnabled()) {
                logger.info("Vendor updated with ID: {}", vendorDTO.getVendorId());
            }
            return ServiceProviderConstants.VENDOR_UPDATED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Vendor not found for update with ID: {}", vendorDTO.getVendorId());
            }
            return ServiceProviderConstants.VENDOR_NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deleteVendorDTO(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deactivating vendor with ID: {}", id);
        }

        if (vendorRepository.existsById(id)) {
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> {
                        if (logger.isErrorEnabled()) {
                            logger.error("Vendor not found with ID: {}", id);
                        }
                        return new RuntimeException("Vendor not found.");
                    });

            vendor.deactivate(); // Deactivate vendor
            vendorRepository.save(vendor);
            if (logger.isInfoEnabled()) {
                logger.info("Vendor with ID {} deactivated", id);
            }
            return ServiceProviderConstants.VENDOR_DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Vendor not found for deactivation with ID: {}", id);
            }
            return ServiceProviderConstants.VENDOR_NOT_FOUND;
        }
    }
}
