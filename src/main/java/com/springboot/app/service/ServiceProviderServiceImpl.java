package com.springboot.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
import com.springboot.app.enums.UserRole;
import com.springboot.app.mapper.ServiceProviderMapper;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import com.springboot.app.util.ExcelSheetHandler;
import org.springframework.data.domain.Pageable;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

        private static final Logger logger = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

        @Autowired
        private ServiceProviderRepository serviceProviderRepository;

        @Autowired
        private ServiceProviderMapper serviceProviderMapper;

        @Autowired
        private UserCredentialsService userCredentialsService;

        @Autowired
        private ServiceProviderEngagementRepository engagementRepository;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        private ExcelSheetHandler excelSheetHandler;

        @Override
        @Transactional
        public List<ServiceProviderDTO> getAllServiceProviderDTOs(int page, int size) {
                logger.info("Fetching service providers with page: {} and size: {}", page, size);

                // Fetch paginated results using Spring Data JPA
                Pageable pageable = PageRequest.of(page, size);
                List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(pageable).getContent();

                logger.debug("Number of service providers fetched: {}", serviceProviders.size());

                if (serviceProviders.isEmpty()) {
                        logger.warn("No service providers found on the requested page.");
                        return new ArrayList<>(); // Return empty list if no service providers found
                }

                // Map entities to DTOs
                return serviceProviders.stream()
                                .map(serviceProvider -> serviceProviderMapper.serviceProviderToDTO(serviceProvider))
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

                // Check if service provider already exists by email or mobile number
                if (serviceProviderRepository.existsByEmailId(serviceProviderDTO.getEmailId())) {
                        throw new IllegalArgumentException("Service provider with this email already exists.");
                }
                if (serviceProviderRepository.existsByMobileNo(serviceProviderDTO.getMobileNo())) {
                        throw new IllegalArgumentException("Service provider with this mobile number already exists.");
                }
                serviceProviderDTO.setUsername(email);

                // Step 1: Calculate age from DOB and set it
                LocalDate dob = serviceProviderDTO.getDOB();
                if (dob != null) {
                        int calculatedAge = calculateAge(dob);
                        // Add the age validation check
                        if (calculatedAge < 18) {
                                throw new IllegalArgumentException("You must be at least 18 years old to proceed.");
                        }
                        serviceProviderDTO.setAge(calculatedAge);
                        logger.info("Calculated age: {}", calculatedAge);
                } else {
                        throw new IllegalArgumentException("Date of Birth (DOB) is required to calculate age.");
                }

                // Step 4: Register user credentials
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

                // Step 5: Save the service provider
                ServiceProvider serviceProvider = serviceProviderMapper.dtoToServiceProvider(serviceProviderDTO);
                serviceProvider.setActive(true);
                serviceProviderRepository.save(serviceProvider);
                logger.debug("Service provider saved successfully: {}", serviceProvider);
        }

        private int calculateAge(LocalDate dob) {
                return LocalDate.now().getYear() - dob.getYear();
        }

        @Override
        @Transactional
        public String updateServiceProviderDTO(ServiceProviderDTO serviceProviderDTO) {
                logger.info("Updating service provider with ID: {}", serviceProviderDTO.getServiceproviderId());

                // Check if the service provider exists
                if (serviceProviderRepository.existsById(serviceProviderDTO.getServiceproviderId())) {

                        // Map DTO to entity and update
                        ServiceProvider existingServiceProvider = serviceProviderMapper
                                        .dtoToServiceProvider(serviceProviderDTO);

                        // Save the updated service provider
                        serviceProviderRepository.save(existingServiceProvider);

                        logger.info("Service provider updated with ID: {}", serviceProviderDTO.getServiceproviderId());
                        return ServiceProviderConstants.UPDATE_DESC;
                } else {
                        logger.error("Service provider not found for update with ID: {}",
                                        serviceProviderDTO.getServiceproviderId());
                        return ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND;
                }
        }

        @Override
        @Transactional
        public String deleteServiceProviderDTO(Long id) {
                logger.info("Deactivating service provider with ID: {}", id);

                return serviceProviderRepository.findById(id)
                                .map(serviceProvider -> {
                                        // Deactivate the service provider
                                        serviceProvider.deactivate(); // Assuming deactivate() s
                                        serviceProviderRepository.save(serviceProvider);
                                        logger.info("Service provider with ID {} deactivated", id);
                                        return ServiceProviderConstants.DELETE_DESC;
                                })
                                .orElseGet(() -> {
                                        logger.error("Service provider not found for deletion with ID: {}", id);
                                        return ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND;
                                });
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

        @Override
        public Map<String, Object> calculateExpectedSalary(Long serviceProviderId) {
                logger.info("Calculating expected salary for service provider ID: {}", serviceProviderId);

                Map<String, Object> response = new HashMap<>();
                try {
                        // Fetch all engagements and filter for the given service provider ID
                        List<ServiceProviderEngagement> engagements = engagementRepository.findAll();
                        List<ServiceProviderEngagement> filteredEngagements = new ArrayList<>();

                        for (ServiceProviderEngagement engagement : engagements) {
                                if (engagement.getServiceProvider().getServiceproviderId().equals(serviceProviderId)) {
                                        filteredEngagements.add(engagement);
                                }
                        }

                        if (filteredEngagements.isEmpty()) {
                                logger.warn("No engagements found for service provider ID: {}", serviceProviderId);
                                response.put("error", "No engagements found");
                                return response;
                        }

                        double totalSalary = 0.0;

                        for (ServiceProviderEngagement engagement : filteredEngagements) {
                                LocalDate startDate = engagement.getStartDate().toLocalDate();
                                LocalDate endDate = (engagement.getEndDate() != null)
                                                ? engagement.getEndDate().toLocalDate()
                                                : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

                                long noOfDays = (ChronoUnit.DAYS.between(startDate, endDate)) + 1;
                                int monthlyAmount = (int) engagement.getMonthlyAmount();
                                int daysInMonth = endDate.lengthOfMonth();
                                double calculatedAmount = (double) monthlyAmount / daysInMonth * noOfDays;

                                // Round the calculated amount to 2 decimal places
                                BigDecimal roundedAmount = new BigDecimal(calculatedAmount).setScale(2,
                                                RoundingMode.HALF_UP);
                                totalSalary += roundedAmount.doubleValue();
                        }

                        // Add service provider ID and expected salary to the response
                        response.put("serviceProviderId", serviceProviderId);
                        response.put("expectedSalary", totalSalary);

                        logger.info("Calculated expected salary for service provider ID: {} is {}", serviceProviderId,
                                        totalSalary);

                        return response;

                } catch (Exception e) {
                        logger.error("Error calculating expected salary for service provider ID: {}", serviceProviderId,
                                        e);
                        response.put("error", "Error calculating salary");
                        return response;
                }

        }

        @Override
        @Transactional
        public List<ServiceProviderDTO> getServiceProvidersByRole(HousekeepingRole role) {
                logger.info("Fetching service providers with role: {}", role);

                // Check if the role is provided
                if (role == null) {
                        logger.warn("Role cannot be null");
                        throw new IllegalArgumentException("Role must be provided to fetch service providers.");
                }

                // Filter service providers by the specified housekeeping role
                Specification<ServiceProvider> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get("housekeepingRole"), role);

                List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(spec);
                logger.debug("Number of service providers found for role {}: {}", role, serviceProviders.size());

                // Map entities to DTOs
                return serviceProviders.stream()
                                .map(serviceProviderMapper::serviceProviderToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public String uploadExcelRecords(String filename) {
                // Read the Excel file and retrieve the list of ServiceProviders
                List<ServiceProvider> serviceProviders = excelSheetHandler.readExcelFile(filename);

                if (serviceProviders != null && !serviceProviders.isEmpty()) {
                        // Save each ServiceProvider to the database
                        serviceProviderRepository.saveAll(serviceProviders);
                }
                return "successfull";
        }

}
