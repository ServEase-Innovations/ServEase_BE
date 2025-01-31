// package com.springboot.app.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.PrePersist;
// import jakarta.persistence.Table;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.JoinColumn;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;
// import com.springboot.app.enums.LeaveType;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Entity
// @Table(name = "service_provider_leave")
// public class ServiceProviderLeave {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @ManyToOne
// @JoinColumn(name = "serviceproviderId", nullable = false)
// private ServiceProvider serviceProvider;

// @Column(name = "from_date", nullable = false)
// private LocalDate fromDate;

// @Column(name = "to_date", nullable = false)
// private LocalDate toDate;

// @Column(name = "no_of_days")
// private int noOfDays;

// @Enumerated(EnumType.STRING)
// private LeaveType leaveType;

// @Column
// private LocalDate appliedOn;

// @Column
// private LocalDate approvedOn;

// @Column
// private int approvedBy;

// @ManyToOne
// @JoinColumn(name = "backup_by_id", nullable = false)
// private ServiceProvider backupBy;

// @Column
// private boolean isApproved;

// @Column
// private boolean isCustomerInformed;

// @Column
// private boolean isBackupRequired;

// @Column(name = "description", length = 255)
// private String description;

// @PrePersist
// public void prePersist() {
// if (fromDate != null && toDate != null) {
// noOfDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
// }
// }
// }
