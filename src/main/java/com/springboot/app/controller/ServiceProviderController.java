package com.springboot.app.controller;

import java.util.List;

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

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.dto.ServiceProviderRequestDTO;

import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

import com.springboot.app.service.ServiceProviderRequestService;
import com.springboot.app.service.ServiceProviderEngagementService;
import com.springboot.app.service.ServiceProviderFeedbackService;
import com.springboot.app.service.ServiceProviderRequestCommentService;
import com.springboot.app.service.ServiceProviderService;

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

        // Check that only one parameter is provided
        int paramCount = 0;
        if (pincode != null)
            paramCount++;
        if (street != null)
            paramCount++;
        if (locality != null)
            paramCount++;

        if (paramCount != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please provide only one filter parameter: pincode, street, or locality.");
        }

        // Call service method with the provided single parameter
        List<ServiceProviderDTO> results = serviceProviderService.getServiceProvidersByFilter(pincode, street,
                locality);
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
    @PatchMapping("/deactivate/engagement/{id}")
    @ApiOperation(value = "Deactivate a service provider engagement") // Operation description
    public ResponseEntity<String> deactivateServiceProviderEngagement(
            @ApiParam(value = "ID of the service provider engagement to deactivate", required = true) @PathVariable Long id) {

        String result = serviceProviderEngagementService.deleteServiceProviderEngagement(id);
        return ResponseEntity.ok(result);
    }

}
