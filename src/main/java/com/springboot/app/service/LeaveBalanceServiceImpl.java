package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.LeaveBalanceDTO;
import com.springboot.app.entity.LeaveBalance;
import com.springboot.app.mapper.LeaveBalanceMapper;
import com.springboot.app.repository.LeaveBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceServiceImpl.class);

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveBalanceMapper leaveBalanceMapper;

    // Get all LeaveBalance records
    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceDTO> getAllLeaveBalances() {
        logger.info("Fetching all leave balance records.");
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findAll();
        if (leaveBalances.isEmpty()) {
            logger.error("No data found.");
            return null; // Return null for no data found
        }
        return leaveBalances.stream()
                .map(leaveBalanceMapper::leaveBalanceToDTO)
                .collect(Collectors.toList());
    }

    // Get LeaveBalance by ID
    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceDTO getLeaveBalanceById(Long id) {
        logger.info("Fetching leave balance record by ID: {}", id);
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findById(id);
        if (!leaveBalanceOptional.isPresent()) {
            logger.error("Data not found with this ID: {}", id);
            return null; // Return null for not found record
        }
        return leaveBalanceMapper.leaveBalanceToDTO(leaveBalanceOptional.get());
    }

    // Get LeaveBalance by Service Provider ID
    @Transactional(readOnly = true)
    public List<LeaveBalanceDTO> getLeaveBalancesByServiceProviderId(Long serviceProviderId) {
        logger.info("Fetching leave balance records for service provider ID: {}", serviceProviderId);
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findAll().stream()
                .filter(lb -> lb.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .collect(Collectors.toList());
        if (leaveBalances.isEmpty()) {
            logger.error("No data found for service provider ID: {}", serviceProviderId);
            return null;
        }
        return leaveBalances.stream()
                .map(leaveBalanceMapper::leaveBalanceToDTO)
                .collect(Collectors.toList());
    }

    // Add a new LeaveBalance record
    @Override
    @Transactional
    public String addLeaveBalance(LeaveBalanceDTO leaveBalanceDTO) {
        logger.info("Adding a new leave balance record.");
        try {
            LeaveBalance leaveBalance = leaveBalanceMapper.dtoToLeaveBalance(leaveBalanceDTO);
            leaveBalanceRepository.save(leaveBalance);
            logger.debug("New leave balance record added with ID: {}", leaveBalance.getBalanceId());
            return CustomerConstants.ADDED;
        } catch (Exception e) {
            logger.error("Failed to add new leave balance record", e);
            return "Failed";
        }
    }

    // Update an existing LeaveBalance record by ID
    @Override
    @Transactional
    public String updateLeaveBalance(Long id, LeaveBalanceDTO leaveBalanceDTO) {
        logger.info("Updating leave balance record with ID: {}", id);
        Optional<LeaveBalance> existingLeaveBalance = leaveBalanceRepository.findById(id);

        if (existingLeaveBalance.isPresent()) {
            LeaveBalance leaveBalance = existingLeaveBalance.get();
            leaveBalance.setLeaveType(leaveBalanceDTO.getLeaveType());
            leaveBalance.setLeaveBalance(leaveBalanceDTO.getLeaveBalance());
            leaveBalance.setServiceProvider(leaveBalanceMapper.dtoToLeaveBalance(leaveBalanceDTO).getServiceProvider());
            leaveBalanceRepository.save(leaveBalance);
            logger.debug("Leave balance record updated with ID: {}", leaveBalance.getBalanceId());
            return CustomerConstants.UPDATED;
        } else {
            logger.error("No leave balance found with ID: {}", id);
            return "Data not found with this ID";
        }
    }

    // Delete a LeaveBalance record by ID
    @Override
    @Transactional
    public String deleteLeaveBalance(Long id) {
        logger.info("Deleting leave balance record with ID: {}", id);
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findById(id);

        if (leaveBalanceOptional.isPresent()) {
            leaveBalanceRepository.deleteById(id);
            logger.debug("Leave balance record deleted with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("No leave balance found with ID: {}", id);
            return "Data not found with this ID";
        }
    }
}
