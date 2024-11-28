// package com.springboot.app.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Entity;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Entity
// @Table(name = "service_provider_feedback")

// public class ServiceProviderFeedback {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @ManyToOne
// @JoinColumn(name = "customer_id", nullable = false)
// private Customer customer;
// // private Long customerId;
// @ManyToOne
// @JoinColumn(name = "serviceprovider_id", nullable = false)
// private ServiceProvider serviceproviderId;

// @Column(name = "rating", nullable = false)
// private Double rating;

// @Column(name = "feedback", length = 255)
// private String feedback;

// }
