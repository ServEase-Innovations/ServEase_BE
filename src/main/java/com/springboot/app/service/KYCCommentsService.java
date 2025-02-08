package com.springboot.app.service;

import com.springboot.app.dto.KYCCommentsDTO;
import java.util.List;

public interface KYCCommentsService {

    // Method to get all KYC comments
    List<KYCCommentsDTO> getAllKycComments(int page, int size);

    // Method to get a single KYC comment by ID
    KYCCommentsDTO getKycCommentById(Long id);

    // Method to add a new KYC comment
    String addKycComment(KYCCommentsDTO kycCommentsDTO);

    // Method to update an existing KYC comment
    String updateKycComment(Long id, KYCCommentsDTO kycCommentsDTO);

    // Method to delete a KYC comment by ID
    String deleteKycComment(Long id);
}
