package com.cus.customertab.service;

import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.KYCDTO;
import com.cus.customertab.entity.KYC;
import com.cus.customertab.mapper.KYCMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KYCServiceImpl implements KYCService {

    private static final Logger logger = LoggerFactory.getLogger(KYCServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private KYCMapper kycMapper;

    // Get all KYC records
    @Override
    @Transactional(readOnly = true)
    public List<KYCDTO> getAllKYC(int page, int size) {
        logger.info("Fetching all KYC records with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<KYC> kycList = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_ALL_KYC,
                page,
                size,
                KYC.class);
        logger.debug("Fetched {} KYC records from the database.", kycList.size());

        return kycList.stream()
                .map(kycMapper::kycToDTO)
                .collect(Collectors.toList());
    }

    // Get KYC by ID
    @Override
    @Transactional
    public KYCDTO getKYCById(Long id) {
        logger.info("Fetching KYC record by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = session.get(KYC.class, id);

        if (kyc != null) {
            logger.debug("Found KYC record with ID: {}", id);
        } else {
            logger.error("No KYC record found with ID: {}", id);
        }

        return kycMapper.kycToDTO(kyc);
    }

    // Add a new KYC record
    @Override
    @Transactional
    public String addKYC(KYCDTO kycDTO) {
        logger.info("Adding a new KYC record");
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        session.persist(kyc);
        logger.debug("Persisted new KYC record with ID: {}", kyc.getKyc_id());
        return CustomerConstants.ADDED;
    }

    // Update an existing KYC record
    @Override
    @Transactional
    public String updateKYC(KYCDTO kycDTO) {
        logger.info("Updating KYC record with ID: {}", kycDTO.getKyc_id());
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        session.merge(kyc);
        logger.debug("Updated KYC record with ID: {}", kyc.getKyc_id());

        return CustomerConstants.UPDATED;
    }
}
