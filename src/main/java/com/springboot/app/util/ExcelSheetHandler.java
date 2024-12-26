package com.springboot.app.util;

import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.HousekeepingRole;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelSheetHandler {

    // Create logger instance
    private static final Logger logger = LoggerFactory.getLogger(ExcelSheetHandler.class);

    public List<ServiceProvider> readExcelFile(String CSV_FILE_LOCATION) {
        List<ServiceProvider> serviceProviders = new ArrayList<>();
        Workbook workbook = null;

        CSV_FILE_LOCATION = "C:\\Users\\shaiq\\Downloads\\excelHandler\\excelHandler\\Service Staff Directory sample.xlsx";

        try {
            workbook = WorkbookFactory.create(new File(CSV_FILE_LOCATION));
            logger.info("Workbook loaded successfully from location: {}", CSV_FILE_LOCATION);

            // Iterate through each sheet
            workbook.forEach(sheet -> {
                logger.info("Processing sheet: {}", sheet.getSheetName());

                // Skip header row and process each subsequent row
                for (int index = 1; index < sheet.getPhysicalNumberOfRows(); index++) {
                    Row row = sheet.getRow(index);
                    if (row != null) {
                        try {
                            ServiceProvider serviceProvider = new ServiceProvider();
                            DataFormatter dataFormatter = new DataFormatter();

                            // Set First Name and Last Name from Name column (Index 1)
                            if (row.getCell(1) != null) {
                                String name = row.getCell(1).getStringCellValue().trim();
                                String[] nameParts = name.split(" ", 2);
                                serviceProvider.setFirstName(nameParts[0]);
                                if (nameParts.length > 1) {
                                    serviceProvider.setLastName(nameParts[1]);
                                } else {
                                    serviceProvider.setLastName("");
                                }
                                logger.debug("Set name: {} {}", serviceProvider.getFirstName(),
                                        serviceProvider.getLastName());
                            }

                            // Set Mobile Number (Index 2)
                            if (row.getCell(2) != null) {
                                String mobileStr = dataFormatter.formatCellValue(row.getCell(2));
                                if (!mobileStr.isEmpty()) {
                                    serviceProvider.setMobileNo(Long.parseLong(mobileStr));
                                    logger.debug("Set mobile number: {}", serviceProvider.getMobileNo());
                                }
                            }

                            // Set Housekeeping Role (Index 3)
                            if (row.getCell(3) != null) {
                                serviceProvider.setHousekeepingRole(
                                        HousekeepingRole.valueOf(row.getCell(3).getStringCellValue()));
                                logger.debug("Set housekeeping role: {}", serviceProvider.getHousekeepingRole());
                            }

                            // Set Gender (Index 4)
                            if (row.getCell(4) != null) {
                                serviceProvider.setGender(Gender.valueOf(row.getCell(4).getStringCellValue()));
                                logger.debug("Set gender: {}", serviceProvider.getGender());
                            }

                            // Set Default or Mock Data for other fields
                            serviceProvider.setEmailId("test" + index + "@gmail.com");
                            serviceProvider.setBuildingName("Building " + index);
                            serviceProvider.setLocality("Locality " + index);
                            serviceProvider.setStreet("Street " + index);
                            serviceProvider.setPincode(560000 + index % 101);
                            serviceProvider.setCurrentLocation("Location " + index);

                            // Automatically set enrolledDate
                            serviceProvider.setEnrolledDate(Timestamp.from(Instant.now()));
                            logger.debug("Set enrolled date: {}", serviceProvider.getEnrolledDate());

                            // Add to the list
                            serviceProviders.add(serviceProvider);
                            logger.info("ServiceProvider added: {}", serviceProvider);

                        } catch (Exception e) {
                            logger.error("Error processing row {}: {}", index, e.getMessage(), e);
                        }
                    }
                }
            });

        } catch (IOException e) {
            logger.error("Error reading the workbook from location: {}", CSV_FILE_LOCATION, e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                    logger.info("Workbook closed successfully.");
                }
            } catch (IOException e) {
                logger.error("Error closing the workbook", e);
            }
        }

        // Return the list of ServiceProvider objects
        logger.info("Total number of Service Providers processed: {}", serviceProviders.size());
        return serviceProviders;
    }
}