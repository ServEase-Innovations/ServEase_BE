package com.springboot.app.controller;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.dto.ServiceProviderPaymentDTO;
import com.springboot.app.service.ServiceProviderPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.springboot.app.Excel.ExcelExportUtil;
import com.springboot.app.constant.ServiceProviderConstants;

@RestController
@RequestMapping(ServiceProviderConstants.BASE_API_PATH)
@Api(value = ServiceProviderConstants.API_VALUE, tags = ServiceProviderConstants.TAG_SERVICEPROVIDERS)
public class ServiceProviderPaymentController {

    private final ServiceProviderPaymentService serviceProviderPaymentService;

    // Constructor injection
    @Autowired
    public ServiceProviderPaymentController(ServiceProviderPaymentService serviceProviderPaymentService) {
        this.serviceProviderPaymentService = serviceProviderPaymentService;
    }

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    // API to get all service provider payments with pagination
    @GetMapping("/payments/all")
    @ApiOperation(value = "Retrieve all service provider payments", response = List.class)
    public ResponseEntity<?> getAllServiceProviderPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null) {
                size = defaultPageSize;
            }
            List<ServiceProviderPaymentDTO> payments = serviceProviderPaymentService.getAllServiceProviderPayments(page,
                    size);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve service provider payments: " + e.getMessage());
        }
    }

    // API to get service provider payment by ID
    @GetMapping("/get/payment/{id}")
    @ApiOperation(value = "Retrieve service provider payment by ID", response = ServiceProviderPaymentDTO.class)
    public ResponseEntity<?> getServiceProviderPaymentById(
            @ApiParam(value = "ID of the service provider payment to retrieve", required = true) @PathVariable Long id) {
        try {
            ServiceProviderPaymentDTO paymentDTO = serviceProviderPaymentService.getServiceProviderPaymentById(id);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve service provider payment: " + e.getMessage());
        }
    }

    // API to add a service provider payment
    @PostMapping("/payment/add")
    @ApiOperation(value = "Add a new service provider payment")
    public ResponseEntity<?> addServiceProviderPayment(
            @ApiParam(value = "Service provider payment data to add", required = true) @RequestBody ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        try {
            String result = serviceProviderPaymentService.addServiceProviderPayment(serviceProviderPaymentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add service provider payment: " + e.getMessage());
        }
    }

    // API to update a service provider payment
    @PutMapping("/update/payment/{id}")
    @ApiOperation(value = "Update an existing service provider payment")
    public ResponseEntity<?> updateServiceProviderPayment(
            @ApiParam(value = "ID of the service provider payment to update", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated service provider payment object", required = true) @RequestBody ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        try {
            if (serviceProviderPaymentService.getServiceProviderPaymentById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(" Service provider payment with ID " + id + " not found");
            }
            serviceProviderPaymentDTO.setId(id);
            String result = serviceProviderPaymentService.updateServiceProviderPayment(serviceProviderPaymentDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update service provider payment: " + e.getMessage());
        }
    }

    // API to delete a service provider payment
    @DeleteMapping("/delete/payment/{id}")
    @ApiOperation(value = "Delete a service provider payment")
    public ResponseEntity<?> deleteServiceProviderPayment(
            @ApiParam(value = "ID of the service provider payment to delete", required = true) @PathVariable Long id) {
        try {
            if (serviceProviderPaymentService.getServiceProviderPaymentById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(" Service provider payment with ID " + id + " not found");
            }
            String result = serviceProviderPaymentService.deleteServiceProviderPayment(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete service provider payment: " + e.getMessage());
        }
    }

<<<<<<< HEAD
=======
    @GetMapping("/get/payments/by-date")
    public ResponseEntity<?> getPaymentsByDateRange(@RequestBody Map<String, String> body) {
        try {
            Date startDate = Date.valueOf(body.get("startDate"));
            Date endDate = Date.valueOf(body.get("endDate"));

            List<ServiceProviderPaymentDTO> payments = serviceProviderPaymentService.getPaymentsByDateRange(startDate,
                    endDate);
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No payments found between the provided dates.");
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    @GetMapping("/get/payments/by-month")
    public ResponseEntity<?> getPaymentsByMonth(@RequestBody Map<String, Integer> body) {
        try {
            int month = body.get("month");
            int year = body.get("year");

            List<ServiceProviderPaymentDTO> payments = serviceProviderPaymentService.getPaymentsByMonthAndYear(month, year);
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No payments found for the provided month.");
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    @GetMapping("/get/payments/by-financial-year")
    public ResponseEntity<?> getPaymentsByFinancialYear(@RequestBody Map<String, Integer> body) {
        try {
            int year = body.get("year");

            List<ServiceProviderPaymentDTO> payments = serviceProviderPaymentService.getPaymentsByFinancialYear(year);
            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No payments found for the financial year " + year);
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    


>>>>>>> main
}
