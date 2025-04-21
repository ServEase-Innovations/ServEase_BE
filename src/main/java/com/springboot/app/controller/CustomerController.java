package com.springboot.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerConcernDTO;
import com.springboot.app.dto.CustomerDTO;
import com.springboot.app.dto.CustomerRequestDTO;
import com.springboot.app.dto.KYCCommentsDTO;
import com.springboot.app.dto.KYCDTO;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.Status;
import com.springboot.app.dto.CustomerFeedbackDTO;
import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.dto.CustomerRequestCommentDTO;
import com.springboot.app.service.CustomerConcernService;
import com.springboot.app.service.CustomerFeedbackService;
import com.springboot.app.service.CustomerHolidaysService;
import com.springboot.app.service.CustomerRequestCommentService;
import com.springboot.app.service.CustomerRequestService;
import com.springboot.app.service.CustomerService;
import com.springboot.app.service.KYCCommentsService;
import com.springboot.app.service.KYCService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/customer")
@Api(value = "Customer operations API", tags = "Customer")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRequestService customerRequestService;
    private final CustomerConcernService customerConcernService;
    private final CustomerFeedbackService customerFeedbackService;
    private final CustomerRequestCommentService customerRequestCommentService;
    private final KYCService kycService;
    private final KYCCommentsService kycCommentsService;
    private final CustomerHolidaysService customerHolidaysService;

    // Constructor-based injection
    @Autowired
    public CustomerController(CustomerService customerService,
            CustomerRequestService customerRequestService,
            CustomerConcernService customerConcernService,
            CustomerFeedbackService customerFeedbackService,
            CustomerRequestCommentService customerRequestCommentService,
            KYCService kycService,
            KYCCommentsService kycCommentsService,
            CustomerHolidaysService customerHolidaysService) {
        this.customerService = customerService;
        this.customerRequestService = customerRequestService;
        this.customerConcernService = customerConcernService;
        this.customerFeedbackService = customerFeedbackService;
        this.customerRequestCommentService = customerRequestCommentService;
        this.kycService = kycService;
        this.kycCommentsService = kycCommentsService;
        this.customerHolidaysService = customerHolidaysService;
    }

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    // --------------------------API's FOR CUSTOMER
    // ENTITY----------------------------------------
    // API to get all customers
    // API to get all customers with pagination
    @GetMapping("/get-all-customers")
    @ApiOperation(value = CustomerConstants.RETRIEVE_ALL_DESC, response = List.class)
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            List<CustomerDTO> customers = customerService.getAllCustomers(page, size);
            if (customers.isEmpty() && page > 0) {
                return getAllCustomers(0, size); // retry with page 0
            }
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customers: " + e.getMessage());
        }
    }

    // API to get customer by id
    @GetMapping("/get-customer-by-id/{id}")
    @ApiOperation(value = CustomerConstants.GET_BY_ID_DESC, response = CustomerDTO.class)
    public ResponseEntity<?> getCustomerById(
            @ApiParam(value = "ID of the customer to retrieve", required = true) @PathVariable Long id) {
        try {
            CustomerDTO customerDTO = customerService.getCustomerById(id);
            return ResponseEntity.ok(customerDTO != null ? customerDTO : new CustomerDTO());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer: " + e.getMessage());
        }
    }

    // API to add a customer
    @PostMapping("/add-customer")
    @ApiOperation(value = CustomerConstants.ADD_NEW_DESC)
    public ResponseEntity<?> addCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            customerService.saveCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add customer: " + e.getMessage());
        }
    }

    // API to update a customer
    @PutMapping("/update-customer/{id}")
    @ApiOperation(value = CustomerConstants.UPDATE_DESC)
    public ResponseEntity<?> updateCustomer(
            @ApiParam(value = "ID of the customer to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated customer object", required = true) @ModelAttribute CustomerDTO customerDTO,
            @ApiParam(value = "Updated profile picture of the customer") @RequestParam(value = "profilePic", required = false) MultipartFile profilePic) {
        try {
            customerDTO.setCustomerId(id);
            customerService.updateCustomer(customerDTO);
            return ResponseEntity.ok(CustomerConstants.UPDATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update customer: " + e.getMessage());
        }
    }

    // API to delete a customer
    @PatchMapping("/delete-customer/{id}")
    @ApiOperation(value = CustomerConstants.DELETE_DESC)
    public ResponseEntity<?> deleteCustomer(
            @ApiParam(value = "ID of the customer to delete", required = true) @PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(CustomerConstants.DELETED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete customer: " + e.getMessage());
        }
    }

    // ----------------------API's FOR CUSTOMER REQUEST
    // ENTITY---------------------------------
    // API to retrieve all customer requests with pagination
    @GetMapping("/get-all-customer-requests")
    @ApiOperation(value = "Retrieve all customer requests", response = List.class)
    public ResponseEntity<?> getAllCustomerRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            List<CustomerRequestDTO> requests = customerRequestService.getAll(page, size);
            if (requests.isEmpty() && page > 0) {
                return getAllCustomerRequests(0, size);
            }
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve requests: " + e.getMessage());
        }
    }

    // API to retrieve categorized customer requests
    @GetMapping("/get-booking-history")
    @ApiOperation(value = "Retrieve categorized customer requests", response = Map.class)
    public ResponseEntity<?> getCategorizedCustomerRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            Map<String, List<CustomerRequestDTO>> categorizedRequests = customerRequestService.getBookingHistory(page,
                    size);
            if (categorizedRequests == null || categorizedRequests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Data Found");
            }
            return ResponseEntity.ok(categorizedRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve booking history: " + e.getMessage());
        }
    }

    // API to get customer request by ID
    @GetMapping("/get-customer-request-by-id/{requestId}")
    @ApiOperation(value = "Get customer request by ID", response = CustomerRequestDTO.class)
    public ResponseEntity<?> getCustomerRequestById(@PathVariable Long requestId) {
        try {
            CustomerRequestDTO requestDTO = customerRequestService.getByRequestId(requestId);
            return ResponseEntity.ok(requestDTO != null ? requestDTO : new CustomerRequestDTO());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer request: " + e.getMessage());
        }
    }

    // API to retrieve all open requests with pagination
    @GetMapping("/get-open-requests")
    @ApiOperation(value = "Retrieve all open requests", response = List.class)
    public ResponseEntity<?> getAllOpenRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            List<CustomerRequestDTO> openRequests = customerRequestService.getAllOpenRequests(page, size);
            if (openRequests.isEmpty() && page > 0) {
                return getAllOpenRequests(0, size);
            }
            return ResponseEntity.ok(openRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve open requests: " + e.getMessage());
        }
    }

    // API to retrieve all potential customers with pagination
    @GetMapping("/get-potential-customers")
    @ApiOperation(value = "Retrieve all potential customers", response = List.class)
    public ResponseEntity<?> getAllPotentialCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            List<CustomerRequestDTO> potentialCustomers = customerRequestService.findAllPotentialCustomers(page, size);
            if (potentialCustomers.isEmpty() && page > 0) {
                return getAllPotentialCustomers(0, size);
            }
            return ResponseEntity.ok(potentialCustomers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve potential customers: " + e.getMessage());
        }
    }

    // API to add a customer request
    @PostMapping("/add-customer-request")
    @ApiOperation(value = "Add a new customer request")
    public ResponseEntity<?> insertCustomerRequest(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try {
            customerRequestService.insert(customerRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add customer request: " + e.getMessage());
        }
    }

    // API to update customer request
    @PutMapping("/update-customer-request/{requestId}")
    @ApiOperation(value = "Update an existing customer request")
    public ResponseEntity<?> updateCustomerRequest(@PathVariable Long requestId,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        try {
            customerRequestDTO.setRequestId(requestId);
            customerRequestService.update(customerRequestDTO);
            return ResponseEntity.ok(CustomerConstants.UPDATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update customer request: " + e.getMessage());
        }
    }

    // API to filter customer requests with pagination
    @GetMapping("/filter-customer-request")
    @ApiOperation(value = "Filter customer requests", response = List.class)
    public ResponseEntity<?> getRequestFilters(
            @RequestParam(required = false) HousekeepingRole housekeepingRole,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer pincode,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String apartment_name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<CustomerRequestDTO> filteredRequests = customerRequestService.getRequestFilters(
                    housekeepingRole, gender, area, pincode, locality, apartment_name, page, size);
            if (filteredRequests.isEmpty() && page > 0) {
                return getRequestFilters(housekeepingRole, gender, area, pincode, locality, apartment_name, 0, size);
            }
            return ResponseEntity.ok(filteredRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to filter customer requests: " + e.getMessage());
        }
    }

    // API to update the status of a customer request
    @PatchMapping("/{requestId}/status")
    @ApiOperation(value = "Update the status of a customer request")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> requestBody) {

        String status = requestBody.get("status");

        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Status value is required");
        }

        try {
            customerRequestService.updateStatus(requestId, Status.valueOf(status.toUpperCase()));
            return ResponseEntity.ok("Status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }

    // EXAMPLE URL
    /*
     * http://localhost:9090/api/customer/filter-customer-request?serviceType=
     * YOUR_SERVICE_TYPE&gender=
     * YOUR_GENDER&ageRangeStart=MIN_AGE&ageRangeEnd=MAX_AGE&area=YOUR_AREA&pincode=
     * YOUR_PINCODE&locality=YOUR_LOCALITY&apartmentName=YOUR_APARTMENT_NAME
     * 
     */

    // --------------------------API's FOR CUSTOMER CONCERN ENTITY-------------------------------
    // API to get all customer concerns with pagination
    @GetMapping("/get-all-customer-concerns")
    @ApiOperation(value = "Retrieve all customer concerns", response = List.class)
    public ResponseEntity<?> getAllConcerns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<CustomerConcernDTO> concerns = customerConcernService.getAllConcerns(page, size);
            if (concerns.isEmpty() && page > 0) {
                return getAllConcerns(0, size);
            }
            return ResponseEntity.ok(concerns);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer concerns: " + e.getMessage());
        }
    }

    // API to get a concern by ID
    @GetMapping("/get-customer-concern-by-id/{id}")
    @ApiOperation(value = "Retrieve a customer concern by ID", response = CustomerConcernDTO.class)
    public ResponseEntity<?> getConcernById(@PathVariable Long id) {
        try {
            CustomerConcernDTO concernDTO = customerConcernService.getConcernById(id);
            return ResponseEntity.ok(concernDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve concern: " + e.getMessage());
        }
    }

    // API to add a new customer concern
    @PostMapping("/add-customer-concern")
    @ApiOperation(value = "Add a new customer concern", response = String.class)
    public ResponseEntity<?> addNewConcern(@RequestBody CustomerConcernDTO customerConcernDTO) {
        try {
            customerConcernService.addNewConcern(customerConcernDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add customer concern: " + e.getMessage());
        }
    }

    // API to update an existing customer concern
    @PutMapping("/modify-customer-concern/{id}")
    @ApiOperation(value = "Update an existing customer concern")
    public ResponseEntity<?> modifyConcern(@PathVariable Long id,
            @RequestBody CustomerConcernDTO customerConcernDTO) {
        try {
            customerConcernDTO.setId(id);
            customerConcernService.modifyConcern(customerConcernDTO);
            return ResponseEntity.ok(CustomerConstants.UPDATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update customer concern: " + e.getMessage());
        }
    }

    // API to delete a customer concern by ID
    @DeleteMapping("/delete-customer-concern/{id}")
    @ApiOperation(value = "Delete a customer concern by ID", response = String.class)
    public ResponseEntity<?> deleteConcern(@PathVariable Long id) {
        try {
            customerConcernService.deleteConcern(id);
            return ResponseEntity.ok(CustomerConstants.DELETED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete customer concern: " + e.getMessage());
        }
    }

    // --------------------------API's FOR CUSTOMER FEEDBACK ENTITY--------------------------------
    // API to get all customer feedback with pagination
    @GetMapping("/get-all-feedback")
    @ApiOperation(value = "Retrieve all customer feedback", response = List.class)
    public ResponseEntity<?> getAllFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<CustomerFeedbackDTO> feedbackList = customerFeedbackService.getAllFeedback(page, size);
            if (feedbackList.isEmpty() && page > 0) {
                return getAllFeedback(0, size);
            }
            return ResponseEntity.ok(feedbackList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer feedback: " + e.getMessage());
        }
    }

    // API to get feedback by ID
    @GetMapping("/get-feedback-by-id/{id}")
    @ApiOperation(value = "Retrieve customer feedback by ID", response = CustomerFeedbackDTO.class)
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        try {
            CustomerFeedbackDTO feedbackDTO = customerFeedbackService.getFeedbackById(id);
            return ResponseEntity.ok(feedbackDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve feedback: " + e.getMessage());
        }
    }

    // API to add new customer feedback
    @PostMapping("/add-feedback")
    @ApiOperation(value = "Add a new customer feedback", response = String.class)
    public ResponseEntity<?> addFeedback(@RequestBody CustomerFeedbackDTO customerFeedbackDTO) {
        try {
            customerFeedbackService.addFeedback(customerFeedbackDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add customer feedback: " + e.getMessage());
        }
    }

    // API to delete customer feedback by ID
    @DeleteMapping("/delete-feedback/{id}")
    @ApiOperation(value = "Delete customer feedback by ID", response = String.class)
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            customerFeedbackService.deleteFeedback(id);
            return ResponseEntity.ok(CustomerConstants.DELETED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete customer feedback: " + e.getMessage());
        }
    }

    // -----------------API's FOR CUSTOMER REQUEST COMMENT ENTITY---------------------------------
    // API to get all customer request comments
    @GetMapping("/get-all-cr-comments")
    @ApiOperation(value = "Retrieve all customer request comments", response = List.class)
    public ResponseEntity<?> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<CustomerRequestCommentDTO> commentsList = customerRequestCommentService.getAllComments(page, size);
            if (commentsList.isEmpty() && page > 0) {
                return getAllComments(0, size);
            }
            return ResponseEntity.ok(commentsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer request comments: " + e.getMessage());
        }
    }

    // API to get comment by ID
    @GetMapping("/get-cr-comment-by-id/{id}")
    @ApiOperation(value = "Retrieve customer request comment by ID", response = CustomerRequestCommentDTO.class)
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            CustomerRequestCommentDTO commentDTO = customerRequestCommentService.getCommentById(id);
            return ResponseEntity.ok(commentDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer request comment: " + e.getMessage());
        }
    }

    // API to add a new customer request comment
    @PostMapping("/add-cr-comment")
    @ApiOperation(value = "Add a new customer request comment", response = String.class)
    public ResponseEntity<?> addComment(@RequestBody CustomerRequestCommentDTO customerRequestCommentDTO) {
        try {
            customerRequestCommentService.addComment(customerRequestCommentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add customer request comment: " + e.getMessage());
        }
    }

    // API to update a comment by ID
    @PutMapping("/update-cr-comment/{id}")
    @ApiOperation(value = "Update a comment by ID", response = String.class)
    public ResponseEntity<?> updateComment(@PathVariable Long id,
            @RequestBody CustomerRequestCommentDTO customerRequestCommentDTO) {
        try {
            String response = customerRequestCommentService.updateComment(id, customerRequestCommentDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update customer request comment: " + e.getMessage());
        }
    }

    // API to delete customer request comment by ID
    @DeleteMapping("/delete-cr-comment/{id}")
    @ApiOperation(value = "Delete customer request comment by ID", response = String.class)
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            customerRequestCommentService.deleteComment(id);
            return ResponseEntity.ok(CustomerConstants.DELETED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete customer request comment: " + e.getMessage());
        }
    }

    // --------------------------------API's FOR KYC ENTITY-----------------------------------------

    // API to get all KYC records with pagination
    @GetMapping("/get-all-kyc")
    @ApiOperation(value = "Retrieve all KYC records", response = List.class)
    public ResponseEntity<?> getAllKYC(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<KYCDTO> kycs = kycService.getAllKYC(page, size);
            if (kycs.isEmpty() && page > 0) {
                return getAllKYC(0, size);
            }
            return ResponseEntity.ok(kycs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve KYC records: " + e.getMessage());
        }
    }

    // API to get KYC by ID
    @GetMapping("/get-kyc-by-id/{id}")
    @ApiOperation(value = "Get KYC record by ID", response = KYCDTO.class)
    public ResponseEntity<?> getKYCbyId(
            @ApiParam(value = "ID of the KYC record to retrieve", required = true) @PathVariable Long id) {
        try {
            KYCDTO kycDTO = kycService.getKYCById(id);
            if (kycDTO != null) {
                return ResponseEntity.ok(kycDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("KYC record not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve KYC record: " + e.getMessage());
        }
    }

    // API to add a new KYC record
    @PostMapping("/add-kyc")
    @ApiOperation(value = "Add a new KYC record")
    public ResponseEntity<?> addKYC(
            @ApiParam(value = "KYC data to add", required = true) @RequestBody KYCDTO kycDTO) {
        try {
            String response = kycService.addKYC(kycDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add KYC record: " + e.getMessage());
        }
    }

    // API to update KYC
    @PutMapping("/update-kyc/{id}")
    @ApiOperation(value = "Update an existing KYC")
    public ResponseEntity<?> updateKYC(
            @ApiParam(value = "ID of the KYC to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated KYC data", required = true) @RequestBody KYCDTO kycDTO) {
        try {
            kycDTO.setKyc_id(id);
            String result = kycService.updateKYC(kycDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update KYC record: " + e.getMessage());
        }
    }

    // ----------------------------API's FOR KYC COMMENTS ENTITY--------------------------------------

    // API to get all KYC comments with pagination
    @GetMapping("/get-all-kyc-comments")
    @ApiOperation(value = "Retrieve all KYC comments", response = List.class)
    public ResponseEntity<?> getAllKycComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<KYCCommentsDTO> commentsList = kycCommentsService.getAllKycComments(page, size);
            if (commentsList.isEmpty() && page > 0) {
                return getAllKycComments(0, size);
            }
            return ResponseEntity.ok(commentsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve KYC comments: " + e.getMessage());
        }
    }

    // API to get a KYC comment by ID
    @GetMapping("/get-kyc-comment-by-id/{id}")
    @ApiOperation(value = "Retrieve KYC comment by ID", response = KYCCommentsDTO.class)
    public ResponseEntity<?> getKycCommentById(@PathVariable Long id) {
        try {
            KYCCommentsDTO commentDTO = kycCommentsService.getKycCommentById(id);
            if (commentDTO != null) {
                return ResponseEntity.ok(commentDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("KYC comment not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve KYC comment: " + e.getMessage());
        }
    }

    // API to add a new KYC comment
    @PostMapping("/add-kyc-comment")
    @ApiOperation(value = "Add a new KYC comment", response = String.class)
    public ResponseEntity<?> addKycComment(@RequestBody KYCCommentsDTO commentDTO) {
        try {
            kycCommentsService.addKycComment(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add KYC comment: " + e.getMessage());
        }
    }

    // API to update a KYC comment by ID
    @PutMapping("/update-kyc-comment/{id}")
    @ApiOperation(value = "Update a KYC comment by ID", response = String.class)
    public ResponseEntity<?> updateKycComment(@PathVariable Long id, @RequestBody KYCCommentsDTO commentDTO) {
        try {
            String response = kycCommentsService.updateKycComment(id, commentDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update KYC comment: " + e.getMessage());
        }
    }

    // API to delete a KYC comment by ID
    @DeleteMapping("/delete-kyc-comment/{id}")
    @ApiOperation(value = "Delete a KYC comment by ID", response = String.class)
    public ResponseEntity<?> deleteKycComment(@PathVariable Long id) {
        try {
            kycCommentsService.deleteKycComment(id);
            return ResponseEntity.ok(CustomerConstants.DELETED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete KYC comment: " + e.getMessage());
        }
    }

    // ----------------------API's FOR CUSTOMER Holidays---------------------------

    // API to get all customer holidays with pagination
    @GetMapping("/get-all-customer-holidays")
    @ApiOperation(value = "Retrieve all customer holidays", response = List.class)
    public ResponseEntity<?> getAllHolidays(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<CustomerHolidaysDTO> holidays = customerHolidaysService.getAllHolidays(page, size);
            if (holidays.isEmpty() && page > 0) {
                return getAllHolidays(0, size);
            }
            return ResponseEntity.ok(holidays);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve customer holidays: " + e.getMessage());
        }
    }

    // API to get a customer holiday by ID
    @GetMapping("/get-customer-holiday-by-id/{id}")
    @ApiOperation(value = "Retrieve a customer holiday by ID", response = CustomerHolidaysDTO.class)
    public ResponseEntity<?> getHolidayById(@PathVariable Long id) {
        try {
            CustomerHolidaysDTO holidayDTO = customerHolidaysService.getHolidayById(id);
            if (holidayDTO != null) {
                return ResponseEntity.ok(holidayDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Holiday not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve holiday: " + e.getMessage());
        }
    }

    // API to add a new customer holiday
    @PostMapping("/add-customer-holiday")
    @ApiOperation(value = "Add a new customer holiday", response = String.class)
    public ResponseEntity<?> addNewHoliday(@RequestBody CustomerHolidaysDTO customerHolidaysDTO) {
        try {
            String result = customerHolidaysService.addNewHoliday(customerHolidaysDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add new holiday: " + e.getMessage());
        }
    }

    // API to update an existing customer holiday
    @PutMapping("/modify-customer-holiday/{id}")
    @ApiOperation(value = "Update an existing customer holiday")
    public ResponseEntity<?> modifyHoliday(@PathVariable Long id,
            @RequestBody CustomerHolidaysDTO customerHolidaysDTO) {
        try {
            customerHolidaysDTO.setId(id);
            customerHolidaysService.modifyHoliday(customerHolidaysDTO);
            return ResponseEntity.ok(CustomerConstants.UPDATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update holiday: " + e.getMessage());
        }
    }

    // API to deactivate a customer holiday by ID
    @PatchMapping("/deactivate-customer-holiday/{id}")
    @ApiOperation(value = "Deactivate a customer holiday by ID", response = String.class)
    public ResponseEntity<?> deactivateHoliday(@PathVariable Long id) {
        try {
            String response = customerHolidaysService.deactivateHoliday(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to deactivate holiday: " + e.getMessage());
        }
    }

}
