package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.ServiceProviderLeaveDTO;
import com.springboot.app.entity.ServiceProviderLeave;
import com.springboot.app.exception.ServiceProviderLeaveException;
import com.springboot.app.mapper.ServiceProviderLeaveMapper;
import com.springboot.app.repository.ServiceProviderLeaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.util.Collections;

@Service
public class ServiceProviderLeaveServiceImpl implements
        ServiceProviderLeaveService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderLeaveServiceImpl.class);

    private final ServiceProviderLeaveRepository leaveRepository;
    private final ServiceProviderLeaveMapper leaveMapper;

    @Autowired
    public ServiceProviderLeaveServiceImpl(ServiceProviderLeaveRepository leaveRepository,
            ServiceProviderLeaveMapper leaveMapper) {
        this.leaveRepository = leaveRepository;
        this.leaveMapper = leaveMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getAllLeaves() {
        logger.info("Fetching all service provider leave records.");
        List<ServiceProviderLeave> leaves = leaveRepository.findAll();
        if (leaves.isEmpty()) {
            return Collections.emptyList();
        }
        return leaves.stream()
                .map(leaveMapper::serviceProviderLeaveToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderLeaveDTO getLeaveById(Long id) {
        logger.info("Fetching service provider leave record by ID: {}", id);
        return leaveRepository.findById(id)
                .map(leaveMapper::serviceProviderLeaveToDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getLeaveByServiceProviderId(Long serviceProviderId) {
        logger.info("Fetching leave records for service provider ID: {}", serviceProviderId);
        List<ServiceProviderLeave> leaves = leaveRepository.findAll().stream()
                .filter(leave -> leave.getServiceProvider().getServiceproviderId().equals(serviceProviderId))
                .toList();
        return leaves.isEmpty()
                ? Collections.emptyList()
                : leaves.stream()
                        .map(leaveMapper::serviceProviderLeaveToDTO)
                        .toList();
    }

    @Override
    @Transactional
    public String updateLeave(Long id, ServiceProviderLeaveDTO leaveDTO) {
        logger.info("Updating service provider leave record with ID: {}", id);
        Optional<ServiceProviderLeave> existingLeave = leaveRepository.findById(id);

        if (existingLeave.isPresent()) {
            ServiceProviderLeave leave = existingLeave.get();
            leave.setFromDate(leaveDTO.getFromDate());
            leave.setToDate(leaveDTO.getToDate());
            leave.setNoOfDays(leaveDTO.getNoOfDays());
            leave.setDescription(leaveDTO.getDescription());
            leave.setServiceProvider(leaveMapper.dtoToServiceProviderLeave(leaveDTO).getServiceProvider());

            leaveRepository.save(leave);
            logger.debug("Leave record updated with ID: {}", leave.getId());
            return CustomerConstants.UPDATED;
        } else {
            logger.error("No leave found with ID: {}", id);
            return "Data not found with this ID";
        }
    }

    @Override
    @Transactional
    public String deleteLeave(Long id) {
        logger.info("Deleting service provider leave record with ID: {}", id);
        Optional<ServiceProviderLeave> leaveOptional = leaveRepository.findById(id);

        if (leaveOptional.isPresent()) {
            leaveRepository.deleteById(id);
            logger.debug("Leave record deleted with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("No leave found with ID: {}", id);
            return "Data not found with this ID";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getServiceProvidersOnLeaveToday() {
        logger.info("Fetching service providers on leave today.");
        LocalDate today = LocalDate.now();
        List<ServiceProviderLeave> leaves = leaveRepository.findAll().stream()
                .filter(leave -> !leave.getFromDate().isAfter(today) &&
                        !leave.getToDate().isBefore(today))
                .toList();
        return leaves.isEmpty()
                ? null
                : leaves.stream()
                        .map(leaveMapper::serviceProviderLeaveToDTO)
                        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getServiceProvidersOnLeaveNextWeek() {
        logger.info("Fetching service providers on leave next week.");
        LocalDate today = LocalDate.now();
        LocalDate startOfNextWeek = today.plusDays(7L - today.getDayOfWeek().getValue());
        LocalDate endOfNextWeek = startOfNextWeek.plusDays(6);

        List<ServiceProviderLeave> leaves = leaveRepository.findAll().stream()
                .filter(leave -> (leave.getFromDate().isAfter(startOfNextWeek.minusDays(1))
                        && leave.getFromDate().isBefore(endOfNextWeek.plusDays(1)))
                        || (leave.getToDate().isAfter(startOfNextWeek.minusDays(1))
                                && leave.getToDate().isBefore(endOfNextWeek.plusDays(1))))
                .toList();

        return leaves.isEmpty()
                ? null
                : leaves.stream()
                        .map(leaveMapper::serviceProviderLeaveToDTO)
                        .toList();
    }

    @Override
    @Transactional
    public String addLeave(ServiceProviderLeaveDTO leaveDTO) {
        ServiceProviderLeave leave = leaveMapper.dtoToServiceProviderLeave(leaveDTO);
        try {
            leaveRepository.save(leave);
            return CustomerConstants.ADDED;
        } catch (Exception e) {
            logger.error("Error while adding leave record for leave: {}", leave, e);
            throw new ServiceProviderLeaveException("Error while adding leave record for leave: " + leave, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getApprovedLeaves() {
        logger.info("Fetching approved service provider leave records.");
        List<ServiceProviderLeave> leaves = leaveRepository.findAll().stream()
                .filter(ServiceProviderLeave::isApproved)
                .toList();
        return leaves.isEmpty() ? null
                : leaves.stream()
                        .map(leaveMapper::serviceProviderLeaveToDTO)
                        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderLeaveDTO> getUnapprovedLeaves() {
        logger.info("Fetching unapproved service provider leave records.");
        List<ServiceProviderLeave> leaves = leaveRepository.findAll().stream()
                .filter(leave -> !leave.isApproved())
                .toList();
        return leaves.isEmpty() ? null
                : leaves.stream()
                        .map(leaveMapper::serviceProviderLeaveToDTO)
                        .toList();
    }

}
