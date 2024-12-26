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

import com.springboot.app.dto.AttendanceDTO;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
//import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.dto.ServiceProviderRequestDTO;
import com.springboot.app.dto.ShortListedServiceProviderDTO;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

import com.springboot.app.service.ServiceProviderRequestService;
import com.springboot.app.service.AttendanceService;
import com.springboot.app.service.ServiceProviderEngagementService;
import com.springboot.app.service.ServiceProviderFeedbackService;
import com.springboot.app.service.ServiceProviderRequestCommentService;
import com.springboot.app.service.ServiceProviderService;
import com.springboot.app.service.ShortListedServiceProviderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(ServiceProviderConstants.BASE_API_PATH)
@Api(value = ServiceProviderConstants.API_VALUE, tags = ServiceProviderConstants.TAG_SERVICEPROVIDERS)
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceProviderRequestService serviceProviderRequestService;

    @Autowired
    private ServiceProviderFeedbackService serviceProviderFeedbackService;

    @Autowired
    private ServiceProviderRequestCommentService serviceProviderRequestCommentService;

    @Autowired
    private ServiceProviderEngagementService serviceProviderEngagementService;

    @Autowired
    private ShortListedServiceProviderService shortListedServiceProviderService;

    @Autowired
    private AttendanceService attendanceService;

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    // --------API's FOR SERVICE PROVIDER ENTITY--------------------
    @GetMapping("/serviceproviders/all")
    @ApiOperation(value = ServiceProviderConstants.RETRIEVE_ALL_DESC, response = List.class)
    public ResponseEntity<List<ServiceProviderDTO>> getAllServiceProviders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<ServiceProviderDTO> serviceProviders = serviceProviderService.getAllServiceProviderDTOs(page, size);
        return ResponseEntity.ok(serviceProviders);
    }

    // API to get serviceproviders by id
    @GetMapping("/get/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.GET_BY_ID_DESC, response = ServiceProviderDTO.class)
    public ResponseEntity<ServiceProviderDTO> getServiceProviderById(
            @ApiParam(value = "ID of the service provider", required = true) @PathVariable Long id) {
        ServiceProviderDTO serviceProviderDTO = serviceProviderService.getServiceProviderDTOById(id);
        return serviceProviderDTO != null ? ResponseEntity.ok(serviceProviderDTO) : ResponseEntity.notFound().build();
    }

    // API to add a serviceprovider
    @PostMapping("/serviceprovider/add")
    @ApiOperation(value = ServiceProviderConstants.ADD_NEW_DESC)
    public ResponseEntity<String> addServiceProvider(
            @ApiParam(value = "Service provider DTO", required = true) @RequestBody ServiceProviderDTO serviceProviderDTO) {
        serviceProviderService.saveServiceProviderDTO(serviceProviderDTO);
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_ADDED);
    }

    // API to update fields
    @PutMapping("/update/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.UPDATE_DESC)
    public ResponseEntity<String> updateServiceProvider(
            @ApiParam(value = "ID of the service provider to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider DTO", required = true) @RequestBody ServiceProviderDTO serviceProviderDTO) {

        serviceProviderDTO.setServiceproviderId(id); // Set the ID in the DTO
        serviceProviderService.updateServiceProviderDTO(serviceProviderDTO); // Update using the DTO
        return ResponseEntity.ok(ServiceProviderConstants.SERVICE_PROVIDER_UPDATED);
    }

    // API to delete a serviceprovider
    @PatchMapping("delete/serviceprovider/{id}")
    @ApiOperation(value = ServiceProviderConstants.DELETE_DESC) // Operation description
    public ResponseEntity<String> deleteServiceProvider(
            @ApiParam(value = "ID of the service provider to deactivate", required = true) @PathVariable Long id) {

        serviceProviderService.deleteServiceProviderDTO(id);
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
            @ApiParam(value = "Maximum age of the service provider") @RequestParam(required = false) Integer maxAge) {

        // Call the service method with the provided filters
        List<ServiceProviderDTO> serviceProviders = serviceProviderService.getfilters(language, rating, gender,
                speciality, housekeepingRole, minAge, maxAge);
        return ResponseEntity.ok(serviceProviders);
    }

    // Endpoint to filter by only one parameter at a time
    @GetMapping("/filter")
    public ResponseEntity<?> getServiceProvidersBySingleFilter(
            @RequestParam(required = false) Integer pincode,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String locality) {

        // Count the number of parameters provided
        int paramCount = 0;
        if (pincode != null)
            paramCount++;
        if (street != null)
            paramCount++;
        if (locality != null)
            paramCount++;

        // Check if more than one parameter is provided
        if (paramCount != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please provide only one filter parameter: pincode, street, or locality.");
        }

        // Call the service method to get results based on the single provided parameter
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
    @GetMapping("/engagements/all")
    @ApiOperation(value = "Retrieve all service provider engagements with pagination", response = List.class)
    public ResponseEntity<List<ServiceProviderEngagementDTO>> getAllServiceProviderEngagements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        if (size == null) {
            size = defaultPageSize; // Replace with the actual default page size
        }
        List<ServiceProviderEngagementDTO> engagements = serviceProviderEngagementService
                .getAllServiceProviderEngagements(page, size);

        // Fallback to the first page if the requested page is empty
        if (engagements.isEmpty() && page > 0) {
            return getAllServiceProviderEngagements(0, size);
        }

        return ResponseEntity.ok(engagements);
    }

    // API to get service provider engagement by ID
    @GetMapping("/get/engagement/{id}")
    @ApiOperation(value = "Retrieve service provider engagement by ID", response = ServiceProviderEngagementDTO.class)
    public ResponseEntity<ServiceProviderEngagementDTO> getServiceProviderEngagementById(
            @ApiParam(value = "ID of the service provider engagement to retrieve", required = true) @PathVariable Long id) {
        ServiceProviderEngagementDTO engagementDTO = serviceProviderEngagementService
                .getServiceProviderEngagementById(id);
        return engagementDTO != null ? ResponseEntity.ok(engagementDTO) : ResponseEntity.notFound().build();
    }

    // API to add a new service provider engagement
    @PostMapping("/engagement/add")
    @ApiOperation(value = "Add a new service provider engagement")
    public ResponseEntity<String> addServiceProviderEngagement(
            @ApiParam(value = "Service provider engagement data to add", required = true) @RequestBody ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        String result = serviceProviderEngagementService.addServiceProviderEngagement(serviceProviderEngagementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // API to update an existing service provider engagement
    @PutMapping("/update/engagement/{id}")
    @ApiOperation(value = "Update an existing service provider engagement")
    public ResponseEntity<String> updateServiceProviderEngagement(
            @ApiParam(value = "ID of the service provider engagement to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider engagement object", required = true) @RequestBody ServiceProviderEngagementDTO serviceProviderEngagementDTO) {
        serviceProviderEngagementDTO.setId(id); // Set the ID in the DTO
        String result = serviceProviderEngagementService.updateServiceProviderEngagement(serviceProviderEngagementDTO);
        return ResponseEntity.ok(result);
    }

    // API to deactivate a service provider engagement
    @PatchMapping("/delete/engagement/{id}")
    @ApiOperation(value = "Deactivate a service provider engagement") // Operation description
    public ResponseEntity<String> deactivateServiceProviderEngagement(
            @ApiParam(value = "ID of the service provider engagement to deactivate", required = true) @PathVariable Long id) {

        String result = serviceProviderEngagementService.deleteServiceProviderEngagement(id);
        return ResponseEntity.ok(result);
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

}
