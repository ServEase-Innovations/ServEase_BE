package com.springboot.app.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_transaction")
public class BookingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "engagement_id", nullable = false)
    private ServiceProviderEngagement engagement;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    // ✅ Setters and getters
    public void setEngagement(ServiceProviderEngagement engagement) {
        this.engagement = engagement;
    }

    public ServiceProviderEngagement getEngagement() {
        return engagement;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        if (this.transactionId == null) {
            this.transactionId = UUID.randomUUID().toString();
        }
    }
}
