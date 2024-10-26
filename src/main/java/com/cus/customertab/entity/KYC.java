package com.cus.customertab.entity;

import java.sql.Timestamp;
import com.cus.customertab.enums.DocumentType;
import com.cus.customertab.enums.Stage;
import com.cus.customertab.enums.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KYC")
public class KYC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kyc_id; 

    @Column(name = "service_provider_id", nullable = false)
    private Long serviceProviderId;

    @Column(name = "is_kyc_validated", nullable = false)
    private boolean isKYCValidated = false;

    @Column(name = "is_kyc_validated_by")
    private Long isKYCValidatedBy;

    @Column(name = "is_kyc_validated_on", updatable = false)
    private Timestamp isKYCValidatedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_type", nullable = false)
    private DocumentType kycType;

    @Column(name = "kyc_type_id", nullable = false)
    private String kycTypeId;

    @Lob
    @Column(name = "kyc_doc")
    private byte[] kycDoc;

    @OneToMany(mappedBy = "kyc", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<KYCComments> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "stage")
    private Stage stage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @PrePersist
    public void prePersist() {
        isKYCValidatedOn = Timestamp.valueOf(LocalDateTime.now());
        stage = Stage.DOC_SUBMITTED;
        status = Status.NOT_STARTED;
    }
}
