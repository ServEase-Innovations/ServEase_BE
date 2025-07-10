package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.constant.ServiceProviderConstants;
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

import java.util.Collections;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceServiceImpl.class);

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;

    @Autowired
    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository,
            LeaveBalanceMapper leaveBalanceMapper) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveBalanceMapper = leaveBalanceMapper;
    }

    // Get all LeaveBalance records
    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceDTO> getAllLeaveBalances() {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all leave balance records.");
        }
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findAll();
        if (leaveBalances.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("No data found.");
            }
            return Collections.emptyList(); 
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} leave balance records from the database.", leaveBalances.size());
        }
        return leaveBalances.stream()
                .map(leaveBalanceMapper::leaveBalanceToDTO)
                .toList(); 
    }

    // Get LeaveBalance by ID
    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceDTO getLeaveBalanceById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching leave balance record by ID: {}", id);
        }
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findById(id);
        if (!leaveBalanceOptional.isPresent()) {
            if (logger.isErrorEnabled()) {
                logger.error("Data not found with this ID: {}", id);
            }
            return null; 
        }
        return leaveBalanceMapper.leaveBalanceToDTO(leaveBalanceOptional.get());
    }

    // Get LeaveBalance by Service Provider ID
    @Transactional(readOnly = true)
    public List<LeaveBalanceDTO> getLeaveBalancesByServiceProviderId(Long serviceProviderId) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching leave balance records for service provider ID: {}", serviceProviderId);
        }
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findAll().stream()
                .filter(lb -> lb.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .toList();

        if (leaveBalances.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error("No data found for service provider ID: {}", serviceProviderId);
            }
            return Collections.emptyList(); 
        }

        return leaveBalances.stream()
                .map(leaveBalanceMapper::leaveBalanceToDTO)
                .toList();
    }

    // Add a new LeaveBalance record
    @Override
    @Transactional
    public String addLeaveBalance(LeaveBalanceDTO leaveBalanceDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding a new leave balance record.");
        }
        try {
            LeaveBalance leaveBalance = leaveBalanceMapper.dtoToLeaveBalance(leaveBalanceDTO);
            leaveBalanceRepository.save(leaveBalance);
            if (logger.isDebugEnabled()) {
                logger.debug("New leave balance record added with ID: {}", leaveBalance.getBalanceId());
            }
            return CustomerConstants.ADDED;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to add new leave balance record", e);
            }
            return CustomerConstants.FAILED;
        }
    }

    // Update an existing LeaveBalance record by ID
    @Override
    @Transactional
    public String updateLeaveBalance(Long id, LeaveBalanceDTO leaveBalanceDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating leave balance record with ID: {}", id);
        }
        Optional<LeaveBalance> existingLeaveBalance = leaveBalanceRepository.findById(id);

        if (existingLeaveBalance.isPresent()) {
            LeaveBalance leaveBalance = existingLeaveBalance.get();
            leaveBalance.setLeaveType(leaveBalanceDTO.getLeaveType());
            leaveBalance.setLeaveBalance(leaveBalanceDTO.getLeaveBalance());
            leaveBalance.setServiceProvider(leaveBalanceMapper.dtoToLeaveBalance(leaveBalanceDTO).getServiceProvider());
            leaveBalanceRepository.save(leaveBalance);
            if (logger.isDebugEnabled()) {
                logger.debug("Leave balance record updated with ID: {}", leaveBalance.getBalanceId());
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No leave balance found with ID: {}", id);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    // Delete a LeaveBalance record by ID
    @Override
    @Transactional
    public String deleteLeaveBalance(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting leave balance record with ID: {}", id);
        }
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findById(id);

        if (leaveBalanceOptional.isPresent()) {
            leaveBalanceRepository.deleteById(id);
            if (logger.isDebugEnabled()) {
                logger.debug("Leave balance record deleted with ID: {}", id);
            }
            return CustomerConstants.DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No leave balance found with ID: {}", id);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }
}
