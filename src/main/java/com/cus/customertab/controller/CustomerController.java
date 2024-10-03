package com.cus.customertab.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;
import com.cus.customertab.service.CustomerRequestService;
import com.cus.customertab.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/customer")
@Api(value = "Customer operations API", tags = "Customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRequestService customerRequestService;

    // API to get all customers
    @GetMapping("/get-all-customers")
    @ApiOperation(value = CustomerConstants.RETRIEVE_ALL_DESC, response = List.class)
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    //API to get all customer request
    @GetMapping("/get-all-customer-requests")
    @ApiOperation(value = "Retrieve all Data", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllCustomerRequests() {
        ResponseEntity<List<CustomerRequestDTO>> response = customerRequestService.getAll();
        return response;
    }

    // API to get customer by id
    @GetMapping("/get-customer-by-id/{id}")
    @ApiOperation(value = CustomerConstants.GET_BY_ID_DESC, response = CustomerDTO.class)
    public ResponseEntity<CustomerDTO> getCustomerById(
            @ApiParam(value = "ID of the customer to retrieve", required = true) @PathVariable Long id) {
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return customerDTO != null ? ResponseEntity.ok(customerDTO) : ResponseEntity.notFound().build();
    }

    //API to get customer request by id
    @GetMapping("/get-customer-request-by-id/{requestId}")
    @ApiOperation(value = "Get data by ID", response = CustomerRequest.class)
    public ResponseEntity<CustomerRequestDTO> getCustomerRequestById(@PathVariable Long requestId) {
        ResponseEntity<CustomerRequestDTO> response = customerRequestService.getByRequestId(requestId);
        return response;
    }

    //API to get all open requests
    @GetMapping("/get-open-requests")
    @ApiOperation(value = "Retrieve all open requests", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllOpenRequests() {
        ResponseEntity<List<CustomerRequestDTO>> response = customerRequestService.getAllOpenRequests();
        return response;
    }

    //API to get all potential customers
    @GetMapping("/get-potential-customers")
    @ApiOperation(value = "Retrieve all Potential Customers", response = List.class)
    public ResponseEntity<List<CustomerRequestDTO>> getAllPotentialCustomers() {
        ResponseEntity<List<CustomerRequestDTO>> response = customerRequestService.findAllPotentialCustomers();
        return response;
    }

    // API to add a customer
    @PostMapping("/add-customer")
    @ApiOperation(value = CustomerConstants.ADD_NEW_DESC)
    public ResponseEntity<String> addCustomer(
            @ApiParam(value = "Customer data to add", required = true) @RequestBody CustomerDTO customerDTO) {
        customerService.saveCustomer(customerDTO);
        return ResponseEntity.ok(CustomerConstants.CUSTOMER_ADDED);
    }

    //API to add a customer request
    @PostMapping("/add-customer-request")
    @ApiOperation(value = "Add a new customer request")
    public ResponseEntity<String> insertCustomerRequest(@RequestBody CustomerRequestDTO customerRequestDTO) {
    System.out.println("Received DTO: " + customerRequestDTO);
    ResponseEntity<String> response = customerRequestService.insert(customerRequestDTO);
    return new ResponseEntity<>(response.getBody(), HttpStatus.CREATED);
}

    // API to update a customer
    @PutMapping("/update-customer/{id}")
    @ApiOperation(value = CustomerConstants.UPDATE_DESC)
    public ResponseEntity<String> updateCustomer(
            @ApiParam(value = "ID of the customer to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated customer object", required = true) @RequestBody CustomerDTO customerDTO) {
        customerDTO.setCustomerId(id);
        customerService.updateCustomer(customerDTO);
        return ResponseEntity.ok(CustomerConstants.CUSTOMER_UPDATED);
    }

    //API to update customer request
    @PutMapping("/update-customer-request/{requestId}")
    @ApiOperation(value = "Update an existing customer request")
    public ResponseEntity<String> updateCustomerRequest(@PathVariable Long requestId, @RequestBody CustomerRequestDTO customerRequestDTO) {
        customerRequestDTO.setRequestId(requestId);
        ResponseEntity<String> response = customerRequestService.update(customerRequestDTO);
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK); 
    }

    // API to delete a customer (soft delete)
    @PatchMapping("/delete-customer/{id}")
    @ApiOperation(value = CustomerConstants.DELETE_DESC)
    public ResponseEntity<String> deleteCustomer(
            @ApiParam(value = "ID of the customer to delete", required = true) @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(CustomerConstants.CUSTOMER_DELETED);
    }
}
