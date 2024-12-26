package com.springboot.app.service;

import com.springboot.app.dto.AttendanceDTO;
//import com.springboot.app.entity.Attendance;

import java.util.List;

public interface AttendanceService {

    // Retrieve all AttendanceDTOs with pagination
    List<AttendanceDTO> getAllAttendance(int page, int size);

    // Retrieve a single AttendanceDTO by its ID
    AttendanceDTO getAttendanceByAttendenceId(Long id);

    // Retrieve all AttendanceDTOs for a specific customer ID
    List<AttendanceDTO> getAttendanceByCustomerId(Long customerId);

    // Retrieve all AttendanceDTOs for a specific service provider ID
    List<AttendanceDTO> getAttendanceByServiceProviderId(Long serviceProviderId);

    // Save a new AttendanceDTO (create new attendance record)
    void saveAttendance(AttendanceDTO attendanceDTO);

    // Update an existing AttendanceDTO
    void updateAttendance(AttendanceDTO attendanceDTO);

    // Delete an AttendanceDTO by its ID
    void deleteAttendance(Long id);

    List<AttendanceDTO> getAllNotifications();

    List<AttendanceDTO> getTodayConflicts();

    List<AttendanceDTO> getOneWeekConflicts();

    List<AttendanceDTO> getTwoWeeksConflicts();

    List<AttendanceDTO> getOneMonthConflicts();
}
