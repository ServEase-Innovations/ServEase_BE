package com.cus.customertab.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cus.customertab.entity.Customer;
import com.cus.customertab.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/customers")
@Api(value = "Customer operations API", tags = "Customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    //API to get all customers
    @GetMapping
    @ApiOperation(value = "Retrieve all customers", response = List.class)
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    //API to get customer by id
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a customer by ID", response = Customer.class)
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }

    //API to add a customer
    @PostMapping
    @ApiOperation(value = "Add a new customer")
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer){
        customerService.saveCustomer(customer);
        return ResponseEntity.ok("Customer added successfully !");
    }

    //API to update 
    @PutMapping("/{id}")
    @ApiOperation(value = "Update an existing customer")
     public ResponseEntity<String> updateCustomer(
        @ApiParam(value = "ID of the customer to update", required = true) @PathVariable Long id,
        @ApiParam(value = "Updated customer object", required = true) @RequestBody Customer customer) {
        customer.setCustomerId(id);
        customerService.updateCustomer(customer);
        return ResponseEntity.ok("Customer updated successfully!");
    }
    
    //API to delete a customer
    @PatchMapping("/{id}")
    @ApiOperation(value = "Delete a customer by ID") // Operation description
    public ResponseEntity<String> deleteCustomer(
        @ApiParam(value = "ID of the customer to delete", required = true) @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully!");
    }
     
}
