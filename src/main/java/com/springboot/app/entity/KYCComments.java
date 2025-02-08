package com.springboot.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kyc_comments")
public class KYCComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kyc_id", nullable = false)
    @JsonBackReference
    private KYC kyc;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(length = 255)
    private String comment;

    @Column(name = "commented_by", length = 100)
    private String commentedBy;

    @Column(name = "commented_on", nullable = false)
    private Timestamp commentedOn;

    @PrePersist
    protected void onCreate() {
        commentedOn = Timestamp.valueOf(LocalDateTime.now());
    }

}
