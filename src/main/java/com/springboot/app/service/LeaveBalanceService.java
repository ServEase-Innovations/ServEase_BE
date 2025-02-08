package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.LeaveBalanceDTO;

public interface LeaveBalanceService {

    List<LeaveBalanceDTO> getAllLeaveBalances();

    LeaveBalanceDTO getLeaveBalanceById(Long id);

    String addLeaveBalance(LeaveBalanceDTO leaveBalanceDTO);

    String updateLeaveBalance(Long id, LeaveBalanceDTO leaveBalanceDTO);

    String deleteLeaveBalance(Long id);

    List<LeaveBalanceDTO> getLeaveBalancesByServiceProviderId(Long serviceProviderId);
}
