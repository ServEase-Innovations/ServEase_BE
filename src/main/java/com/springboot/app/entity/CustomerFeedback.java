package com.springboot.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CustomerFeedback")
public class CustomerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = true)
    private Double rating;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private Timestamp commentedOn;

    @PrePersist
    protected void onCreate() {
        commentedOn = Timestamp.valueOf(LocalDateTime.now());
    }
}
