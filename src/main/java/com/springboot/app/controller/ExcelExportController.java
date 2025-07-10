package com.springboot.app.controller;

import com.springboot.app.Excel.ExcelExportUtil;
import com.springboot.app.dto.ServiceProviderPaymentDTO;
import com.springboot.app.service.ServiceProviderPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelExportController {

    @Autowired
    private ServiceProviderPaymentService serviceProviderPaymentService;

    // API to export service provider payments to Excel
    @GetMapping("/export/payments")
    public ResponseEntity<?> exportAllPaymentsToExcel() {
        try {
            List<ServiceProviderPaymentDTO> servicePayments = serviceProviderPaymentService.getAllServiceProviderPayments(0, Integer.MAX_VALUE);

            InputStream excelFile = ExcelExportUtil.exportPaymentsToExcel(servicePayments);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=payments.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(excelFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export data to Excel: " + e.getMessage());
        }
    }
}