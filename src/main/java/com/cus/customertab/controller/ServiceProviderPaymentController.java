package com.cus.customertab.controller;

import com.cus.customertab.dto.ServiceProviderPaymentDTO;
import com.cus.customertab.service.ServiceProviderPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.cus.customertab.constants.ServiceProviderConstants;

@RestController
@RequestMapping(ServiceProviderConstants.BASE_API_PATH)
@Api(value = ServiceProviderConstants.API_VALUE, tags = ServiceProviderConstants.TAG_SERVICEPROVIDERS)
public class ServiceProviderPaymentController {

    @Autowired
    private ServiceProviderPaymentService serviceProviderPaymentService;

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    // API to get all service provider payments with pagination
    @GetMapping("/get-all-sp-payments")
    @ApiOperation(value = "Retrieve all service provider payments", response = List.class)
    public ResponseEntity<List<ServiceProviderPaymentDTO>> getAllServiceProviderPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
                if (size == null) {
            size = defaultPageSize;
        }
        List<ServiceProviderPaymentDTO> payments = serviceProviderPaymentService.getAllServiceProviderPayments(page,
                size);
        return ResponseEntity.ok(payments);
    }

    // API to get service provider payment by ID
    @GetMapping("/get-sp-payment-by-id/{id}")
    @ApiOperation(value = "Retrieve service provider payment by ID", response = ServiceProviderPaymentDTO.class)
    public ResponseEntity<ServiceProviderPaymentDTO> getServiceProviderPaymentById(
            @ApiParam(value = "ID of the service provider payment to retrieve", required = true) @PathVariable Long id) {
        ServiceProviderPaymentDTO paymentDTO = serviceProviderPaymentService.getServiceProviderPaymentById(id);
        return ResponseEntity.ok(paymentDTO);
    }

    // API to add a service provider payment
    @PostMapping("/add-sp-payment")
    @ApiOperation(value = "Add a new service provider payment")
    public ResponseEntity<String> addServiceProviderPayment(
            @ApiParam(value = "Service provider payment data to add", required = true) @RequestBody ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        String result = serviceProviderPaymentService.addServiceProviderPayment(serviceProviderPaymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // API to update a service provider payment
    @PutMapping("/update-sp-payment/{id}")
    @ApiOperation(value = "Update an existing service provider payment")
    public ResponseEntity<String> updateServiceProviderPayment(
            @ApiParam(value = "ID of the service provider payment to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider payment object", required = true) @RequestBody ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        serviceProviderPaymentDTO.setId(id);
        String result = serviceProviderPaymentService.updateServiceProviderPayment(serviceProviderPaymentDTO);
        return ResponseEntity.ok(result);
    }

    // API to delete a service provider payment
    @DeleteMapping("/delete-sp-payment/{id}")
    @ApiOperation(value = "Delete a service provider payment")
    public ResponseEntity<String> deleteServiceProviderPayment(
            @ApiParam(value = "ID of the service provider payment to delete", required = true) @PathVariable Long id) {
        String result = serviceProviderPaymentService.deleteServiceProviderPayment(id);
        return ResponseEntity.ok(result);
    }
}
