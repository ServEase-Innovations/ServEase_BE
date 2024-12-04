package com.springboot.app.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
import com.springboot.app.enums.UserRole;
import com.springboot.app.mapper.ServiceProviderMapper;
import com.springboot.app.repository.ServiceProviderRepository;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

        private static final Logger logger = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

        @Autowired
        private ServiceProviderRepository serviceProviderRepository;

        @Autowired
        private ServiceProviderMapper serviceProviderMapper;

        @Autowired
        private UserCredentialsService userCredentialsService;

        @Override
        @Transactional
        public List<ServiceProviderDTO> getAllServiceProviderDTOs(int page, int size) {
                logger.info("Fetching service providers with pagination - page: {}, size: {}", page, size);

                // Fetch paginated results using Spring Data JPA
                Page<ServiceProvider> serviceProvidersPage = serviceProviderRepository
                                .findAll(PageRequest.of(page, size));

                logger.debug("Fetched {} service provider(s) from the database.",
                                serviceProvidersPage.getTotalElements());

                // Map entities to DTOs
                return serviceProvidersPage.stream()
                                .map(serviceProviderMapper::serviceProviderToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ServiceProviderDTO getServiceProviderDTOById(Long id) {
                logger.info("Fetching service provider with ID: {}", id);

                ServiceProvider serviceProvider = serviceProviderRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Service provider with ID {} not found", id);
                                        return new RuntimeException(
                                                        ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
                                });

                return serviceProviderMapper.serviceProviderToDTO(serviceProvider);
        }

        @Override
        @Transactional
        public void saveServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
                logger.info("Saving a new service provider: {}", serviceProviderDTO);
                // Automatically set the username as the emailId
                String email = serviceProviderDTO.getEmailId();
                if (email == null || email.isEmpty()) {
                        throw new IllegalArgumentException("EmailId is required to save a service provider.");
                }
                serviceProviderDTO.setUsername(email);

                // Step 1: Register user credentials
                UserCredentialsDTO userDTO = new UserCredentialsDTO(
                                serviceProviderDTO.getUsername(),
                                serviceProviderDTO.getPassword(),
                                true, 0, null, false,
                                serviceProviderDTO.getMobileNo().toString(),
                                null,
                                UserRole.SERVICE_PROVIDER.getValue()

                );
                String registrationResponse = userCredentialsService.saveUserCredentials(userDTO);
                if (!"Registration successful!".equalsIgnoreCase(registrationResponse)) {
                        throw new RuntimeException("User registration failed: " + registrationResponse);
                }

                // Step 2: Save the service provider
                ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
                serviceProvider.setActive(true);
                serviceProviderRepository.save(serviceProvider);
                logger.debug("Service provider saved successfully: {}", serviceProvider);
        }

        @Override
        @Transactional
        public void updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
                logger.info("Updating service provider with ID: {}", serviceProviderDTO.getServiceproviderId());

                ServiceProvider existingServiceProvider = serviceProviderRepository
                                .findById(serviceProviderDTO.getServiceproviderId())
                                .orElseThrow(() -> {
                                        logger.warn("Service provider with ID {} not found for update",
                                                        serviceProviderDTO.getServiceproviderId());
                                        return new RuntimeException(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND
                                                        + serviceProviderDTO.getServiceproviderId());
                                });

                serviceProviderMapper.updateServiceProviderFromDTO(serviceProviderDTO, existingServiceProvider);
                serviceProviderRepository.save(existingServiceProvider);

                logger.debug("Service provider updated: {}", existingServiceProvider);
        }

        @Override
        @Transactional
        public void deleteServiceProviderDTO(Long id) {
                logger.info("Deactivating service provider with ID: {}", id);

                ServiceProvider serviceProvider = serviceProviderRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Service provider with ID {} not found for deletion", id);
                                        return new RuntimeException(
                                                        ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND + id);
                                });

                serviceProvider.deactivate();
                serviceProviderRepository.save(serviceProvider);

                logger.debug("Service provider with ID {} deactivated", id);

        }

        @Override
        @Transactional
        public List<ServiceProviderDTO> getfilters(LanguageKnown language, Double rating, Gender gender,
                        Speciality speciality, HousekeepingRole housekeepingRole, Integer minAge, Integer maxAge) {

                logger.info("Filtering service providers with specified criteria");

                // Build the Specification dynamically based on non-null parameters
                Specification<ServiceProvider> spec = Specification.where(null);

                if (language != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                                        .equal(root.get("languageKnown"), language));
                        logger.debug("Filtering by language: {}", language);
                }
                if (rating != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("rating"),
                                        rating));
                        logger.debug("Filtering by rating: {}", rating);
                }
                if (gender != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gender"),
                                        gender));
                        logger.debug("Filtering by gender: {}", gender);
                }
                if (speciality != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("speciality"),
                                        speciality));
                        logger.debug("Filtering by speciality: {}", speciality);
                }
                if (housekeepingRole != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                                        .equal(root.get("housekeepingRole"), housekeepingRole));
                        logger.debug("Filtering by housekeeping role: {}", housekeepingRole);
                }
                if (minAge != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                                        .greaterThanOrEqualTo(root.get("age"), minAge));
                        logger.debug("Filtering by minimum age: {}", minAge);
                }
                if (maxAge != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
                                        .lessThanOrEqualTo(root.get("age"), maxAge));
                        logger.debug("Filtering by maximum age: {}", maxAge);
                }

                // Execute the query using the Specification
                List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(spec);
                logger.debug("Found {} service providers matching the criteria", serviceProviders.size());

                // Convert the list of ServiceProvider entities to a list of ServiceProviderDTOs
                return serviceProviders.stream()
                                .map(serviceProviderMapper::serviceProviderToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public List<ServiceProviderDTO> getServiceProvidersByFilter(Integer pincode, String street, String locality) {
                logger.info("Fetching service providers with the given filter parameters - Pincode: {}, Street: {}, Locality: {}",
                                pincode, street, locality);

                // Start building the specification for dynamic filtering
                Specification<ServiceProvider> spec = Specification.where(null);

                if (pincode != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pincode"),
                                        pincode));
                        logger.debug("Filtering by pincode: {}", pincode);
                }
                if (street != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("street"),
                                        street));
                        logger.debug("Filtering by street: {}", street);
                }
                if (locality != null) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("locality"),
                                        locality));
                        logger.debug("Filtering by locality: {}", locality);
                }

                // Execute the query using the Specification
                List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(spec);
                logger.debug("Number of service providers found: {}", serviceProviders.size());

                // Convert the list of ServiceProvider entities to a list of ServiceProviderDTOs
                return serviceProviders.stream()
                                .map(serviceProviderMapper::serviceProviderToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public List<ServiceProviderDTO> getServiceProvidersByOrFilter(Integer pincode, String street, String locality) {
                logger.info("Fetching service providers with OR filter - Pincode: {}, Street: {}, Locality: {}",
                                pincode, street, locality);

                // Initialize specification for OR conditions
                Specification<ServiceProvider> spec = Specification.where(null);

                if (pincode != null) {
                        spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pincode"),
                                        pincode));
                        logger.debug("Adding OR condition for pincode: {}", pincode);
                }
                if (street != null) {
                        spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("street"),
                                        street));
                        logger.debug("Adding OR condition for street: {}", street);
                }
                if (locality != null) {
                        spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("locality"),
                                        locality));
                        logger.debug("Adding OR condition for locality: {}", locality);
                }

                // Fetch results using the built specification
                List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(spec);
                logger.debug("Number of service providers found: {}", serviceProviders.size());

                // Map entities to DTOs
                return serviceProviders.stream()
                                .map(serviceProviderMapper::serviceProviderToDTO)
                                .collect(Collectors.toList());
        }

        /*
         * @Override
         * 
         * @Transactional
         * public List<ServiceProviderDTO> getServiceProvidersByOrFilter(Integer
         * pincode, String street, String locality) {
         * logger.
         * info("Fetching service providers with OR filter - Pincode: {}, Street: {}, Locality: {}"
         * ,
         * pincode,
         * street, locality);
         * 
         * List<ServiceProvider> serviceProviders =
         * serviceProviderRepository.findByPincodeOrStreetOrLocality(
         * pincode,
         * street, locality);
         * 
         * logger.debug("Found {} service provider(s) matching the criteria.",
         * serviceProviders.size());
         * 
         * return serviceProviders.stream()
         * .map(serviceProviderMapper::serviceProviderToDTO)
         * .collect(Collectors.toList());
         * }
         */
}
