package com.springboot.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.KYCDTO;
import com.springboot.app.entity.KYC;
import com.springboot.app.mapper.KYCMapper;
import com.springboot.app.repository.KYCRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KYCServiceImpl implements KYCService {

    private static final Logger logger = LoggerFactory.getLogger(KYCServiceImpl.class);

    private final KYCRepository kycRepository;
    private final KYCMapper kycMapper;

    @Autowired
    public KYCServiceImpl(KYCRepository kycRepository, KYCMapper kycMapper) {
        this.kycRepository = kycRepository;
        this.kycMapper = kycMapper;
    }

    // Get all KYC records
    @Override
    @Transactional(readOnly = true)
    public List<KYCDTO> getAllKYC(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all KYC records with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<KYC> kycList = kycRepository.findAll(pageable).getContent();

        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} KYC records from the database.", kycList.size());
        }
        return kycList.stream()
                .map(kycMapper::kycToDTO)
                .toList();
    }

    // Get KYC by ID
    @Override
    @Transactional
    public KYCDTO getKYCById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching KYC record by ID: {}", id);
        }
        KYC kyc = kycRepository.findById(id).orElse(null);

        if (kyc != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found KYC record with ID: {}", id);
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No KYC record found with ID: {}", id);
            }
        }

        return kycMapper.kycToDTO(kyc);
    }

    // Add a new KYC record
    @Override
    @Transactional
    public String addKYC(KYCDTO kycDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding a new KYC record");
        }
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        kycRepository.save(kyc); // Use JPA to persist the entity
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new KYC record with ID: {}", kyc.getKyc_id());
        }
        return CustomerConstants.ADDED;
    }

    // Update an existing KYC record
    @Override
    @Transactional
    public String updateKYC(KYCDTO kycDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating KYC record with ID: {}", kycDTO.getKyc_id());
        }
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        kycRepository.save(kyc); 
        if (logger.isDebugEnabled()) {
            logger.debug("Updated KYC record with ID: {}", kyc.getKyc_id());
        }

        return CustomerConstants.UPDATED;
    }
}
