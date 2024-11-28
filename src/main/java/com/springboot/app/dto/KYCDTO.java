package com.springboot.app.dto;

import com.springboot.app.entity.KYCComments;
import com.springboot.app.enums.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCDTO {

    private Long kyc_id;
    private Long serviceProviderId;
    private boolean isKYCValidated;
    private Long isKYCValidatedBy;
    private Timestamp isKYCValidatedOn;
    private DocumentType kycType;
    private String kycTypeId;
    private byte[] kycDoc;
    private List<KYCComments> comments;
    private String stage;
    private String status;

}
