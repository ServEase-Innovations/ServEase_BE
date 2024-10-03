package com.cus.customertab.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(CustomerConstants.BASE_API_PATH)
@Api(value = CustomerConstants.API_VALUE, tags = CustomerConstants.TAG_CUSTOMERS)
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // API to get all customers
    @GetMapping("/get-all-customers")
    @ApiOperation(value = CustomerConstants.RETRIEVE_ALL_DESC, response = List.class)
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // API to get customer by id
    @GetMapping("/get-by-id/{id}")
    @ApiOperation(value = CustomerConstants.GET_BY_ID_DESC, response = CustomerDTO.class)
    public ResponseEntity<CustomerDTO> getCustomerById(
            @ApiParam(value = "ID of the customer to retrieve", required = true) @PathVariable Long id) {
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return customerDTO != null ? ResponseEntity.ok(customerDTO) : ResponseEntity.notFound().build();
    }

    // API to add a customer
    @PostMapping("/add-customer")
    @ApiOperation(value = CustomerConstants.ADD_NEW_DESC)
    public ResponseEntity<String> addCustomer(
            @ApiParam(value = "Customer data to add", required = true) @RequestBody CustomerDTO customerDTO) {
        customerService.saveCustomer(customerDTO);
        return ResponseEntity.ok(CustomerConstants.CUSTOMER_ADDED);
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

    // API to delete a customer (soft delete)
    @PatchMapping("/delete-customer/{id}")
    @ApiOperation(value = CustomerConstants.DELETE_DESC)
    public ResponseEntity<String> deleteCustomer(
            @ApiParam(value = "ID of the customer to delete", required = true) @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(CustomerConstants.CUSTOMER_DELETED);
    }
}
