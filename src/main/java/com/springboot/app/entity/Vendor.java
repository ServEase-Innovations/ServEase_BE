package com.springboot.app.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long vendorId;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String registrationId;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(nullable = false, length = 10, unique = true)
    private Long phoneNo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private Timestamp createdDate;

    @PrePersist
    public void prePersist() {
        // Setting the current timestamp formatted as "yyyy-MM-dd HH:mm:ss.SSS"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDate = sdf.format(System.currentTimeMillis());
        this.createdDate = Timestamp.valueOf(formattedDate);
        this.isActive = true;

        if (this.companyName != null) {
            this.companyName = this.companyName.toLowerCase();
        }
        if (this.address != null) {
            this.address = this.address.toLowerCase();
        }
    }

    // to deactivate
    public void deactivate() {
        this.isActive = false;
    }
}
