package com.cus.customertab.service;

import com.cus.customertab.dto.KYCDTO;

import java.util.List;

public interface KYCService {

    List<KYCDTO> getAllKYC(int page, int size);
    KYCDTO getKYCById(Long id);
    String addKYC(KYCDTO kycDTO);
    String updateKYC(KYCDTO kycDTO);
}
