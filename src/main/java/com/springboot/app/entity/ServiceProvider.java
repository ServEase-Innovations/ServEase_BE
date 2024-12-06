package com.springboot.app.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import com.springboot.app.enums.DocumentType;
import com.springboot.app.enums.Gender;
import com.springboot.app.enums.Habit;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.LanguageKnown;
import com.springboot.app.enums.Speciality;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "serviceprovider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long serviceproviderId;

	@Column(nullable = false)
	private String firstName;

	private String middleName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false, length = 10)
	private Long mobileNo;

	@Column(length = 10)
	private Long alternateNo;

	@Column(nullable = false)
	private String emailId;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private String buildingName;

	@Column(nullable = false)
	private String locality;

	@Column(nullable = false)
	private String street;

	@Column(nullable = false, length = 6)
	private Integer pincode;

	@Column(nullable = false)

	private String currentLocation;
	private String nearbyLocation;

	private Timestamp enrolledDate;

	@Lob
	private byte[] profilePic;

	private String idNo;

	@Column(nullable = false)
	private boolean isActive;

	@Enumerated(EnumType.STRING)
	private HousekeepingRole housekeepingRole;

	@Enumerated(EnumType.STRING)
	private Habit diet;

	@Enumerated(EnumType.STRING)
	private Habit cookingSpeciality;

	@Enumerated(EnumType.STRING)
	private DocumentType KYC;

	private double rating;

	@Enumerated(EnumType.STRING)
	private LanguageKnown languageKnown;

	@Enumerated(EnumType.STRING)
	private Speciality speciality;

	@Column
	private Integer age;

	@Column
	private String info;
	// private String username;
	// private String password;
	@Column
	private LocalDate DOB;

	@PrePersist
	public void prePersist() {
		// Setting the current timestamp formatted as "yyyy-MM-dd HH:mm:ss.SSS"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String formattedDate = sdf.format(System.currentTimeMillis());
		this.enrolledDate = Timestamp.valueOf(formattedDate);
		this.isActive = true;

		if (this.street != null) {
			this.street = this.street.toLowerCase();
		}
		if (this.locality != null) {
			this.locality = this.locality.toLowerCase();
		}
		if (this.buildingName != null) {
			this.buildingName = this.buildingName.toLowerCase();
		}
		if (this.currentLocation != null) {
			this.currentLocation = this.currentLocation.toLowerCase();
		}
		if (this.nearbyLocation != null) {
			this.nearbyLocation = this.nearbyLocation.toLowerCase();
		}
	}

	// to deactivate
	public void deactivate() {
		this.isActive = false;
	}

}
