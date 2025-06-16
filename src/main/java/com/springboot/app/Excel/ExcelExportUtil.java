package com.springboot.app.Excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.springboot.app.dto.ServiceProviderPaymentDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelExportUtil {

    public static InputStream exportPaymentsToExcel(List<ServiceProviderPaymentDTO> servicePayments) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Sheet 1: Service Provider Payments
        Sheet serviceSheet = workbook.createSheet("ServiceProviderPayments");
        writeServiceProviderPayments(serviceSheet, servicePayments);

        // Output the file to stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void writeServiceProviderPayments(Sheet sheet, List<ServiceProviderPaymentDTO> dataList) {
        Row header = sheet.createRow(0);
        String[] columns = { "ID", "Service Provider ID", "Customer ID", "Start Date", "End Date", "Settled On",
                "Payment Mode", "Payment On", "Transaction ID", "No. of Days", "Amount", "Currency",
                "UPI ID", "Month", "Year", "Monthly Amount" };
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (ServiceProviderPaymentDTO dto : dataList) {
            Row row = sheet.createRow(rowNum++);
            int col = 0;

            row.createCell(col++).setCellValue(dto.getId());
            row.createCell(col++).setCellValue(dto.getServiceProviderId());
            row.createCell(col++).setCellValue(dto.getCustomerId());
            row.createCell(col++).setCellValue(dto.getStartDate() != null ? dto.getStartDate().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getEndDate() != null ? dto.getEndDate().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getSettledOn() != null ? dto.getSettledOn().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getPaymentMode() != null ? dto.getPaymentMode().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getPaymentOn() != null ? dto.getPaymentOn().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getTransactionId() != null ? dto.getTransactionId() : "NULL");
            row.createCell(col++).setCellValue(dto.getNoOfDays());
            row.createCell(col++).setCellValue(dto.getAmount());
            row.createCell(col++).setCellValue(dto.getCurrency() != null ? dto.getCurrency().toString() : "NULL");
            row.createCell(col++).setCellValue(dto.getUpiId() != null ? dto.getUpiId() : "NULL");
            row.createCell(col++).setCellValue(dto.getMonth());
            row.createCell(col++).setCellValue(dto.getYear());
            row.createCell(col++).setCellValue(dto.getMonthlyAmount());
        }
        

        
    }

}
