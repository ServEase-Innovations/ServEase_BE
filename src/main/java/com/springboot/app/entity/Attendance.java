package com.springboot.app.entity;

import java.time.LocalDateTime;

import com.springboot.app.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serviceProviderId", nullable = false)
    private ServiceProvider serviceProvider;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(name = "attendance_status", nullable = false)
    private LocalDateTime attendanceStatus;

    @Column(name = "is_attended", nullable = false)
    private boolean isAttended = false;

    @Column(name = "is_customer_agreed", nullable = false)
    private boolean isCustomerAgreed = true;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.NOT_STARTED;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @Column(name = "description", length = 500)
    private String description;

    @PrePersist
    public void prePersist() {
        // Automatically set the current date and time when a new record is created
        this.attendanceStatus = LocalDateTime.now();
    }

}
