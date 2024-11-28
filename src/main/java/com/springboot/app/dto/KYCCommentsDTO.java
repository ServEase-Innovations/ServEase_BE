package com.springboot.app.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCCommentsDTO {
    public Long id;
    public Long serviceProviderId;
    public String comment;
    public String commentedBy;
    public Timestamp commentedOn;
    public Long kyc_id;
}
