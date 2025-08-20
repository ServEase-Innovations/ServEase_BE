package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.dto.ServiceProviderLeaveDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.enums.BookingType;
import com.springboot.app.enums.HousekeepingRole;

import com.springboot.app.exception.ServiceProviderEngagementNotFoundException;
import com.springboot.app.mapper.CustomerHolidaysMapper;
import com.springboot.app.mapper.ServiceProviderEngagementMapper;
import com.springboot.app.mapper.ServiceProviderLeaveMapper;
import com.springboot.app.mapper.ServiceProviderMapper;
import com.springboot.app.repository.CustomerHolidaysRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.repository.ServiceProviderLeaveRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;

@Service
public class ServiceProviderEngagementServiceImpl implements ServiceProviderEngagementService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderEngagementServiceImpl.class);

    private final ServiceProviderEngagementRepository engagementRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final CustomerRepository customerRepository;
    private final ServiceProviderEngagementMapper engagementMapper;
    private final ServiceProviderLeaveRepository leaveRepository;
    private final ServiceProviderLeaveMapper leaveMapper;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    @Autowired
    private GeoHashService geoHashService;

    @Autowired
    private CustomerHolidaysRepository customerHolidaysRepository;

    @Autowired
    private CustomerHolidaysMapper customerHolidaysMapper;

    @Autowired
    public ServiceProviderEngagementServiceImpl(ServiceProviderEngagementRepository engagementRepository,
            ServiceProviderRepository serviceProviderRepository,
            CustomerRepository customerRepository,
            ServiceProviderEngagementMapper engagementMapper,
            ServiceProviderLeaveRepository leaveRepository,
            ServiceProviderLeaveMapper leaveMapper) {
        this.engagementRepository = engagementRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.customerRepository = customerRepository;
        this.engagementMapper = engagementMapper;
        this.leaveRepository = leaveRepository;
        this.leaveMapper = leaveMapper;
    }

    @Override
    @Transactional
    public List<ServiceProviderEngagementDTO> getAllServiceProviderEngagements(int page, int size) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching service provider engagements with page: {} and size: {}", page, size);
        }
        // Fetch paginated results using Spring Data JPA
        Pageable pageable = PageRequest.of(page, size);
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll(pageable).getContent();
        if (logger.isDebugEnabled()) {

            logger.debug("Number of service provider engagements fetched: {}", engagements.size());
        }
        // Check if no engagements are found and log a warning
        if (engagements.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("No service provider engagements found on the requested page.");
            }
            return new ArrayList<>(); // Return empty list if no engagements found
        }

        // Map entities to DTOs
        // Map entities to DTOs
        return engagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderEngagementDTO getServiceProviderEngagementById(Long id) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching service provider engagement by ID: {}", id);
        }
        return engagementRepository.findById(id)
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .orElseThrow(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("No service provider engagement found with ID: {}", id);
                    }
                    return new RuntimeException("Service Provider Engagement not found.");
                });
    }

    @Override
    @Transactional
    public String addServiceProviderEngagement(ServiceProviderEngagementDTO dto) {
        if (logger.isInfoEnabled()) {

            logger.info("Adding new service provider engagement");
        }
        ServiceProvider serviceProvider = null;

        if (dto.getBookingType() != BookingType.ON_DEMAND) {
            // For NON-ON_DEMAND bookings, serviceProviderId is required
            if (dto.getServiceProviderId() != null) {
                serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                        .orElseThrow(() -> {
                            if (logger.isErrorEnabled()) {
                                logger.error("ServiceProvider with ID {} not found.", dto.getServiceProviderId());
                            }
                            return new RuntimeException("Service Provider not found.");
                        });
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("Booking type is {} but ServiceProvider ID is missing.", dto.getBookingType());
                }
                throw new RuntimeException("Service Provider ID is required for booking type: " + dto.getBookingType());
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("ON_DEMAND booking: Skipping ServiceProvider lookup.");
            }
        }

        // Fetch the Customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Customer with ID {} not found.", dto.getCustomerId());
                    }
                    return new RuntimeException("Customer not found.");
                });

        // Map DTO to entity and set relationships
        ServiceProviderEngagement engagement = engagementMapper.dtoToServiceProviderEngagement(dto);

        // Set relationships if ServiceProvider exists
        if (serviceProvider != null) {
            engagement.setServiceProvider(serviceProvider);
        }
        engagement.setCustomer(customer);
        if (dto.getEndDate() != null && dto.getEndDate().isBefore(LocalDate.now())) {
            engagement.setTimeslot("00:00-00:00");
        }

        engagementRepository.save(engagement);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new service provider engagement with ID: {}", engagement.getId());
        }
        return "Service Provider Engagement added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderEngagement(ServiceProviderEngagementDTO dto) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating service provider engagement with ID: {}", dto.getId());
        }

        return engagementRepository.findById(dto.getId())
                .map(existingEngagement -> {
                    // Fetch related customer
                    Customer customer = customerRepository.findById(dto.getCustomerId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Customer not found with ID: " + dto.getCustomerId()));

                    // ✅ Start with current service provider from DB
                    ServiceProvider serviceProvider = existingEngagement.getServiceProvider();

                    // Booking type validation
                    if (dto.getBookingType() != null && dto.getBookingType() != BookingType.ON_DEMAND) {
                        if (dto.getServiceProviderId() == null) {
                            throw new RuntimeException(
                                    "Service Provider ID is required for booking type: " + dto.getBookingType());
                        }
                    }

                    // ✅ Update only if a new ID is provided
                    if (dto.getServiceProviderId() != null) {
                        serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Service Provider not found with ID: " + dto.getServiceProviderId()));
                    }

                    // Update other fields from DTO
                    engagementMapper.updateEntityFromDTO(dto, existingEngagement);

                    if (dto.getMonthlyAmount() != null) {
                        existingEngagement.setMonthlyAmount(dto.getMonthlyAmount());
                    }

                    existingEngagement.setCustomer(customer);
                    existingEngagement.setServiceProvider(serviceProvider); // ✅ preserves old value if no new ID

                    engagementRepository.save(existingEngagement);

                    if (logger.isDebugEnabled()) {
                        logger.info("Service provider engagement updated successfully with ID: {}", dto.getId());
                    }

                    return ServiceProviderConstants.ENGAGEMENT_UPDATED;
                })
                .orElse(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND);
    }

    @Override
    @Transactional
    public String deleteServiceProviderEngagement(Long id) {
        if (logger.isInfoEnabled()) {

            logger.info("Deactivating service provider engagement with ID: {}", id);
        }
        // Check if the service provider engagement exists
        if (engagementRepository.existsById(id)) {

            // Fetch the engagement from the repository
            ServiceProviderEngagement engagement = engagementRepository.findById(id)
                    .orElseThrow(() -> {
                        if (logger.isErrorEnabled()) {
                            logger.error("Service provider engagement not found with ID: {}", id);
                        }
                        return new RuntimeException("Service Provider Engagement not found.");
                    });

            // Mark engagement as completed (e.g., set `isActive` to false or similar)
            engagement.completeEngagement(); // Example method to deactivate engagement
            engagementRepository.save(engagement); // Save the updated engagement
            if (logger.isInfoEnabled()) {
                logger.info("Service provider engagement with ID {} deactivated", id);
            }
            return ServiceProviderConstants.ENGAGEMENT_DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Service provider engagement not found for deactivation with ID: {}", id);
            }
            return ServiceProviderConstants.ENGAGEMENT_NOT_FOUND;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByServiceProviderId(Long serviceProviderId) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching service provider engagements by ServiceProvider ID: {}", serviceProviderId);
        }
        // Fetch all engagements and filter by serviceProviderId
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll();
        List<ServiceProviderEngagement> filteredEngagements = engagements.stream()
                .filter(e -> e.getServiceProvider() != null &&
                        e.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .toList();

        // Check if no engagements found and throw a dedicated exception
        if (filteredEngagements.isEmpty()) {
            throw new ServiceProviderEngagementNotFoundException(
                    "No data found for ServiceProvider ID: " + serviceProviderId);
        }

        // Convert to DTO and return an unmodifiable list
        return filteredEngagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderEngagementDTO> getServiceProviderEngagementsByCustomerId(Long customerId) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching service provider engagements by Customer ID: {}", customerId);
        }
        List<ServiceProviderEngagement> filteredEngagements = engagementRepository.findAll().stream()
                .filter(e -> e.getCustomer() != null && e.getCustomer().getCustomerId().equals(customerId))
                .toList();

        if (filteredEngagements.isEmpty()) {
            throw new ServiceProviderEngagementNotFoundException("No data found for Customer ID: " + customerId);
        }

        return filteredEngagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .toList();
    }

    // @Override
    // @Transactional(readOnly = true)
    // public Map<String, List<ServiceProviderEngagementDTO>>
    // getServiceProviderBookingHistoryByCustomerId(
    // Long customerId) {
    // logger.info("Fetching booking history for customerId: {}", customerId);

    // List<ServiceProviderEngagement> engagements =
    // engagementRepository.findByCustomer_CustomerId(customerId);

    // if (engagements.isEmpty()) {
    // return Collections.emptyMap();
    // }

    // LocalDate currentDate = LocalDate.now();

    // return engagements.stream()
    // .map(engagementMapper::serviceProviderEngagementToDTO)
    // .collect(Collectors.groupingBy(engagement -> {
    // LocalDate startDate = engagement.getStartDate();
    // LocalDate endDate = engagement.getEndDate();

    // if (endDate == null) {
    // return (startDate != null && !startDate.isAfter(currentDate)) ? "current" :
    // "future";
    // } else {
    // if (endDate.isBefore(currentDate))
    // return "past";
    // if (startDate != null && startDate.isAfter(currentDate))
    // return "future";
    // return "current";
    // }
    // }));
    // }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ServiceProviderEngagementDTO>> getServiceProviderBookingHistoryByCustomerId(
            Long customerId) {
        logger.info("Fetching booking history for customerId: {}", customerId);

        // Fetch engagements
        List<ServiceProviderEngagement> engagements = engagementRepository.findByCustomer_CustomerId(customerId);

        if (engagements.isEmpty()) {
            return Collections.emptyMap();
        }

        // Fetch holidays for the same customer
        List<CustomerHolidaysDTO> holidays = customerHolidaysRepository
                .findByCustomer_CustomerId(customerId)
                .stream()
                .map(customerHolidaysMapper::customerHolidaysToDTO)
                .toList();

        LocalDate currentDate = LocalDate.now();

        // Convert engagements to DTO and attach holiday info
        List<ServiceProviderEngagementDTO> engagementDTOs = engagements.stream()
                .map(engagement -> {
                    ServiceProviderEngagementDTO dto = engagementMapper.serviceProviderEngagementToDTO(engagement);

                    // Attach holidays that overlap with this engagement period
                    List<CustomerHolidaysDTO> matchingHolidays = holidays.stream()
                            .filter(h -> {
                                LocalDate start = h.getStartDate();
                                LocalDate end = h.getEndDate() != null ? h.getEndDate() : start;
                                return (dto.getStartDate() != null && !start.isAfter(dto.getEndDate())) &&
                                        (dto.getEndDate() == null || !end.isBefore(dto.getStartDate()));
                            })
                            .toList();
                    dto.setCustomerHolidays(matchingHolidays); // Assuming you add this field to DTO
                    return dto;
                }).toList();

        // Group by past/current/future
        return engagementDTOs.stream().collect(Collectors.groupingBy(engagement ->

        {
            LocalDate startDate = engagement.getStartDate();
            LocalDate endDate = engagement.getEndDate();

            if (endDate == null) {
                return (startDate != null && !startDate.isAfter(currentDate)) ? "current" : "future";
            } else {
                if (endDate.isBefore(currentDate))
                    return "past";
                if (startDate != null && startDate.isAfter(currentDate))
                    return "future";
                return "current";
            }
        }));
    }

    // @Override
    // @Transactional(readOnly = true)
    // public Map<String, List<ServiceProviderEngagementDTO>>
    // getServiceProviderBookingHistoryByServiceProviderId(
    // Long serviceProviderId) {
    // logger.info("Fetching booking history for serviceProviderId: {}",
    // serviceProviderId);

    // // ✅ Fetch engagements by service provider
    // List<ServiceProviderEngagement> engagements = engagementRepository
    // .findByServiceProvider_ServiceproviderId(serviceProviderId);

    // if (engagements.isEmpty()) {
    // return Collections.emptyMap();
    // }

    // // ✅ Fetch all leaves of this service provider
    // List<ServiceProviderLeaveDTO> leaves = leaveRepository
    // .findByServiceProvider_ServiceproviderId(serviceProviderId)
    // .stream()
    // .map(leaveMapper::serviceProviderLeaveToDTO)
    // .toList();

    // LocalDate currentDate = LocalDate.now();

    // // ✅ Convert engagements to DTO and attach overlapping leaves
    // List<ServiceProviderEngagementDTO> engagementDTOs = engagements.stream()
    // .map(engagement -> {
    // ServiceProviderEngagementDTO dto =
    // engagementMapper.serviceProviderEngagementToDTO(engagement);

    // // Find leaves that overlap with engagement period
    // List<ServiceProviderLeaveDTO> matchingLeaves = leaves.stream()
    // .filter(l -> {
    // LocalDate leaveStart = l.getFromDate();
    // LocalDate leaveEnd = l.getToDate() != null ? l.getToDate() : leaveStart;
    // return (dto.getStartDate() != null && !leaveStart.isAfter(dto.getEndDate()))
    // &&
    // (dto.getEndDate() == null || !leaveEnd.isBefore(dto.getStartDate()));
    // })
    // .toList();

    // // Attach leaves (make sure your DTO has this field)
    // dto.setServiceProviderLeaves(matchingLeaves);

    // return dto;
    // }).toList();

    // // ✅ Group by past/current/future
    // return engagementDTOs.stream().collect(Collectors.groupingBy(engagement ->

    // {
    // LocalDate startDate = engagement.getStartDate();
    // LocalDate endDate = engagement.getEndDate();

    // if (endDate == null) {
    // return (startDate != null && !startDate.isAfter(currentDate)) ? "current" :
    // "future";
    // } else {
    // if (endDate.isBefore(currentDate))
    // return "past";
    // if (startDate != null && startDate.isAfter(currentDate))
    // return "future";
    // return "current";
    // }
    // }));
    // }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ServiceProviderEngagementDTO>> getServiceProviderBookingHistoryByServiceProviderId(
            Long serviceProviderId) {
        logger.info("Fetching booking history for serviceProviderId: {}", serviceProviderId);

        List<ServiceProviderEngagement> engagements = engagementRepository
                .findByServiceProvider_ServiceproviderId(serviceProviderId);

        if (engagements.isEmpty()) {
            return Collections.emptyMap();
        }

        // ✅ Fetch all service provider leaves
        List<ServiceProviderLeaveDTO> leaves = leaveRepository
                .findByServiceProvider_ServiceproviderId(serviceProviderId)
                .stream()
                .map(leaveMapper::serviceProviderLeaveToDTO)
                .toList();

        LocalDate currentDate = LocalDate.now();

        // ✅ Convert engagements to DTOs and attach BOTH leaves + customer holidays
        List<ServiceProviderEngagementDTO> engagementDTOs = engagements.stream()
                .map(engagement -> {
                    ServiceProviderEngagementDTO dto = engagementMapper.serviceProviderEngagementToDTO(engagement);

                    // ---- Attach service provider leaves ----
                    List<ServiceProviderLeaveDTO> matchingLeaves = leaves.stream()
                            .filter(l -> {
                                LocalDate leaveStart = l.getFromDate();
                                LocalDate leaveEnd = l.getToDate() != null ? l.getToDate() : leaveStart;
                                return (dto.getStartDate() != null && !leaveStart.isAfter(dto.getEndDate())) &&
                                        (dto.getEndDate() == null || !leaveEnd.isBefore(dto.getStartDate()));
                            })
                            .toList();
                    dto.setServiceProviderLeaves(matchingLeaves);

                    // ---- Attach customer holidays ----
                    List<CustomerHolidaysDTO> customerHolidays = customerHolidaysRepository
                            .findByCustomer_CustomerId(dto.getCustomerId())
                            .stream()
                            .map(customerHolidaysMapper::customerHolidaysToDTO)
                            .filter(h -> {
                                LocalDate start = h.getStartDate();
                                LocalDate end = h.getEndDate() != null ? h.getEndDate() : start;
                                return (dto.getStartDate() != null && !start.isAfter(dto.getEndDate())) &&
                                        (dto.getEndDate() == null || !end.isBefore(dto.getStartDate()));
                            })
                            .toList();
                    dto.setCustomerHolidays(customerHolidays);

                    return dto;
                })
                .toList();

        // ✅ Group by past/current/future
        return engagementDTOs.stream()
                .collect(Collectors.groupingBy(engagement -> {
                    LocalDate startDate = engagement.getStartDate();
                    LocalDate endDate = engagement.getEndDate();

                    if (endDate == null) {
                        return (startDate != null && !startDate.isAfter(currentDate)) ? "current" : "future";
                    } else {
                        if (endDate.isBefore(currentDate))
                            return "past";
                        if (startDate != null && startDate.isAfter(currentDate))
                            return "future";
                        return "current";
                    }
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ServiceProviderEngagementDTO>> getServiceProviderBookingHistory(int page, int size) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching and categorizing service provider engagements with pagination - page: {}, size: {}",
                    page,
                    size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<ServiceProviderEngagement> engagements = engagementRepository.findAll(pageable).getContent();

        if (engagements.isEmpty()) {
            return Collections.emptyMap();
        }

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Categorize engagements and return directly
        return engagements.stream()
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .collect(Collectors.groupingBy(engagement -> {
                    LocalDate startDate = engagement.getStartDate();
                    LocalDate endDate = engagement.getEndDate();

                    if (endDate == null) {
                        if (startDate != null && (startDate.isBefore(currentDate) ||
                                startDate.isEqual(currentDate))) {
                            return "current";
                        } else {
                            return "future";
                        }
                    } else {
                        if (endDate.isBefore(currentDate)) {
                            return "past";
                        } else if (startDate != null && startDate.isAfter(currentDate)) {
                            return "future";
                        } else {
                            return "current";
                        }
                    }
                }));
    }

    // search api method
    @Override
    @Transactional(readOnly = true)
    public List<Object> getProgressiveSearch(
            double latitude,
            double longitude,
            HousekeepingRole housekeepingRole,
            int precision) {
        logger.info(
                "Performing progressive search for housekeeping role: {}, latitude: {}, longitude: {}, precision: {}",
                housekeepingRole, latitude, longitude, precision);

        List<String> geoHashes = geoHashService.getNearbyGeoHashes(latitude, longitude, precision);
        List<ServiceProvider> providers = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, geoHashes);
        if (providers.isEmpty()) {
            logger.warn("No service providers found for the given parameters.");
            return Collections.emptyList();
        }
        return new ArrayList<>(providers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProvider> getPriorityBasedSearch(
            double latitude,
            double longitude,
            HousekeepingRole housekeepingRole) {

        logger.info("Starting priority-based geo search for role: {}, lat: {}, long: {}",
                housekeepingRole, latitude, longitude);

        // Generate geohashes by precision
        List<String> geoHashes7 = geoHashService.getNearbyGeoHashes(latitude, longitude, 7);
        List<String> geoHashes6 = geoHashService.getNearbyGeoHashes(latitude, longitude, 6);
        List<String> geoHashes5 = geoHashService.getNearbyGeoHashes(latitude, longitude, 5);
        List<String> geoHashes4 = geoHashService.getNearbyGeoHashes(latitude, longitude, 4);

        List<ServiceProvider> result = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        // Precision 7
        List<ServiceProvider> from7 = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, geoHashes7);
        for (ServiceProvider sp : from7) {
            if (seenIds.add(sp.getServiceproviderId())) {
                result.add(sp);
            }
        }

        // Precision 6
        List<ServiceProvider> from6 = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, geoHashes6);
        for (ServiceProvider sp : from6) {
            if (seenIds.add(sp.getServiceproviderId())) {
                result.add(sp);
            }
        }

        // Precision 5
        List<ServiceProvider> from5 = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, geoHashes5);
        for (ServiceProvider sp : from5) {
            if (seenIds.add(sp.getServiceproviderId())) {
                result.add(sp);
            }
        }

        // Precision 4
        List<ServiceProvider> from4 = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, geoHashes4);
        for (ServiceProvider sp : from4) {
            if (seenIds.add(sp.getServiceproviderId())) {
                result.add(sp);
            }
        }

        logger.info("Priority-based search found total {} unique providers", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getEngagementsByExactDateTimeslotAndHousekeepingRole(
            LocalDate startDate, LocalDate endDate, String timeslot, HousekeepingRole housekeepingRole,
            double latitude, double longitude, int precision) {
        if (logger.isInfoEnabled()) {

            logger.info("Fetching engagements for startDate: {}, endDate: {}, housekeepingRole: {}",
                    startDate, endDate, housekeepingRole);
        }
        // Fetch all engagements
        List<ServiceProviderEngagement> engagements = engagementRepository
                .findByDateAndHousekeepingRole(startDate, endDate, housekeepingRole);

        List<String> nearbyGeoHashes = geoHashService.getNearbyGeoHashes(latitude, longitude, precision);
        Set<Long> engagedProviderIds = new HashSet<>();
        Set<Long> excludedProviderIds = new HashSet<>();

        // find engaged service providers
        List<Object> engagementDetails = engagements.stream()
                .filter(e -> e.getServiceProvider() != null)
                .filter(e -> {
                    ServiceProvider provider = e.getServiceProvider();
                    boolean isNearby = (precision == 5 && nearbyGeoHashes.contains(provider.getGeoHash5())) ||
                            (precision == 6 && nearbyGeoHashes.contains(provider.getGeoHash6())) ||
                            (precision == 7 && nearbyGeoHashes.contains(provider.getGeoHash7()));

                    // Exclude engagements that match or overlap the requested timeslot
                    boolean isExcluded = timeslot != null && isTimeslotExcluded(e.getTimeslot(), timeslot);

                    if (isExcluded) {
                        excludedProviderIds.add(provider.getServiceproviderId());
                    }

                    if (isNearby && !isExcluded) {
                        engagedProviderIds.add(provider.getServiceproviderId());
                    }

                    return isNearby && !isExcluded;
                })
                .map(engagementMapper::serviceProviderEngagementToDTO)
                .collect(Collectors.toList());

        // Fetch nearby providers
        List<ServiceProvider> nearbyProviders = serviceProviderRepository
                .findByHousekeepingRoleAndGeoHash(housekeepingRole, nearbyGeoHashes);

        List<ServiceProvider> unengagedProviders = nearbyProviders.stream()
                .filter(sp -> !engagedProviderIds.contains(sp.getServiceproviderId()))
                .filter(sp -> !excludedProviderIds.contains(sp.getServiceproviderId()))
                .filter(sp -> isProviderFreeInTimeslot(sp, startDate, endDate, timeslot))
                .collect(Collectors.toList());

        List<Object> result = new ArrayList<>();
        result.addAll(engagementDetails);
        result.addAll(unengagedProviders);

        return result;
    }

    private boolean isProviderFreeInTimeslot(ServiceProvider provider, LocalDate startDate,
            LocalDate endDate, String requestedTimeslot) {
        List<ServiceProviderEngagement> providerEngagements = engagementRepository
                .findByServiceProviderAndDateRange(provider.getServiceproviderId(), startDate, endDate);

        for (ServiceProviderEngagement engagement : providerEngagements) {
            if (isTimeslotExcluded(engagement.getTimeslot(), requestedTimeslot)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTimeslotExcluded(String engagementTimeslot, String requestedTimeslot) {
        List<LocalTime[]> engagementRanges = parseTimeslot(engagementTimeslot);
        List<LocalTime[]> requestedRanges = parseTimeslot(requestedTimeslot);

        // Check for any overlap between engagement and requested ranges
        for (LocalTime[] engagementRange : engagementRanges) {
            for (LocalTime[] requestedRange : requestedRanges) {
                if (engagementRange[0].isBefore(requestedRange[1]) && engagementRange[1].isAfter(requestedRange[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<LocalTime[]> parseTimeslot(String timeslot) {
        List<LocalTime[]> ranges = new ArrayList<>();
        String[] slots = timeslot.split(",");

        for (String slot : slots) {
            String[] parts = slot.trim().split("-");
            if (parts.length == 2) {
                ranges.add(new LocalTime[] {
                        LocalTime.parse(parts[0], DateTimeFormatter.ofPattern("H:mm")),
                        LocalTime.parse(parts[1], DateTimeFormatter.ofPattern("H:mm"))
                });
            }
        }
        return ranges;
    }

    // @Override
    // @Transactional(readOnly = true)
    // public List<ServiceProviderEngagementDTO>
    // getEngagementsByExactDateTimeslotAndHousekeepingRole(
    // LocalDate startDate, LocalDate endDate, String timeslot, HousekeepingRole
    // housekeepingRole) {

    // logger.info("Fetching engagements for startDate: {}, endDate: {},
    // timeslot:{}, housekeepingRole: {}",
    // startDate, endDate, timeslot, housekeepingRole);

    // List<ServiceProviderEngagement> engagements = engagementRepository
    // .findByExactDateTimeslotAndHousekeepingRole(startDate, endDate, timeslot,
    // housekeepingRole);

    // if (engagements.isEmpty()) {
    // logger.warn("No engagements found for the given filters.");
    // return Collections.emptyList();
    // }

    // return engagements.stream()
    // .map(engagementMapper::serviceProviderEngagementToDTO)
    // .toList();
    // }

}
