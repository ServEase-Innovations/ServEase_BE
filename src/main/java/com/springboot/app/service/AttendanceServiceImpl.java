package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.AttendanceDTO;
import com.springboot.app.entity.Attendance;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;
import com.springboot.app.enums.TaskStatus;
import com.springboot.app.exception.AttendanceNotFoundException;
import com.springboot.app.mapper.AttendanceMapper;
import com.springboot.app.repository.AttendanceRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.ServiceProviderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

        private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

        private final AttendanceRepository attendanceRepository;
        private final AttendanceMapper attendanceMapper;
        private final ServiceProviderRepository serviceProviderRepository;
        private final CustomerRepository customerRepository;

        public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                        AttendanceMapper attendanceMapper,
                        ServiceProviderRepository serviceProviderRepository,
                        CustomerRepository customerRepository) {
                this.attendanceRepository = attendanceRepository;
                this.attendanceMapper = attendanceMapper;
                this.serviceProviderRepository = serviceProviderRepository;
                this.customerRepository = customerRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllAttendance(int page, int size) {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records with pagination - page: {}, size: {}", page, size);
                }
                Page<Attendance> attendancePage = attendanceRepository.findAll(PageRequest.of(page, size));
                if (logger.isDebugEnabled()) {

                        logger.debug("Fetched {} attendance record(s) from the database.", attendancePage.getSize());
                }
                return attendancePage.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();

        }

        @Override
        @Transactional(readOnly = true)
        public AttendanceDTO getAttendanceByAttendenceId(Long id) {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance record with ID: {}", id);
                }
                Attendance attendance = attendanceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                ServiceProviderConstants.ATTENDANCE_NOT_FOUND_MSG + id));
                if (logger.isDebugEnabled()) {

                        logger.debug("Found attendance record: {}", attendance);
                }
                return attendanceMapper.attendanceToDTO(attendance);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAttendanceByCustomerId(Long customerId) {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records for customer ID: {}", customerId);
                }
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get("customer").get("id"), customerId);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        "No attendance records found for customer ID: " + customerId);
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAttendanceByServiceProviderId(Long serviceProviderId) {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records for service provider ID: {}", serviceProviderId);
                }
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get("serviceProvider").get("id"), serviceProviderId);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
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
                attendance.setResolved(attendanceDTO.isResolved());
                attendance.setDescription(attendanceDTO.getDescription());

                // Default taskStatus if null
                if (attendance.getTaskStatus() == null) {
                        attendance.setTaskStatus(TaskStatus.NOT_STARTED);
                }

                // Save the Attendance entity to the database
                attendanceRepository.save(attendance);

        }

        @Override
        @Transactional
        public void updateAttendance(AttendanceDTO attendanceDTO) {
                if (logger.isInfoEnabled()) {

                        logger.info("Updating attendance record with ID: {}", attendanceDTO.getId());
                }
                Attendance existingAttendance = attendanceRepository.findById(attendanceDTO.getId())
                                .orElseThrow(
                                                () -> new RuntimeException("Attendance record not found with ID: "
                                                                + attendanceDTO.getId()));

                // Map updated values from DTO to the existing entity
                Attendance updatedAttendance = attendanceMapper.dtoToAttendance(attendanceDTO);
                updatedAttendance.setId(existingAttendance.getId()); // Preserve the original ID

                attendanceRepository.save(updatedAttendance);
                if (logger.isDebugEnabled()) {

                        logger.debug("Attendance record updated: {}", updatedAttendance);
                }
        }

        @Override
        @Transactional
        public void deleteAttendance(Long id) {
                if (logger.isInfoEnabled()) {

                        logger.info("Deleting attendance record with ID: {}", id);
                }
                Attendance attendance = attendanceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attendance record not found with ID: " + id));

                attendanceRepository.delete(attendance);
                if (logger.isDebugEnabled()) {

                        logger.debug("Attendance record with ID {} deleted", id);
                }
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllNotifications() {
                logger.info("Fetching attendance records with conflicts between {} and {}",
                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED,
                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);

                // Specification for conflicting conditions using constants
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.or(
                                criteriaBuilder.and(
                                                criteriaBuilder.equal(root.get(
                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                true),
                                                criteriaBuilder.equal(root.get(
                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                false)),
                                criteriaBuilder.and(
                                                criteriaBuilder.equal(root.get(
                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                false),
                                                criteriaBuilder.equal(root.get(
                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                true)));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        "No attendance records found with conflicts between " +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED + " and "
                                                        +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTodayConflicts() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records with conflicts for today");
                }
                LocalDate today = LocalDate.now();
                // Define start and end of day boundaries as needed
                LocalDate startOfDay = today.atStartOfDay().toLocalDate();
                LocalDate endOfDay = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.or(
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                true),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                false)),
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                false),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                true))),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfDay, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException("No attendance records found with conflicts for today");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneWeekConflicts() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records with conflicts for the past one week");
                }

                LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
                LocalDate today = LocalDate.now();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.or(
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                true),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                false)),
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                false),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                true))),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                oneWeekAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        "No attendance records found with conflicts for the past one week");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTwoWeeksConflicts() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records with conflicts for the past two weeks");
                }

                LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
                LocalDate today = LocalDate.now();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.or(
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                true),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                false)),
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                false),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                true))),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                twoWeeksAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        "No attendance records found with conflicts for the past two weeks");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Conflict for one month
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneMonthConflicts() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records with conflicts for the past one month");
                }

                LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
                LocalDate today = LocalDate.now();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.or(
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                true),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                false)),
                                                criteriaBuilder.and(
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                                                false),
                                                                criteriaBuilder.equal(root.get(
                                                                                ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                                                true))),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                oneMonthAgo, today));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        "No attendance records found with conflicts for the past one month");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllCustomerNotAgreedAttendance() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED), false);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_FOUND_PREFIX +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED
                                                        + " is false.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Customer not agreed for today
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTodayCustomerNotAgreed() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for today",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }

                LocalDate today = LocalDate.now();
                LocalDate startOfDay = today.atStartOfDay().toLocalDate();
                LocalDate endOfDay = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(
                                                root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfDay, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException("No attendance records found where " +
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED
                                        + " is false for today.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // Customer not agreed for one week
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getLastWeekCustomerNotAgreed() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past week",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }

                LocalDate today = LocalDate.now();
                LocalDate startOfWeek = today.minusWeeks(1);
                LocalDate endOfDay = today.atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(
                                                root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfWeek, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException("No attendance records found where " +
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED
                                        + " is false for the past week.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // For two weeks customer not agreed
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getLastTwoWeeksCustomerNotAgreed() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past two weeks",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }
                LocalDate today = LocalDate.now();
                LocalDate startOfTwoWeeks = today.minusWeeks(2); // Two weeks ago
                LocalDate endOfDay = today.atStartOfDay().toLocalDate(); // Today (end of the range)

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(
                                                root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfTwoWeeks, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED
                                                        + " is false for the past two weeks.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // For one month customer not agreed
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getLastMonthCustomerNotAgreed() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past month",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED);
                }
                LocalDate today = LocalDate.now();
                // Here, using one month ago as the start date.
                LocalDate startOfMonth = today.minusMonths(1);
                LocalDate endOfDay = today.atStartOfDay().toLocalDate(); // Today (end of the range)

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(
                                                root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfMonth, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_CUSTOMER_AGREED
                                                        + " is false for the past month.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // For service provider not agreed (i.e. not attended records)
        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getAllNotAttendedRecords() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED);
                }
                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                                .equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED), false);

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED
                                                        + " is false.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // serviceprovider not agreed for today
        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTodayNotAttendedRecords() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for today",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED);
                }
                LocalDate today = LocalDate.now();
                LocalDate startOfDay = today.atStartOfDay().toLocalDate();
                LocalDate endOfDay = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfDay, endOfDay));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED
                                                        + " is false for today.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // for one week
        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneWeekNotAttendedRecords() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past week",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED);
                }
                LocalDate today = LocalDate.now();
                LocalDate startOfWeek = today.minusWeeks(1).atStartOfDay().toLocalDate();
                LocalDate endOfWeek = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfWeek, endOfWeek));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED
                                                        + " is false for the past week.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // for two weeks
        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getTwoWeeksNotAttendedRecords() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past two weeks",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED);
                }
                LocalDate today = LocalDate.now();
                LocalDate startOfTwoWeeks = today.minusWeeks(2).atStartOfDay().toLocalDate();
                LocalDate endOfTwoWeeks = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfTwoWeeks, endOfTwoWeeks));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED
                                                        + " is false for the past two weeks.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

        // for one month
        @Override
        @Transactional(readOnly = true)
        public List<AttendanceDTO> getOneMonthNotAttendedRecords() {
                if (logger.isInfoEnabled()) {

                        logger.info("Fetching attendance records where {} is false for the past month",
                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED);
                }
                LocalDate today = LocalDate.now();
                LocalDate startOfMonth = today.minusMonths(1).atStartOfDay().toLocalDate();
                LocalDate endOfMonth = today.plusDays(1).atStartOfDay().toLocalDate();

                Specification<Attendance> spec = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED),
                                                false),
                                criteriaBuilder.between(root.get(ServiceProviderConstants.ATTENDANCE_FIELD_STATUS),
                                                startOfMonth, endOfMonth));

                List<Attendance> attendanceList = attendanceRepository.findAll(spec);

                if (attendanceList.isEmpty()) {
                        throw new AttendanceNotFoundException(
                                        ServiceProviderConstants.NO_ATTENDANCE_RECORDS_FOUND +
                                                        ServiceProviderConstants.ATTENDANCE_FIELD_IS_ATTENDED
                                                        + " is false for the past month.");
                }

                return attendanceList.stream()
                                .map(attendanceMapper::attendanceToDTO)
                                .toList();
        }

}
