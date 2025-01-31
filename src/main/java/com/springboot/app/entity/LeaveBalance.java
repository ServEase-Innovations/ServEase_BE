package com.springboot.app.entity;

import com.springboot.app.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leave_balance")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "serviceproviderId", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "total_leaves", nullable = false)
    private int totalLeaves;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(name = "leave_taken", nullable = false)
    private int leaveTaken;

    @Column(name = "leave_balance")
    private int leaveBalance;

    @PrePersist
    @PreUpdate
    public void calculateLeaveBalance() {
        this.leaveBalance = this.totalLeaves - this.leaveTaken;
    }
}
