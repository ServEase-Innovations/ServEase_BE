package com.springboot.app.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.app.entity.Attendance;
import com.springboot.app.repository.AttendanceRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceConflictScheduler {
        private static final Logger logger = LoggerFactory.getLogger(AttendanceConflictScheduler.class);

        @Autowired
        private final AttendanceRepository attendanceRepository;

        @PersistenceContext
        private EntityManager entityManager;

        @Scheduled(fixedRate = 300000) // Runs every 5 minutes (300,000 ms)
        @Transactional(readOnly = true)
        public void checkAttendanceConflicts() {
                logger.info("Running scheduled check for attendance conflicts...");

                // Get current date-time for comparison (assuming you want to check today's
                // records)
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

                // Specification for conflicting conditions
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.or(
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get("isAttended"), true),
                                                                criteriaBuilder.equal(root.get("isCustomerAgreed"),
                                                                                false)),
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get("isAttended"), false),
                                                                criteriaBuilder.equal(root.get("isCustomerAgreed"),
                                                                                true))),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("attendanceStatus"), startOfDay) // Filter

                );

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        logger.info("No attendance conflicts found.");
                        return;
                }

                logger.warn("Conflicting attendance records found:");
                attendanceList.forEach(attendance -> {
                        // Safely retrieve the ServiceProvider ID
                        Long serviceProviderId = attendance.getServiceProvider() != null
                                        ? attendance.getServiceProvider().getServiceproviderId()
                                        : null;

                        // Safely retrieve the Customer ID
                        Long customerId = attendance.getCustomer() != null
                                        ? attendance.getCustomer().getCustomerId()
                                        : null;

                        // Logging in the desired format with specific IDs
                        logger.warn(String.format(
                                        "Conflict detected - Customer ID: %d, ServiceProvider ID: %d, isAttended: %b, isCustomerAgreed: %b",
                                        customerId != null ? customerId : 0, // Default to 0 if null
                                        serviceProviderId != null ? serviceProviderId : 0, // Default to 0 if null
                                        attendance.isAttended(),
                                        attendance.isCustomerAgreed()));
                });
        }
}
