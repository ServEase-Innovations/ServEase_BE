package com.cus.customertab.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerConcernDTO;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.dto.KYCCommentsDTO;
import com.cus.customertab.dto.KYCDTO; 
import com.cus.customertab.enums.Gender;
import com.cus.customertab.enums.ServiceType;
import com.cus.customertab.dto.CustomerFeedbackDTO;
import com.cus.customertab.dto.CustomerRequestCommentDTO;
import com.cus.customertab.service.CustomerConcernService;
import com.cus.customertab.service.CustomerFeedbackService;
import com.cus.customertab.service.CustomerRequestCommentService;
import com.cus.customertab.service.CustomerRequestService;
import com.cus.customertab.service.CustomerService;
 import com.cus.customertab.service.KYCCommentsService;
import com.cus.customertab.service.KYCService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.*;

@RestController
@RequestMapping("/api/customer")
@Api(value = "Customer operations API", tags = "Customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private CustomerConcernService customerConcernService;

    @Autowired
    private CustomerFeedbackService customerFeedbackService;

    @Autowired
    private CustomerRequestCommentService customerRequestCommentService;

    @Autowired
    private KYCService kycService;

    @Autowired
    private KYCCommentsService kycCommentsService;

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    //--------------------------API's FOR CUSTOMER ENTITY----------------------------------------
    //API to get all customers
    // API to get all customers with pagination
    @GetMapping("/get-all-customers")
    @ApiOperation(value = CustomerConstants.RETRIEVE_ALL_DESC, response = List.class)
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        if (size == null) {
            size = defaultPageSize;
        }
        List<CustomerDTO> customers = customerService.getAllCustomers(page, size);
        if (customers.isEmpty() && page > 0) {
            return getAllCustomers(0, size);
        }
        return ResponseEntity.ok(customers);
    }
    // @GetMapping("/get-all-customers")
    // @ApiOperation(value = CustomerConstants.RETRIEVE_ALL_DESC, response = List.class)
    // public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
    //     List<CustomerDTO> customers = customerService.getAllCustomers();
    //     return ResponseEntity.ok(customers);
    // }

    //API to get customer by id
    @GetMapping("/get-customer-by-id/{id}")
    @ApiOperation(value = CustomerConstants.GET_BY_ID_DESC, response = CustomerDTO.class)
    public ResponseEntity<CustomerDTO> getCustomerById(
            @ApiParam(value = "ID of the customer to retrieve", required = true) @PathVariable Long id) {
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerDTO);
    }

    // API to add a customer
    @PostMapping("/add-customer")
    @ApiOperation(value = CustomerConstants.ADD_NEW_DESC)
    public ResponseEntity<String> addCustomer(
            @ApiParam(value = "Customer data to add", required = true) @ModelAttribute CustomerDTO customerDTO,
            @ApiParam(value = "Profile picture of the customer") @RequestParam(value = "profilePic", required = false) MultipartFile profilePic)
            throws IOException {
        customerDTO.setProfilePic(profilePic);
        customerService.saveCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to update a customer
    @PutMapping("/update-customer/{id}")
    @ApiOperation(value = CustomerConstants.UPDATE_DESC)
    public ResponseEntity<String> updateCustomer(
            @ApiParam(value = "ID of the customer to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated customer object", required = true) @RequestBody CustomerDTO customerDTO) {
        customerDTO.setCustomerId(id);
        customerService.updateCustomer(customerDTO);
        return ResponseEntity.ok(CustomerConstants.UPDATED);
    }

    // API to delete a customer
    @PatchMapping("/delete-customer/{id}")
    @ApiOperation(value = CustomerConstants.DELETE_DESC)
    public ResponseEntity<String> deleteCustomer(
            @ApiParam(value = "ID of the customer to delete", required = true) @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(CustomerConstants.DELETED);
    }

    //----------------------API's FOR CUSTOMER REQUEST ENTITY---------------------------------
    // API to get all customer requests
    @GetMapping("/get-all-customer-requests")
    @ApiOperation(value = "Retrieve all customer requests", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllCustomerRequests() {
        List<CustomerRequestDTO> requests = customerRequestService.getAll();
        return ResponseEntity.ok(requests);
    }

    // API to get customer request by ID
    @GetMapping("/get-customer-request-by-id/{requestId}")
    @ApiOperation(value = "Get customer request by ID", response = CustomerRequestDTO.class)
    public ResponseEntity<CustomerRequestDTO> getCustomerRequestById(@PathVariable Long requestId) {
        CustomerRequestDTO requestDTO = customerRequestService.getByRequestId(requestId);
        return ResponseEntity.ok(requestDTO);
    }

    // API to get all open requests
    @GetMapping("/get-open-requests")
    @ApiOperation(value = "Retrieve all open requests", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllOpenRequests() {
        List<CustomerRequestDTO> openRequests = customerRequestService.getAllOpenRequests();
        return ResponseEntity.ok(openRequests);
    }

    // API to get all potential customers
    @GetMapping("/get-potential-customers")
    @ApiOperation(value = "Retrieve all potential customers", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllPotentialCustomers() {
        List<CustomerRequestDTO> potentialCustomers = customerRequestService.findAllPotentialCustomers();
        return ResponseEntity.ok(potentialCustomers);
    }

    // API to add a customer request
    @PostMapping("/add-customer-request")
    @ApiOperation(value = "Add a new customer request")
    public ResponseEntity<String> insertCustomerRequest(@RequestBody CustomerRequestDTO customerRequestDTO) {
        customerRequestService.insert(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to update customer request
    @PutMapping("/update-customer-request/{requestId}")
    @ApiOperation(value = "Update an existing customer request")
    public ResponseEntity<String> updateCustomerRequest(@PathVariable Long requestId,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        customerRequestDTO.setRequestId(requestId);
        customerRequestService.update(customerRequestDTO);
        return ResponseEntity.ok(CustomerConstants.UPDATED);
    }

    // FILTER API
    @GetMapping("/filter-customer-request")
    public List<CustomerRequestDTO> getRequestFilters(
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer pincode,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String apartment_name) {

        return customerRequestService.getRequestFilters(serviceType, gender, area, pincode, locality,
                apartment_name);
    }
    
    //EXAMPLE URL
    /*
     * http://localhost:9090/api/customer/filter-customer-request?serviceType=YOUR_SERVICE_TYPE&gender=
     * YOUR_GENDER&ageRangeStart=MIN_AGE&ageRangeEnd=MAX_AGE&area=YOUR_AREA&pincode=
     * YOUR_PINCODE&locality=YOUR_LOCALITY&apartmentName=YOUR_APARTMENT_NAME
     * 
     */


    //--------------------------API's FOR CUSTOMER CONCERN ENTITY-------------------------------
    // API to get all customer concerns
    @GetMapping("/get-all-customer-concerns")
    @ApiOperation(value = "Retrieve all customer concerns", response = List.class)
    public ResponseEntity<List<CustomerConcernDTO>> getAllConcerns() {
        List<CustomerConcernDTO> concerns = customerConcernService.getAllConcerns();
        return ResponseEntity.ok(concerns);
    }

    // API to get a concern by ID
    @GetMapping("/get-customer-concern-by-id/{id}")
    @ApiOperation(value = "Retrieve a customer concern by ID", response = CustomerConcernDTO.class)
    public ResponseEntity<CustomerConcernDTO> getConcernById(@PathVariable Long id) {
        CustomerConcernDTO concernDTO = customerConcernService.getConcernById(id);
        return ResponseEntity.ok(concernDTO);
    }

    // API to add a new customer concern
    @PostMapping("/add-customer-concern")
    @ApiOperation(value = "Add a new customer concern", response = String.class)
    public ResponseEntity<String> addNewConcern(@RequestBody CustomerConcernDTO customerConcernDTO) {
        customerConcernService.addNewConcern(customerConcernDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to update an existing customer concern
    @PutMapping("/modify-customer-concern/{id}")
    @ApiOperation(value = "Update an existing customer concern")
    public ResponseEntity<String> modifyConcern(@PathVariable Long id,
            @RequestBody CustomerConcernDTO customerConcernDTO) {
        customerConcernDTO.setId(id);
        customerConcernService.modifyConcern(customerConcernDTO);
        return ResponseEntity.ok(CustomerConstants.UPDATED);
    }

    // API to delete a customer concern by ID
    @DeleteMapping("/delete-customer-concern/{id}")
    @ApiOperation(value = "Delete a customer concern by ID", response = String.class)
    public ResponseEntity<String> deleteConcern(@PathVariable Long id) {
        customerConcernService.deleteConcern(id);
        return ResponseEntity.ok(CustomerConstants.DELETED);
    }

    // --------------------------API's FOR CUSTOMER FEEDBACK ENTITY--------------------------------
    // API to get all customer feedback
    @GetMapping("/get-all-feedback")
    @ApiOperation(value = "Retrieve all customer feedback", response = List.class)
    public ResponseEntity<List<CustomerFeedbackDTO>> getAllFeedback() {
        List<CustomerFeedbackDTO> feedbackList = customerFeedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    // API to get feedback by ID
    @GetMapping("/get-feedback-by-id/{id}")
    @ApiOperation(value = "Retrieve customer feedback by ID", response = CustomerFeedbackDTO.class)
    public ResponseEntity<CustomerFeedbackDTO> getFeedbackById(@PathVariable Long id) {
        CustomerFeedbackDTO feedbackDTO = customerFeedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedbackDTO);
    }

    // API to add new customer feedback
    @PostMapping("/add-feedback")
    @ApiOperation(value = "Add a new customer feedback", response = String.class)
    public ResponseEntity<String> addFeedback(@RequestBody CustomerFeedbackDTO customerFeedbackDTO) {
        customerFeedbackService.addFeedback(customerFeedbackDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to delete customer feedback by ID
    @DeleteMapping("/delete-feedback/{id}")
    @ApiOperation(value = "Delete customer feedback by ID", response = String.class)
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        customerFeedbackService.deleteFeedback(id);
        return ResponseEntity.ok(CustomerConstants.DELETED);
    }

    //-----------------API's FOR CUSTOMER REQUEST COMMENT ENTITY---------------------------------
    // API to get all customer request comments
    @GetMapping("/get-all-comments")
    @ApiOperation(value = "Retrieve all customer request comments", response = List.class)
    public ResponseEntity<List<CustomerRequestCommentDTO>> getAllComments() {
        List<CustomerRequestCommentDTO> commentsList = customerRequestCommentService.getAllComments();
        return ResponseEntity.ok(commentsList);
    }

    // API to get comment by ID
    @GetMapping("/get-comment-by-id/{id}")
    @ApiOperation(value = "Retrieve customer request comment by ID", response = CustomerRequestCommentDTO.class)
    public ResponseEntity<CustomerRequestCommentDTO> getCommentById(@PathVariable Long id) {
        CustomerRequestCommentDTO commentDTO = customerRequestCommentService.getCommentById(id);
        return ResponseEntity.ok(commentDTO);
    }

    // API to add a new customer request comment
    @PostMapping("/add-comment")
    @ApiOperation(value = "Add a new customer request comment", response = String.class)
    public ResponseEntity<String> addComment(@RequestBody CustomerRequestCommentDTO customerRequestCommentDTO) {
        customerRequestCommentService.addComment(customerRequestCommentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to update a comment by ID
    @PutMapping("/update-comment/{id}")
    @ApiOperation(value = "Update a comment by ID", response = String.class)
    public ResponseEntity<String> updateComment(@PathVariable Long id,
            @RequestBody CustomerRequestCommentDTO customerRequestCommentDTO) {
        String response = customerRequestCommentService.updateComment(id, customerRequestCommentDTO);
        return ResponseEntity.ok(response);
    }

    // API to delete customer request comment by ID
    @DeleteMapping("/delete-comment/{id}")
    @ApiOperation(value = "Delete customer request comment by ID", response = String.class)
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        customerRequestCommentService.deleteComment(id);
        return ResponseEntity.ok(CustomerConstants.DELETED);
    }

    //--------------------------------API's FOR KYC ENTITY-----------------------------------------------

    // API to get all KYC records
    @GetMapping("/get-all-kyc")
    @ApiOperation(value = "Retrieve all KYC records", response = List.class)
    public ResponseEntity<List<KYCDTO>> getAllKYC() {
        List<KYCDTO> kycs = kycService.getAllKYC();
        return ResponseEntity.ok(kycs);
    }

    // API to get KYC by ID
    @GetMapping("/get-kyc-by-id/{id}")
    @ApiOperation(value = "Get KYC record by ID", response = KYCDTO.class)
    public ResponseEntity<KYCDTO> getKYCbyId(
            @ApiParam(value = "ID of the KYC record to retrieve", required = true) @PathVariable Long id) {
        KYCDTO kycDTO = kycService.getKYCById(id);
        if (kycDTO != null) {
            return ResponseEntity.ok(kycDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }
    }

    // API to add a new KYC record
    @PostMapping("/add-kyc")
    @ApiOperation(value = "Add a new KYC record")
    public ResponseEntity<String> addKYC(
            @ApiParam(value = "KYC data to add", required = true) @RequestBody KYCDTO kycDTO) {
        String response = kycService.addKYC(kycDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // API to update KYC
    @PutMapping("/update-kyc/{id}")
    @ApiOperation(value = "Update an existing KYC")
    public ResponseEntity<String> updateKYC(
            @ApiParam(value = "ID of the KYC to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated KYC data", required = true) @RequestBody KYCDTO kycDTO) {
        kycDTO.setKyc_id(id);
        String result = kycService.updateKYC(kycDTO);
        return ResponseEntity.ok(result);
    }

    //----------------------------API's FOR KYC COMMENTS ENTITY--------------------------------------

    //API to get all KYC comments
    @GetMapping("/get-all-kyc-comments")
    @ApiOperation(value = "Retrieve all KYC comments", response = List.class)
    public ResponseEntity<List<KYCCommentsDTO>> getAllKycComments() {
        List<KYCCommentsDTO> commentsList = kycCommentsService.getAllKycComments();
        return ResponseEntity.ok(commentsList);
    }

    // API to get a KYC comment by ID
    @GetMapping("/get-kyc-comment-by-id/{id}")
    @ApiOperation(value = "Retrieve KYC comment by ID", response = KYCCommentsDTO.class)
    public ResponseEntity<KYCCommentsDTO> getKycCommentById(@PathVariable Long id) {
        KYCCommentsDTO commentDTO = kycCommentsService.getKycCommentById(id);
        return ResponseEntity.ok(commentDTO);
    }

    // API to add a new KYC comment
    @PostMapping("/add-kyc-comment")
    @ApiOperation(value = "Add a new KYC comment", response = String.class)
    public ResponseEntity<String> addKycComment(@RequestBody KYCCommentsDTO commentDTO) {
        kycCommentsService.addKycComment(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerConstants.ADDED);
    }

    // API to update a KYC comment by ID
    @PutMapping("/update-kyc-comment/{id}")
    @ApiOperation(value = "Update a KYC comment by ID", response = String.class)
    public ResponseEntity<String> updateKycComment(@PathVariable Long id, @RequestBody KYCCommentsDTO commentDTO) {
        String response = kycCommentsService.updateKycComment(id, commentDTO);
        return ResponseEntity.ok(response);
    }

    // API to delete a KYC comment by ID
    @DeleteMapping("/delete-kyc-comment/{id}")
    @ApiOperation(value = "Delete a KYC comment by ID", response = String.class)
    public ResponseEntity<String> deleteKycComment(@PathVariable Long id) {
        kycCommentsService.deleteKycComment(id);
        return ResponseEntity.ok(CustomerConstants.DELETED);
    }
}