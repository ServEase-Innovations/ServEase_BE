package com.cus.customertab.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "no_of_tries", nullable = false)
    private int noOfTries;

    @Column(name = "disable_till")
    private Timestamp disableTill;

    @Column(name = "is_temp_locked", nullable = false)
    private boolean isTempLocked;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    // Automatically set default values before persisting
    @PrePersist
    public void prePersist() {
        this.isTempLocked = false;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }

    public void lock() {
        this.isTempLocked = true;
    }
}