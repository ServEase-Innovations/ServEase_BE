package com.springboot.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
public class UserCredentials {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username; // Stores email ID as primary key

    @Column(name = "password", nullable = false)
    private String password; // Stores encrypted password

    @Column(name = "is_active", nullable = false)
    private boolean isActive; // Indicates if user is active

    @Column(name = "no_of_tries", nullable = false)
    private int noOfTries; // Tracks login attempts, max is 3

    @Column(name = "disable_till")
    private Timestamp disableTill; // Timestamp after which user can retry login

    @Column(name = "is_temp_locked", nullable = false)
    private boolean isTempLocked; // True if account is temporarily locked

    @Column(name = "phone_number", length = 15)
    private String phoneNumber; // Stores phone number as VARCHAR

    @Column(name = "last_login")
    private Timestamp lastLogin; // Timestamp of the last login

    // Automatically set default values before persisting
    @PrePersist
    public void prePersist() {
        this.isTempLocked = false;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;

        // Formatting the current timestamp to "yyyy-MM-dd HH:mm:ss.SSS"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDate = sdf.format(System.currentTimeMillis());

        // Converting the formatted date back to a Timestamp
        this.lastLogin = Timestamp.valueOf(formattedDate);
    }

    // public void deactivate() {
    // this.isActive = false;
    // this.lastLogin = new Timestamp(System.currentTimeMillis());
    // }

    public void lock() {
        this.isTempLocked = true;
    }
}
