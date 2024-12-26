package com.springboot.app.entity;

//import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.LocalTime;

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

    // @Column(name = "attendance_date", nullable = false)
    // private LocalDate date;

    // @Column(name = "attendance_time", nullable = false)
    // private LocalTime time;

    @Column(name = "attendance_status", nullable = false)
    private LocalDateTime attendanceStatus;

    @Column(name = "is_attended", nullable = false)
    private boolean isAttended = false;

    @Column(name = "is_customer_agreed", nullable = false)
    private boolean isCustomerAgreed = true;

    @PrePersist
    public void prePersist() {
        // Automatically set the current date and time when a new record is created
        this.attendanceStatus = LocalDateTime.now();
    }

}
