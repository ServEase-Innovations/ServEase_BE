package com.cus.customertab.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
//import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_provider_request_comments")
public class ServiceProviderRequestComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requestId", nullable = false)
    @JsonBackReference
    private ServiceProviderRequest serviceProviderRequest;

    @Column(nullable = false)
    private String comment; // The comment text

    @Column(name = "commented_by", nullable = false)
    private String commentedBy; // Who made the comment

    @Column(name = "commented_on", nullable = false)
    private Timestamp commentedOn; // When the comment was made

    @PrePersist
    protected void onCreated() {
        commentedOn = Timestamp.valueOf(LocalDateTime.now());
    }

}
