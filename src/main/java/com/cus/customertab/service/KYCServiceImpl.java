package com.cus.customertab.service;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.KYCDTO;
import com.cus.customertab.entity.KYC;
import com.cus.customertab.mapper.KYCMapper;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KYCServiceImpl implements KYCService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private KYCMapper kycMapper;

    // Get all KYC records
    @Override
    @Transactional
    public List<KYCDTO> getAllKYC() {
        Session session = sessionFactory.getCurrentSession();
        List<KYC> kycList = session.createQuery(CustomerConstants.GET_ALL_KYC, KYC.class).list();
        return kycList.stream()
                .map(kyc -> kycMapper.kycToDTO(kyc))
                .collect(Collectors.toList());
    }

    // Get KYC by ID
    @Override
    @Transactional
    public KYCDTO getKYCById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = session.get(KYC.class, id);
        return kycMapper.kycToDTO(kyc);
    }

    // Add a new KYC record
    @Override
    @Transactional
    public String addKYC(KYCDTO kycDTO) {
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        session.persist(kyc);
        return CustomerConstants.ADDED;
    }

    // Update an existing KYC record
    @Override
    @Transactional
    public String updateKYC(KYCDTO kycDTO) {
        Session session = sessionFactory.getCurrentSession();
        KYC kyc = kycMapper.dtoToKYC(kycDTO);
        session.merge(kyc);
        return CustomerConstants.UPDATED;
    }
}