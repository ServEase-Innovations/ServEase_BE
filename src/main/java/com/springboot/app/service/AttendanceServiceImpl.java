package com.springboot.app.service;

import com.springboot.app.dto.AttendanceDTO;
import com.springboot.app.entity.Attendance;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.mapper.AttendanceMapper;
import com.springboot.app.repository.AttendanceRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

        private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

        @Autowired
        private AttendanceRepository attendanceRepository;

        @Autowired
        private AttendanceMapper attendanceMapper;

        @Autowired
        private ServiceProviderRepository serviceProviderRepository;

        @Autowired
        private CustomerRepository customerRepository;

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllAttendance(int page, int size) {
                logger.info("Fetching attendance records with pagination - page: {}, size: {}", page, size);

                Page<Attendance> attendancePage = attendanceRepository.findAll(PageRequest.of(page, size));
                logger.debug("Fetched {} attendance record(s) from the database.", attendancePage.getSize());

                return attendancePage.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public AttendanceDTO getAttendanceByAttendenceId(Long id) {
                logger.info("Fetching attendance record with ID: {}", id);

                Attendance attendance = attendanceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attendance record not found with ID: " + id));

                logger.debug("Found attendance record: {}", attendance);
                return attendanceMapper.attendanceToDTO(attendance);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAttendanceByCustomerId(Long customerId) {
                logger.info("Fetching attendance records for customer ID: {}", customerId);

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get("customer").get("id"), customerId);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException("No attendance records found for customer ID: " + customerId);
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAttendanceByServiceProviderId(Long serviceProviderId) {
                logger.info("Fetching attendance records for service provider ID: {}", serviceProviderId);

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get("serviceProvider").get("id"), serviceProviderId);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException(
                                        "No attendance records found for service provider ID: " + serviceProviderId);
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Override
        @Transactional
        public void saveAttendance(AttendanceDTO attendanceDTO) {
                // Retrieve the ServiceProvider and Customer entities using the IDs from DTO
                ServiceProvider serviceProvider = serviceProviderRepository
                                .findById(attendanceDTO.getServiceProviderId())
                                .orElseThrow(() -> new RuntimeException(
                                                "ServiceProvider not found with id: "
                                                                + attendanceDTO.getServiceProviderId()));

                Customer customer = customerRepository.findById(attendanceDTO.getCustomerId())
                                .orElseThrow(
                                                () -> new RuntimeException("Customer not found with id: "
                                                                + attendanceDTO.getCustomerId()));

                // Create a new Attendance entity
                Attendance attendance = new Attendance();

                // Set the fields on the Attendance entity
                attendance.setServiceProvider(serviceProvider);
                attendance.setCustomer(customer); // Ensure customer is not null
                attendance.setAttended(attendanceDTO.isAttended()); // This is where the value is set from the DTO
                attendance.setCustomerAgreed(attendanceDTO.isCustomerAgreed());

                // Save the Attendance entity to the database
                attendanceRepository.save(attendance);

        }

        @Override
        @Transactional
        public void updateAttendance(AttendanceDTO attendanceDTO) {
                logger.info("Updating attendance record with ID: {}", attendanceDTO.getId());

                Attendance existingAttendance = attendanceRepository.findById(attendanceDTO.getId())
                                .orElseThrow(
                                                () -> new RuntimeException("Attendance record not found with ID: "
                                                                + attendanceDTO.getId()));

                // Map updated values from DTO to the existing entity
                Attendance updatedAttendance = attendanceMapper.dtoToAttendance(attendanceDTO);
                updatedAttendance.setId(existingAttendance.getId()); // Preserve the original ID

                attendanceRepository.save(updatedAttendance);
                logger.debug("Attendance record updated: {}", updatedAttendance);
        }

        @Override
        @Transactional
        public void deleteAttendance(Long id) {
                logger.info("Deleting attendance record with ID: {}", id);

                Attendance attendance = attendanceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attendance record not found with ID: " + id));

                attendanceRepository.delete(attendance);
                logger.debug("Attendance record with ID {} deleted", id);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllNotifications() {
                logger.info("Fetching attendance records with conflicts between isAttended and isCustomerAgreed");

                // Specification for conflicting conditions
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                                criteriaBuilder.and(
                                                criteriaBuilder.equal(root.get("isAttended"), true),
                                                criteriaBuilder.equal(root.get("isCustomerAgreed"), false)),
                                criteriaBuilder.and(
                                                criteriaBuilder.equal(root.get("isAttended"), false),
                                                criteriaBuilder.equal(root.get("isCustomerAgreed"), true)));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException(
                                        "No attendance records found with conflicts between isAttended and isCustomerAgreed");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Conflict for today
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTodayConflicts() {
                logger.info("Fetching attendance records with conflicts for today");

                LocalDate today = LocalDate.now();
                LocalDate startOfDay = today.atStartOfDay().toLocalDate();
                LocalDate endOfDay = today.plusDays(1).atStartOfDay().toLocalDate();

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
                                criteriaBuilder.between(root.get("attendanceStatus"), startOfDay, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException("No attendance records found with conflicts for today");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Conflict for one week
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneWeekConflicts() {
                logger.info("Fetching attendance records with conflicts for the past one week");

                LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
                LocalDate today = LocalDate.now();

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
                                criteriaBuilder.between(root.get("attendanceStatus"), oneWeekAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException("No attendance records found with conflicts for the past one week");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Conflict for two weeks
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTwoWeeksConflicts() {
                logger.info("Fetching attendance records with conflicts for the past two weeks");

                LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
                LocalDate today = LocalDate.now();

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
                                criteriaBuilder.between(root.get("attendanceStatus"), twoWeeksAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException("No attendance records found with conflicts for the past two weeks");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Conflict for one month
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneMonthConflicts() {
                logger.info("Fetching attendance records with conflicts for the past one month");

                LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
                LocalDate today = LocalDate.now();

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
                                criteriaBuilder.between(root.get("attendanceStatus"), oneMonthAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new RuntimeException("No attendance records found with conflicts for the past one month");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

}
