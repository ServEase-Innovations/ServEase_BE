package com.springboot.app.dto;

import com.springboot.app.enums.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {

    private Long balanceId;
    private Long serviceProviderId;
    private int totalLeaves;
    private LeaveType leaveType;
    private int leaveTaken;
    private int leaveBalance;
}
