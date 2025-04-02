package com.springboot.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.app.constant.ServiceProviderConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import com.springboot.app.dto.AttendanceDTO;
import com.springboot.app.dto.LeaveBalanceDTO;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.dto.ServiceProviderLeaveDTO;

import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.dto.ShortListedServiceProviderDTO;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Habit;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;
import com.springboot.app.enums.UserRole;
import com.springboot.app.service.ServiceProviderRequestService;
import com.springboot.app.service.AttendanceService;
import com.springboot.app.service.LeaveBalanceService;
import com.springboot.app.service.ServiceProviderEngagementService;
import com.springboot.app.service.ServiceProviderFeedbackService;
import com.springboot.app.service.ServiceProviderLeaveService;

import com.springboot.app.service.ServiceProviderRequestCommentService;
import com.springboot.app.service.ServiceProviderService;
import com.springboot.app.service.ShortListedServiceProviderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping(ServiceProviderConstants.BASE_API_PATH)
@Api(value = ServiceProviderConstants.API_VALUE, tags = ServiceProviderConstants.TAG_SERVICEPROVIDERS)
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    private final ServiceProviderRequestService serviceProviderRequestService;
    private final ServiceProviderFeedbackService serviceProviderFeedbackService;
    private final ServiceProviderRequestCommentService serviceProviderRequestCommentService;
    private final ServiceProviderEngagementService serviceProviderEngagementService;
    private final ShortListedServiceProviderService shortListedServiceProviderService;
    private final AttendanceService attendanceService;
    private final ServiceProviderLeaveService serviceProviderLeaveService;
    private final LeaveBalanceService leaveBalanceService;

    @Autowired
    public ServiceProviderController(
            ServiceProviderService serviceProviderService,
            ServiceProviderRequestService serviceProviderRequestService,
            ServiceProviderFeedbackService serviceProviderFeedbackService,
            ServiceProviderRequestCommentService serviceProviderRequestCommentService,
            ServiceProviderEngagementService serviceProviderEngagementService,
            ShortListedServiceProviderService shortListedServiceProviderService,
            AttendanceService attendanceService,
            ServiceProviderLeaveService serviceProviderLeaveService,
            LeaveBalanceService leaveBalanceService) {
        this.serviceProviderService = serviceProviderService;
        this.serviceProviderRequestService = serviceProviderRequestService;
        this.serviceProviderFeedbackService = serviceProviderFeedbackService;
        this.serviceProviderRequestCommentService = serviceProviderRequestCommentService;
        this.serviceProviderEngagementService = serviceProviderEngagementService;
        this.shortListedServiceProviderService = shortListedServiceProviderService;
        this.attendanceService = attendanceService;
        this.serviceProviderLeaveService = serviceProviderLeaveService;
        this.leaveBalanceService = leaveBalanceService;
    }

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    // --------API's FOR SERVICE PROVIDER ENTITY--------------------

    //get nearby service providers
    @GetMapping("/nearby")
    public ResponseEntity<List<ServiceProviderDTO>> getNearbyProviders(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false, defaultValue = "5") int precision) {

        List<ServiceProviderDTO> providers = serviceProviderService.findNearbyProviders(latitude, longitude, precision);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/serviceproviders/all")
    @ApiOperation(value = ServiceProviderConstants.RETRIEVE_ALL_DESC, response = List.class)
    public ResponseEntity<List<ServiceProviderDTO>> getAllServiceProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String location) {

        // Validate page and size
        if (page < 0) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        if (size == null || size <= 0) {
            size = defaultPageSize; // Default page size if not provided or invalid
        }

        // Get service providers, filtered by location if provided
        List<ServiceProviderDTO> serviceProviders = serviceProviderService.getAllServiceProviderDTOs(page, size,
                location);

        // Ensure occupiedTimeSlots are calculated before returning the response
        serviceProviders.forEach(serviceProviderDTO -> {
            if (serviceProviderDTO.getTimeslot() != null &&
                    !serviceProviderDTO.getTimeslot().isEmpty()) {
                serviceProviderDTO.setOccupiedTimeSlots(calculateOccupiedTimes(serviceProviderDTO.getTimeslot()));
            } else {
                serviceProviderDTO.setOccupiedTimeSlots(Collections.emptyList());
            }
        });

        // Return response with service providers (empty list if no results found)
        return serviceProviders.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList())
                : ResponseEntity.ok(serviceProviders);
    }

    /**
     * Method to calculate occupied time slots based on the given busy timeslots.
     */
    private List<String> calculateOccupiedTimes(String timeslot) {
        List<String> occupiedTimes = new ArrayList<>();

        try {
            // Validate input
            if (timeslot == null || timeslot.isEmpty()) {
                return occupiedTimes;
            }

            // Parse multiple busy timeslots (comma-separated)
            String[] timeRanges = timeslot.split(",");
            for (String range : timeRanges) {
                String[] hours = range.trim().split("-");
                if (hours.length != 2) {
                    continue; // Skip invalid format
                }

                int startHour = parseHour(hours[0]);
                int endHour = parseHour(hours[1]);

                // Add busy hours to the list
                for (int i = startHour; i < endHour; i++) {
                    occupiedTimes.add(String.format("%02d:00", i));
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timeslot input. Ensure it's in 'HH:mm-HH:mm' format.", e);
        }
        return occupiedTimes;
    }

    /**
     * Method to calculate available time slots based on the busy timeslot.
     */
    private List<String> calculateAvailableTimes(String timeslot) {
        List<String> availableTimes = new ArrayList<>();

        try {
            // Validate input
            if (timeslot == null || timeslot.isEmpty()) {
                return availableTimes;
            }

            // Create an array to track busy hours (0-23)
            boolean[] busyHours = new boolean[24];

            // Parse multiple busy timeslots (comma-separated)
            String[] timeRanges = timeslot.split(",");
            for (String range : timeRanges) {
                String[] hours = range.trim().split("-");
                if (hours.length != 2) {
                    continue; // Skip invalid format
                }

                int startHour = parseHour(hours[0]);
                int endHour = parseHour(hours[1]);

                // Mark busy hours
                for (int i = startHour; i < endHour; i++) {
                    if (i >= 0 && i < 24) {
                        busyHours[i] = true;
                    }
                }
            }

            // Identify available hours
            for (int i = 0; i < 24; i++) {
                if (!busyHours[i]) {
                    availableTimes.add(String.format("%02d:00", i));
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timeslot input. Ensure it's in 'HH:mm-HH:mm' format.", e);
        }
        return availableTimes;
    }

    /**
     * Method to extract the hour from "HH:mm" format.
     */
    private int parseHour(String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid time format. Expected 'HH:mm'.");
            }
            return Integer.parseInt(parts[0]); // Return the hour as an integer
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Time contains non-numeric values.", e);
        }
    }

    // API to get service provider by id
    @GetMapping("/get/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.GET_BY_ID_DESC, response = ServiceProviderDTO.class)
    public ResponseEntity<ServiceProviderDTO> getServiceProviderById(
            @ApiParam(value = "ID of the service provider to retrieve", required = true) @PathVariable Long id) {

        ServiceProviderDTO serviceProviderDTO = serviceProviderService.getServiceProviderDTOById(id);

        if (serviceProviderDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Service provider not found
        }

        // Calculate occupied times before returning response
        List<String> occupiedTimes = calculateOccupiedTimes(serviceProviderDTO.getTimeslot());
        serviceProviderDTO.setOccupiedTimeSlots(occupiedTimes);

        return ResponseEntity.ok(serviceProviderDTO);
    }

    // API to get service providers by vendor ID
    @GetMapping("/get/serviceproviders/vendor/{vendorId}")
    @ApiOperation(value = ServiceProviderConstants.GET_BY_VENDOR_ID_DESC, response = ServiceProviderDTO.class, responseContainer = "List")
    public ResponseEntity<List<ServiceProviderDTO>> getServiceProvidersByVendorId(
            @ApiParam(value = "Vendor ID to retrieve service providers for", required = true) @PathVariable Long vendorId) {
        List<ServiceProviderDTO> serviceProviderDTOs = serviceProviderService.getServiceProvidersByVendorId(vendorId);

        if (serviceProviderDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // No service providers found
        }

        return ResponseEntity.ok(serviceProviderDTOs);
    }

    // API to add a serviceprovider
    @PostMapping("/serviceprovider/add")
    @ApiOperation(value = ServiceProviderConstants.ADD_NEW_DESC)
    public ResponseEntity<String> addServiceProvider(
            @ApiParam(value = "Service provider DTO", required = true) @RequestBody ServiceProviderDTO serviceProviderDTO) {
        serviceProviderService.saveServiceProviderDTO(serviceProviderDTO);
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_ADDED);
    }

    // API to update a service provider
    @PutMapping("/update/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.UPDATE_DESC)
    public ResponseEntity<String> updateServiceProvider(
            @ApiParam(value = "ID of the service provider to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider object", required = true) @RequestBody ServiceProviderDTO serviceProviderDTO) {
        serviceProviderDTO.setServiceproviderId(id);
        String updateResponse = serviceProviderService.updateServiceProviderDTO(serviceProviderDTO);
        if (updateResponse.equals(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_UPDATED);
    }

    // API to delete a service provider (soft-delete)
    @PatchMapping("delete/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.DELETE_DESC)
    public ResponseEntity<String> deleteServiceProvider(
            @ApiParam(value = "ID of the service provider to deactivate", required = true) @PathVariable Long id) {
        String deleteResponse = serviceProviderService.deleteServiceProviderDTO(id);
        if (deleteResponse.equals(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.SERVICE_PROVIDER_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_DELETED);
    }

    // API to get service providers with flexible filtering
    @GetMapping("/get-by-filters")
    @ApiOperation(value = "Get service providers by filters", response = ServiceProviderDTO.class, responseContainer = "List")
    public ResponseEntity<List<ServiceProviderDTO>> getFilters(
            @ApiParam(value = "Language known by the service provider") @RequestParam(required = false) LanguageKnown language,
            @ApiParam(value = "Rating of the service provider") @RequestParam(required = false) Double rating,
            @ApiParam(value = "Gender of the service provider") @RequestParam(required = false) Gender gender,
            @ApiParam(value = "Speciality of the service provider") @RequestParam(required = false) Speciality speciality,
            @ApiParam(value = "Housekeeping role of the service provider") @RequestParam(required = false) HousekeepingRole housekeepingRole,
            @ApiParam(value = "Minimum age of the service provider") @RequestParam(required = false) Integer minAge,
            @ApiParam(value = "Maximum age of the service provider") @RequestParam(required = false) Integer maxAge,
            @ApiParam(value = "Timeslot in the format HH:mm-HH:mm") @RequestParam(required = false) String timeslot,
            @ApiParam(value = "Dietary habit of the service provider") @RequestParam(required = false) Habit diet) {

        // Call the service method with the provided filters
        List<ServiceProviderDTO> serviceProviders = serviceProviderService.getfilters(language, rating, gender,
                speciality, housekeepingRole, minAge, maxAge, timeslot, diet);
        return ResponseEntity.ok(serviceProviders);
    }

    // Endpoint to filter by only one parameter at a time
    @GetMapping("/filter")
    public ResponseEntity<List<ServiceProviderDTO>> getServiceProvidersBySingleFilter(
            @RequestParam(required = false) Integer pincode,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String locality) {

        int paramCount = 0;
        if (pincode != null)
            paramCount++;
        if (street != null)
            paramCount++;
        if (locality != null)
            paramCount++;

        if (paramCount != 1) {
            return ResponseEntity.badRequest()
                    .body(Collections.emptyList()); // Return an empty list instead of a wildcard response
        }

        List<ServiceProviderDTO> results;
        if (pincode != null) {
            results = serviceProviderService.getServiceProvidersByFilter(pincode, null, null);
        } else if (street != null) {
            results = serviceProviderService.getServiceProvidersByFilter(null, street, null);
        } else {
            results = serviceProviderService.getServiceProvidersByFilter(null, null, locality);
        }

        return ResponseEntity.ok(results);
    }

    // New endpoint for OR filtering
    @GetMapping("/orfilter")
    public List<ServiceProviderDTO> getServiceProvidersByOrFilter(
            @RequestParam(required = false) Integer pincode,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String locality) {
        return serviceProviderService.getServiceProvidersByOrFilter(pincode, street, locality);
    }

    @GetMapping("/{serviceProviderId}/expected-salary")
    public ResponseEntity<Map<String, Object>> getExpectedSalary(@PathVariable Long serviceProviderId) {

        Map<String, Object> response = serviceProviderService.calculateExpectedSalary(serviceProviderId);
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role")
    public ResponseEntity<List<ServiceProviderDTO>> getServiceProvidersByRole(
            @RequestParam("role") HousekeepingRole role) {
        // Validate the input role
        if (role == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<ServiceProviderDTO> serviceProviders = serviceProviderService.getServiceProvidersByRole(role);
        return ResponseEntity.ok(serviceProviders);
    }

    @PostMapping("/upload/{filPath}")
    @ApiOperation(value = "upload service providers")
    public ResponseEntity<String> addServiceProviderEngagement(
            @ApiParam(value = "Service provider engagement data to add", required = true) @PathVariable String filPath) {
        String result = serviceProviderService.uploadExcelRecords(filPath);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ----------API's FOR SERVICE PROVIDER REQUEST ENTITY-----------------
    @GetMapping("/requests/all")
    @ApiOperation(value = ServiceProviderConstants.DESC_RETRIEVE_ALL_SERVICE_PROVIDER_REQUESTS, response = List.class)
    public ResponseEntity<List<ServiceProviderRequestDTO>> getAllServiceProviderRequests(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<ServiceProviderRequestDTO> requests = serviceProviderRequestService.getAllServiceProviderRequestDTOs(page,
                size);
        return ResponseEntity.ok(requests);
    }

    // API to get service provider request by ID
    @GetMapping("/get/request/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_GET_SERVICE_PROVIDER_REQUEST_BY_ID, response = ServiceProviderRequestDTO.class)
    public ResponseEntity<ServiceProviderRequestDTO> getServiceProviderRequestById(
            @ApiParam(value = "ID of the service provider request", required = true) @PathVariable Long id) {
        ServiceProviderRequestDTO serviceProviderRequestDTO = serviceProviderRequestService
                .getServiceProviderRequestDTOById(id);
        return serviceProviderRequestDTO != null ? ResponseEntity.ok(serviceProviderRequestDTO)
                : ResponseEntity.notFound().build();
    }

    // API to add a service provider request
    @PostMapping("/request/add")
    @ApiOperation(value = ServiceProviderConstants.DESC_ADD_NEW_SERVICE_PROVIDER_REQUEST)
    public ResponseEntity<String> addServiceProviderRequest(
            @ApiParam(value = "Service provider request DTO", required = true) @RequestBody ServiceProviderRequestDTO serviceProviderRequestDTO) {
        serviceProviderRequestService.saveServiceProviderRequestDTO(serviceProviderRequestDTO);
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_ADDED);
    }

    // API to update fields of a service provider request
    @PutMapping("/update/request/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_UPDATE_SERVICE_PROVIDER_REQUEST)
    public ResponseEntity<String> updateServiceProviderRequest(
            @ApiParam(value = "ID of the service provider request to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider request DTO", required = true) @RequestBody ServiceProviderRequestDTO serviceProviderRequestDTO) {

        serviceProviderRequestDTO.setRequestId(id); // Set the ID in the DTO
        serviceProviderRequestService.updateServiceProviderRequestDTO(serviceProviderRequestDTO);
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_UPDATED);
    }

    // API to delete a service provider request
    @PatchMapping("/delete/request/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_DELETE_SERVICE_PROVIDER_REQUEST) // Operation description
    public ResponseEntity<String> deleteServiceProviderRequest(
            @ApiParam(value = "ID of the service provider request to deactivate", required = true) @PathVariable Long id) {

        serviceProviderRequestService.deleteServiceProviderRequestDTO(id);
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_REQUEST_DELETED);

    }

    // ------API's FOR SERVICE PROVIDER FEEDBACK ENTITY--------------------
    // API to get all service provider feedbacks with pagination
    @GetMapping("/feedbacks/all")
    @ApiOperation(value = ServiceProviderConstants.DESC_RETRIEVE_ALL_FEEDBACKS, response = List.class)
    public ResponseEntity<List<ServiceProviderFeedbackDTO>> getAllServiceProviderFeedbacks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<ServiceProviderFeedbackDTO> feedbacks = serviceProviderFeedbackService
                .getAllServiceProviderFeedbackDTOs(page, size);
        return ResponseEntity.ok(feedbacks);
    }

    // API to get feedback by ID
    @GetMapping("/get/feedback/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_GET_FEEDBACK_BY_ID, response = ServiceProviderFeedbackDTO.class)
    public ResponseEntity<ServiceProviderFeedbackDTO> getServiceProviderFeedbackById(
            @ApiParam(value = "ID of the feedback", required = true) @PathVariable Long id) {
        ServiceProviderFeedbackDTO feedbackDTO = serviceProviderFeedbackService.getServiceProviderFeedbackDTOById(id);
        return feedbackDTO != null ? ResponseEntity.ok(feedbackDTO) : ResponseEntity.notFound().build();
    }

    // API to add feedback
    @PostMapping("/feedback/add")
    @ApiOperation(value = ServiceProviderConstants.DESC_ADD_NEW_FEEDBACK)
    public ResponseEntity<String> addServiceProviderFeedback(
            @ApiParam(value = "Service provider feedback DTO", required = true) @RequestBody ServiceProviderFeedbackDTO feedbackDTO) {
        serviceProviderFeedbackService.saveServiceProviderFeedbackDTO(feedbackDTO);
        return ResponseEntity.ok(ServiceProviderConstants.FEEDBACK_ADDED);
    }

    // API to update feedback
    @PutMapping("/update/feedback/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_UPDATE_FEEDBACK)
    public ResponseEntity<String> updateServiceProviderFeedback(
            @ApiParam(value = "ID of the feedback to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider feedback DTO", required = true) @RequestBody ServiceProviderFeedbackDTO feedbackDTO) {
        feedbackDTO.setId(id); // Set the ID in the DTO
        serviceProviderFeedbackService.updateServiceProviderFeedbackDTO(feedbackDTO);
        return ResponseEntity.ok(ServiceProviderConstants.FEEDBACK_UPDATED);
    }

    // API to delete feedback
    @DeleteMapping("/delete/feedback/{id}")
    @ApiOperation(value = ServiceProviderConstants.DESC_DELETE_FEEDBACK)
    public ResponseEntity<String> deleteServiceProviderFeedback(
            @ApiParam(value = "ID of the feedback to deactivate", required = true) @PathVariable Long id) {
        serviceProviderFeedbackService.deleteServiceProviderFeedbackDTO(id);
        return ResponseEntity.ok(ServiceProviderConstants.FEEDBACK_DELETED);
    }

    // -------API's FOR SERVICE PROVIDER REQUEST COMMENT ENTITY-------------
    @GetMapping("/comments/all")
    @ApiOperation(value = ServiceProviderConstants.RETRIEVE_ALL_COMMENTS_DESC, response = List.class)
    public ResponseEntity<List<ServiceProviderRequestCommentDTO>> getAllComments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<ServiceProviderRequestCommentDTO> comments = serviceProviderRequestCommentService
                .getAllServiceProviderRequestComments(page, size);
        return ResponseEntity.ok(comments);
    }

    // API to get service provider request comment by ID
    @GetMapping("/get/comment/{id}")
    @ApiOperation(value = ServiceProviderConstants.GET_COMMENT_BY_ID_DESC, response = ServiceProviderRequestCommentDTO.class)
    public ResponseEntity<ServiceProviderRequestCommentDTO> getCommentById(
            @ApiParam(value = "ID of the comment", required = true) @PathVariable Long id) {
        ServiceProviderRequestCommentDTO commentDTO = serviceProviderRequestCommentService
                .getServiceProviderRequestCommentById(id);
        return ResponseEntity.ok(commentDTO);
    }

    // API to add a service provider request comment
    @PostMapping("/comment/add")
    @ApiOperation(value = ServiceProviderConstants.ADD_NEW_COMMENT_DESC)
    public ResponseEntity<String> addComment(
            @ApiParam(value = "Service provider request comment DTO", required = true) @RequestBody ServiceProviderRequestCommentDTO commentDTO) {
        serviceProviderRequestCommentService.saveServiceProviderRequestComment(commentDTO);
        return ResponseEntity.ok(ServiceProviderConstants.COMMENT_ADDED_SUCCESS);
    }

    // API to update a service provider request comment
    @PutMapping("/update/comment/{id}")
    @ApiOperation(value = ServiceProviderConstants.UPDATE_COMMENT_DESC)
    public ResponseEntity<String> updateComment(
            @ApiParam(value = "ID of the comment to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider request comment DTO", required = true) @RequestBody ServiceProviderRequestCommentDTO commentDTO) {

        commentDTO.setId(id); // Set the ID in the DTO
        serviceProviderRequestCommentService.updateServiceProviderRequestComment(id, commentDTO);
        return ResponseEntity.ok(ServiceProviderConstants.COMMENT_UPDATED_SUCCESS);
    }

    // API to delete a service provider request comment
    @DeleteMapping("/delete/comment/{id}")
    @ApiOperation(value = ServiceProviderConstants.DELETE_COMMENT_DESC)
    public ResponseEntity<String> deleteComment(
            @ApiParam(value = "ID of the comment to delete", required = true) @PathVariable Long id) {

        serviceProviderRequestCommentService.deleteServiceProviderRequestComment(id);
        return ResponseEntity.ok(ServiceProviderConstants.COMMENT_DELETED_SUCCESS);
    }

    // -------API's FOR SERVICE PROVIDER ENGAGENENTS ENTITY-------------
    // API to get all service provider engagements with pagination
    // API to get all service provider engagements
    @GetMapping("/engagements/all")
    @ApiOperation(value = ServiceProviderConstants.RETRIEVE_ALL_ENGAGEMENT_DESC, response = List.class)
    public ResponseEntity<List<ServiceProviderEngagementDTO>> getAllServiceProviderEngagements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        if (size == null) {
            size = defaultPageSize; // Default page size if not provided
        }

        List<ServiceProviderEngagementDTO> engagements = serviceProviderEngagementService
                .getAllServiceProviderEngagements(page, size);

        if (engagements.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonList(new ServiceProviderEngagementDTO()));
        }

        // Process each engagement to calculate available times
        engagements.forEach(engagement -> {
            List<String> availableTimes = calculateAvailableTimes(engagement.getTimeslot());
            engagement.setAvailableTimeSlots(availableTimes);
        });

        return ResponseEntity.ok(engagements);
    }

    // API to get service provider engagement by id
    @GetMapping("/get/engagement/{id}")
    @ApiOperation(value = ServiceProviderConstants.GET_BY_ID_ENGAGEMENT_DESC, response = ServiceProviderEngagementDTO.class)
    public ResponseEntity<ServiceProviderEngagementDTO> getServiceProviderEngagementById(
            @ApiParam(value = "ID of the service provider engagement to retrieve", required = true) @PathVariable Long id) {

        ServiceProviderEngagementDTO engagementDTO = serviceProviderEngagementService
                .getServiceProviderEngagementById(id);

        if (engagementDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Engagement not found
        }

        // Calculate available times before returning response
        List<String> availableTimes = calculateAvailableTimes(engagementDTO.getTimeslot());
        engagementDTO.setAvailableTimeSlots(availableTimes);

        return ResponseEntity.ok(engagementDTO);
    }

    // API to add a service provider engagement
    @PostMapping("/engagement/add")
    @ApiOperation(value = ServiceProviderConstants.ADD_NEW_ENGAGEMENT_DESC)
    public ResponseEntity<String> addServiceProviderEngagement(
            @RequestBody ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        try {
            serviceProviderEngagementService.addServiceProviderEngagement(serviceProviderEngagementDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ServiceProviderConstants.ENGAGEMENT_ADDED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ServiceProviderConstants.ENGAGEMENT_ALREADY_EXISTS);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceProviderConstants.ENGAGEMENT_ERROR);
        }
    }

    // API to update a service provider engagement
    @PutMapping("/update/engagement/{id}")
    @ApiOperation(value = ServiceProviderConstants.UPDATE_ENGAGEMENT_DESC)
    public ResponseEntity<String> updateServiceProviderEngagement(
            @ApiParam(value = "ID of the service provider engagement to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider engagement object", required = true) @RequestBody ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        serviceProviderEngagementDTO.setId(id);
        String updateResponse = serviceProviderEngagementService
                .updateServiceProviderEngagement(serviceProviderEngagementDTO);
        if (updateResponse.equals(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.ENGAGEMENT_UPDATED);
    }

    // API to deactivate a service provider engagement
    @PatchMapping("/delete/engagement/{id}")
    @ApiOperation(value = ServiceProviderConstants.DEACTIVATE_ENGAGEMENT_DESC)
    public ResponseEntity<String> deactivateServiceProviderEngagement(
            @ApiParam(value = "ID of the service provider engagement to deactivate", required = true) @PathVariable Long id) {
        String deleteResponse = serviceProviderEngagementService.deleteServiceProviderEngagement(id);
        if (deleteResponse.equals(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ServiceProviderConstants.ENGAGEMENT_NOT_FOUND);
        }
        return ResponseEntity.ok(ServiceProviderConstants.ENGAGEMENT_DELETED);
    }

    // API to get service provider engagements by ServiceProvider ID
    @GetMapping("/get/engagement/by/serviceProvider/{serviceProviderId}")
    @ApiOperation(value = "Retrieve service provider engagements by ServiceProvider ID", response = List.class)
    public ResponseEntity<List<ServiceProviderEngagementDTO>> getServiceProviderEngagementsByServiceProviderId(
            @ApiParam(value = "ServiceProvider ID to retrieve engagements for", required = true) @PathVariable Long serviceProviderId) {

        List<ServiceProviderEngagementDTO> engagements = serviceProviderEngagementService
                .getServiceProviderEngagementsByServiceProviderId(serviceProviderId);

        // Calculate available times for each engagement before returning response
        engagements.forEach(engagement -> {
            List<String> availableTimes = calculateAvailableTimes(engagement.getTimeslot());
            engagement.setAvailableTimeSlots(availableTimes);
        });

        return ResponseEntity.ok(engagements);
    }

    // API to get service provider engagements by Customer ID
    @GetMapping("/get/engagement/by/customer/{customerId}")
    @ApiOperation(value = "Retrieve service provider engagements by Customer ID", response = List.class)
    public ResponseEntity<List<ServiceProviderEngagementDTO>> getServiceProviderEngagementsByCustomerId(
            @ApiParam(value = "Customer ID to retrieve engagements for", required = true) @PathVariable Long customerId) {
        List<ServiceProviderEngagementDTO> engagements = serviceProviderEngagementService
                .getServiceProviderEngagementsByCustomerId(customerId);
        // Calculate available times for each engagement before returning response
        engagements.forEach(engagement -> {
            List<String> availableTimes = calculateAvailableTimes(engagement.getTimeslot());
            engagement.setAvailableTimeSlots(availableTimes);
        });

        return ResponseEntity.ok(engagements);
    }

    // API to get booking history
    @GetMapping("/get-sp-booking-history")
    @ApiOperation(value = "Retrieve categorized service provider engagements", response = Map.class)
    public ResponseEntity<Map<String, List<ServiceProviderEngagementDTO>>> getCategorizedServiceProviderEngagements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        if (size == null) {
            size = defaultPageSize;
        }

        Map<String, List<ServiceProviderEngagementDTO>> categorizedEngagements = serviceProviderEngagementService
                .getServiceProviderBookingHistory(page, size);

        if (categorizedEngagements == null || categorizedEngagements.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyMap());
        }

        return ResponseEntity.ok(categorizedEngagements);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Object>> searchEngagements(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String timeslot,
            @RequestParam HousekeepingRole housekeepingRole,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false, defaultValue = "5") int precision) {

        List<Object> engagements = serviceProviderEngagementService
                .getEngagementsByExactDateTimeslotAndHousekeepingRole(startDate, endDate, timeslot, housekeepingRole, latitude, longitude, precision);

        if (engagements.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(engagements);
    }

    // ------API's FOR SHORTLISTED SERVICEPROVIDER------------------
    // API to get all shortlisted service providers with pagination
    @GetMapping("/shortlisted/all")
    @ApiOperation(value = "Retrieve all shortlisted service providers", response = List.class)
    public ResponseEntity<List<ShortListedServiceProviderDTO>> getAllShortListedServiceProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        if (size == null) {
            size = defaultPageSize;
        }
        List<ShortListedServiceProviderDTO> providers = shortListedServiceProviderService
                .getAllShortListedServiceProviders(page, size);
        return ResponseEntity.ok(providers);
    }

    // API to get shortlisted service provider by ID
    @GetMapping("/get/shortlisted/{id}")
    @ApiOperation(value = "Retrieve shortlisted service provider by ID", response = ShortListedServiceProviderDTO.class)
    public ResponseEntity<ShortListedServiceProviderDTO> getShortListedServiceProviderById(
            @ApiParam(value = "ID of the shortlisted service provider to retrieve", required = true) @PathVariable Long id) {
        ShortListedServiceProviderDTO providerDTO = shortListedServiceProviderService
                .getShortListedServiceProviderById(id);
        return ResponseEntity.ok(providerDTO);
    }

    // API to add a new shortlisted service provider
    @PostMapping("/shortlisted/add")
    @ApiOperation(value = "Add a new shortlisted service provider")
    public ResponseEntity<String> addShortListedServiceProvider(
            @ApiParam(value = "Shortlisted service provider data to add", required = true) @RequestBody ShortListedServiceProviderDTO shortListedServiceProviderDTO) {
        String result = shortListedServiceProviderService.addShortListedServiceProvider(shortListedServiceProviderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // API to update an existing shortlisted service provider
    @PutMapping("/update/shortlisted/{id}")
    @ApiOperation(value = "Update an existing shortlisted service provider")
    public ResponseEntity<String> updateShortListedServiceProvider(
            @ApiParam(value = "ID of the shortlisted service provider to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated shortlisted service provider object", required = true) @RequestBody ShortListedServiceProviderDTO shortListedServiceProviderDTO) {
        shortListedServiceProviderDTO.setId(id);
        String result = shortListedServiceProviderService
                .updateShortListedServiceProvider(shortListedServiceProviderDTO);
        return ResponseEntity.ok(result);
    }

    // API to delete a shortlisted service provider
    @DeleteMapping("/delete/shortlisted/{id}")
    @ApiOperation(value = "Delete a shortlisted service provider")
    public ResponseEntity<String> deleteShortListedServiceProvider(
            @ApiParam(value = "ID of the shortlisted service provider to delete", required = true) @PathVariable Long id) {
        String result = shortListedServiceProviderService.deleteShortListedServiceProvider(id);
        return ResponseEntity.ok(result);
    }

    // API to remove a specific service provider from the shortlisted list
    @DeleteMapping("/remove/shortlisted/{customerId}/{serviceProviderId}")
    public ResponseEntity<String> removeServiceProviderId(
            @PathVariable Long customerId,
            @PathVariable String serviceProviderId) {
        String response = shortListedServiceProviderService.removeFromServiceProviderIdList(customerId,
                serviceProviderId);
        return ResponseEntity.ok(response);
    }

    // ------API's FOR ATTENDANCE-----------------

    // API to get all attendance records
    @GetMapping("/attendance/all")
    @ApiOperation(value = "Retrieve all attendance records", response = List.class)
    public ResponseEntity<List<AttendanceDTO>> getAllAttendanceRecords(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<AttendanceDTO> attendanceList = attendanceService.getAllAttendance(page, size);
        return ResponseEntity.ok(attendanceList);
    }

    // API to get an attendance record by ID
    @GetMapping("/get/attendance/{id}")
    @ApiOperation(value = "Retrieve an attendance record by ID", response = AttendanceDTO.class)
    public ResponseEntity<AttendanceDTO> getAttendanceRecordById(
            @ApiParam(value = "ID of the attendance record", required = true) @PathVariable Long id) {

        AttendanceDTO attendanceDTO = attendanceService.getAttendanceByAttendenceId(id);
        return attendanceDTO != null ? ResponseEntity.ok(attendanceDTO)
                : ResponseEntity.notFound().build();
    }

    // Retrieve all AttendanceDTOs for a specific customer ID
    @GetMapping("/get/customer/{customerId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByCustomerId(@PathVariable Long customerId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCustomerId(customerId);
        return ResponseEntity.ok(attendances);
    }

    // Retrieve all AttendanceDTOs for a specific service provider ID
    @GetMapping("/get/serviceProvider/{serviceProviderId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByServiceProviderId(@PathVariable Long serviceProviderId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByServiceProviderId(serviceProviderId);
        return ResponseEntity.ok(attendances);
    }

    // API to add a new attendance record
    @PostMapping("/attendance/add")
    @ApiOperation(value = "Add a new attendance record")
    public ResponseEntity<String> addAttendanceRecord(
            @ApiParam(value = "Attendance record DTO", required = true) @RequestBody AttendanceDTO attendanceDTO) {

        attendanceService.saveAttendance(attendanceDTO);
        return ResponseEntity.ok(ServiceProviderConstants.ATTENDANCE_ADDED);
    }

    // API to update an attendance record
    @PutMapping("/update/attendance/{id}")
    @ApiOperation(value = "Update an attendance record by ID")
    public ResponseEntity<String> updateAttendanceRecord(
            @ApiParam(value = "ID of the attendance record to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated attendance record DTO", required = true) @RequestBody AttendanceDTO attendanceDTO) {

        attendanceDTO.setId(id); // Set the ID in the DTO
        attendanceService.updateAttendance(attendanceDTO);
        return ResponseEntity.ok(ServiceProviderConstants.ATTENDANCE_UPDATED);
    }

    // API to delete an attendance record (mark as deleted)
    @PatchMapping("/delete/attendance/{id}")
    @ApiOperation(value = "Delete (resolve) an attendance record by ID")
    public ResponseEntity<String> deleteAttendanceRecord(
            @ApiParam(value = "ID of the attendance record to delete", required = true) @PathVariable Long id) {

        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ServiceProviderConstants.ATTENDANCE_DELETED);
    }

    @GetMapping("/get/notifications")
    public ResponseEntity<List<AttendanceDTO>> getAllNotifications() {
        List<AttendanceDTO> notifications = attendanceService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get conflicts for today
    @GetMapping("/get/today/conflicts")
    public ResponseEntity<List<AttendanceDTO>> getTodayConflicts() {
        List<AttendanceDTO> notifications = attendanceService.getTodayConflicts();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get conflicts for the past one week
    @GetMapping("/get/oneweek/conflicts")
    public ResponseEntity<List<AttendanceDTO>> getOneWeekConflicts() {
        List<AttendanceDTO> notifications = attendanceService.getOneWeekConflicts();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get conflicts for the past two weeks
    @GetMapping("/get/twoweeks/conflicts")
    public ResponseEntity<List<AttendanceDTO>> getTwoWeeksConflicts() {
        List<AttendanceDTO> notifications = attendanceService.getTwoWeeksConflicts();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get conflicts for the past one month
    @GetMapping("/get/onemonth/conflicts")
    public ResponseEntity<List<AttendanceDTO>> getOneMonthConflicts() {
        List<AttendanceDTO> notifications = attendanceService.getOneMonthConflicts();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch all attendance records where isCustomerAgreed is false
    @GetMapping("/customer/notagreed")
    public ResponseEntity<List<AttendanceDTO>> getAllCustomerNotAgreedAttendance() {
        List<AttendanceDTO> notifications = attendanceService.getAllCustomerNotAgreedAttendance();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isCustomerAgreed is false for
    // today
    @GetMapping("/customer/notagreed/today")
    public ResponseEntity<List<AttendanceDTO>> getTodayCustomerNotAgreed() {
        List<AttendanceDTO> notifications = attendanceService.getTodayCustomerNotAgreed();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isCustomerAgreed is false for the
    // past week
    @GetMapping("/customer/notagreed/week")
    public ResponseEntity<List<AttendanceDTO>> getLastWeekCustomerNotAgreed() {
        List<AttendanceDTO> notifications = attendanceService.getLastWeekCustomerNotAgreed();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isCustomerAgreed is false for the
    // past two weeks
    @GetMapping("/customer/notagreed/twoweeks")
    public ResponseEntity<List<AttendanceDTO>> getLastTwoWeeksCustomerNotAgreed() {
        List<AttendanceDTO> notifications = attendanceService.getLastTwoWeeksCustomerNotAgreed();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isCustomerAgreed is false for the
    // past month
    @GetMapping("/customer/notagreed/month")
    public ResponseEntity<List<AttendanceDTO>> getLastMonthCustomerNotAgreed() {
        List<AttendanceDTO> notifications = attendanceService.getLastMonthCustomerNotAgreed();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch all attendance records where isAttended is false
    @GetMapping("/service/notattended")
    public ResponseEntity<List<AttendanceDTO>> getAllNotAttendedRecords() {
        List<AttendanceDTO> notifications = attendanceService.getAllNotAttendedRecords();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isAttended is false for today
    @GetMapping("/service/notattended/today")
    public ResponseEntity<List<AttendanceDTO>> getTodayNotAttendedRecords() {
        List<AttendanceDTO> notifications = attendanceService.getTodayNotAttendedRecords();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isAttended is false for the past
    // week
    @GetMapping("/service/notattended/week")
    public ResponseEntity<List<AttendanceDTO>> getOneWeekNotAttendedRecords() {
        List<AttendanceDTO> notifications = attendanceService.getOneWeekNotAttendedRecords();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isAttended is false for the past
    // two weeks
    @GetMapping("/service/notattended/twoweeks")
    public ResponseEntity<List<AttendanceDTO>> getTwoWeeksNotAttendedRecords() {
        List<AttendanceDTO> notifications = attendanceService.getTwoWeeksNotAttendedRecords();
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to fetch attendance records where isAttended is false for the past
    // month
    @GetMapping("/service/notattended/month")
    public ResponseEntity<List<AttendanceDTO>> getOneMonthNotAttendedRecords() {
        List<AttendanceDTO> notifications = attendanceService.getOneMonthNotAttendedRecords();
        return ResponseEntity.ok(notifications);
    }

    // --------------API's FOR SERVICE PROVIDER
    // LEAVE-----------------------------------------------
    @GetMapping("/get-all-leaves")
    @ApiOperation(value = "Retrieve all service provider leave records", response = List.class)
    public ResponseEntity<List<ServiceProviderLeaveDTO>> getAllServiceProviderLeaves() {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService.getAllLeaves();

        if (leaves == null || leaves.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/get-leave-by-id/{id}")
    @ApiOperation(value = "Retrieve service provider leave by ID", response = ServiceProviderLeaveDTO.class)
    public ResponseEntity<ServiceProviderLeaveDTO> getServiceProviderLeaveById(
            @ApiParam(value = "ID of the service provider leave", required = true) @PathVariable Long id) {

        ServiceProviderLeaveDTO serviceProviderLeaveDTO = serviceProviderLeaveService.getLeaveById(id);

        if (serviceProviderLeaveDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(serviceProviderLeaveDTO);
    }

    @GetMapping("/get-leave-by-sp-id/{serviceProviderId}")
    @ApiOperation(value = "Retrieve service provider leaves by service provider ID", response = List.class)
    public ResponseEntity<List<ServiceProviderLeaveDTO>> getServiceProviderLeavesByServiceProviderId(
            @ApiParam(value = "Service provider ID", required = true) @PathVariable Long serviceProviderId) {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService
                .getLeaveByServiceProviderId(serviceProviderId);
        if (leaves == null || leaves.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(leaves);
    }

    // API to add leave
    @PostMapping("/add-leave")
    @ApiOperation(value = "Add a new service provider leave record")
    public ResponseEntity<String> addServiceProviderLeave(
            @ApiParam(value = "Service provider leave DTO", required = true) @RequestBody ServiceProviderLeaveDTO leaveDTO) {
        String response = serviceProviderLeaveService.addLeave(leaveDTO);
        if (ServiceProviderConstants.FAILED_RESPONSE.equals(response)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceProviderConstants.FAILED_RESPONSE);
        }
        return ResponseEntity.ok(response);
    }

    // API to update a service provider leave record by ID
    @PutMapping("/update-leave/{id}")
    @ApiOperation(value = "Update service provider leave record by ID")
    public ResponseEntity<String> updateServiceProviderLeave(
            @ApiParam(value = "ID of the service provider leave to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider leave DTO", required = true) @RequestBody ServiceProviderLeaveDTO leaveDTO) {
        String response = serviceProviderLeaveService.updateLeave(id, leaveDTO);
        if (ServiceProviderConstants.DATA_NOT_FOUND.equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.DATA_NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    // API to delete a service provider leave record by ID
    @DeleteMapping("/delete-leave/{id}")
    @ApiOperation(value = "Delete service provider leave record by ID")
    public ResponseEntity<String> deleteServiceProviderLeave(
            @ApiParam(value = "ID of the service provider leave to delete", required = true) @PathVariable Long id) {
        String response = serviceProviderLeaveService.deleteLeave(id);
        if (ServiceProviderConstants.DATA_NOT_FOUND.equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.DATA_NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    // API to get service providers on leave today
    @GetMapping("/on-leave-today")
    @ApiOperation(value = "Retrieve service provider leaves on today", response = List.class)
    public ResponseEntity<Object> getServiceProvidersOnLeaveToday() {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService.getServiceProvidersOnLeaveToday();

        if (leaves == null || leaves.isEmpty()) {
            // Returning a String when no data is found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.NO_DATA_FOUND);
        }

        // Returning a List of ServiceProviderLeaveDTO when data is found
        return ResponseEntity.ok(leaves);
    }

    // API to get service providers on leave next week
    @GetMapping("/on-leave-next-week")
    @ApiOperation(value = "Retrieve service provider leaves next week", response = List.class)
    public ResponseEntity<Object> getServiceProvidersOnLeaveNextWeek() {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService.getServiceProvidersOnLeaveNextWeek();
        if (leaves == null || leaves.isEmpty()) {
            // Using constant instead of the literal string
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.NO_DATA_FOUND);
        }
        return ResponseEntity.ok(leaves);
    }

    // API to get approved service provider leaves
    @GetMapping("/get-approved-leaves")
    @ApiOperation(value = "Retrieve approved service provider leaves", response = List.class)
    public ResponseEntity<Object> getApprovedServiceProviderLeaves() {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService.getApprovedLeaves();
        if (leaves == null || leaves.isEmpty()) {
            // Using constant instead of the literal string
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.NO_DATA_FOUND);
        }
        return ResponseEntity.ok(leaves);
    }

    // API to get unapproved service provider leaves
    @GetMapping("/get-unapproved-leaves")
    @ApiOperation(value = "Retrieve unapproved service provider leaves", response = List.class)
    public ResponseEntity<Object> getUnapprovedServiceProviderLeaves() {
        List<ServiceProviderLeaveDTO> leaves = serviceProviderLeaveService.getUnapprovedLeaves();
        if (leaves == null || leaves.isEmpty()) {
            // Using constant instead of the literal string
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.NO_DATA_FOUND);
        }
        return ResponseEntity.ok(leaves);
    }

    // ----------------API's FOR LEAVE
    // BALANCE------------------------------------------------------
    // API to get all leave balance records
    // API to get all leave balances
    @GetMapping("/get-all-leave-balances")
    @ApiOperation(value = "Retrieve all leave balance records", response = List.class)
    public ResponseEntity<Object> getAllLeaveBalances() {
        List<LeaveBalanceDTO> leaveBalances = leaveBalanceService.getAllLeaveBalances();
        if (leaveBalances == null || leaveBalances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.NO_DATA_FOUND);
        }
        return ResponseEntity.ok(leaveBalances);
    }

    // API to get leave balance by ID
    @GetMapping("/get-balance-by-id/{id}")
    @ApiOperation(value = "Retrieve leave balance by ID", response = LeaveBalanceDTO.class)
    public ResponseEntity<Object> getLeaveBalanceById(
            @ApiParam(value = "ID of the leave balance", required = true) @PathVariable Long id) {
        LeaveBalanceDTO leaveBalanceDTO = leaveBalanceService.getLeaveBalanceById(id);
        if (leaveBalanceDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ServiceProviderConstants.DATA_NOT_FOUND);
        }
        return ResponseEntity.ok(leaveBalanceDTO);
    }

    // API to add a new leave balance record
    @PostMapping("/add-leavebalance")
    @ApiOperation(value = "Add a new leave balance record")
    public ResponseEntity<Object> addLeaveBalance(
            @ApiParam(value = "Leave balance DTO", required = true) @RequestBody LeaveBalanceDTO leaveBalanceDTO) {
        String response = leaveBalanceService.addLeaveBalance(leaveBalanceDTO);
        if ("Failed".equals(response)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ServiceProviderConstants.FAILED_TO_ADD);
        }
        return ResponseEntity.ok(response);
    }

    // API to update a leave balance record by ID
    @PutMapping("/update-balance/{id}")
    @ApiOperation(value = "Update leave balance record by ID")
    public ResponseEntity<Object> updateLeaveBalance(
            @ApiParam(value = "ID of the leave balance to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated leave balance DTO", required = true) @RequestBody LeaveBalanceDTO leaveBalanceDTO) {
        String response = leaveBalanceService.updateLeaveBalance(id, leaveBalanceDTO);
        if (ServiceProviderConstants.DATA_NOT_FOUND.equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // API to delete a leave balance record by ID
    @DeleteMapping("/delete-balance/{id}")
    @ApiOperation(value = "Delete leave balance record by ID")
    public ResponseEntity<Object> deleteLeaveBalance(
            @ApiParam(value = "ID of the leave balance to delete", required = true) @PathVariable Long id) {
        String response = leaveBalanceService.deleteLeaveBalance(id);
        if (ServiceProviderConstants.DATA_NOT_FOUND.equals(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

}
