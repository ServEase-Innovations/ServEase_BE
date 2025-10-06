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

import ch.hsr.geohash.GeoHash;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
//import jakarta.persistence.Lob;
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

	@Column(nullable = true)
	private String firstName;

	private String middleName;

	@Column(nullable = true)
	private String lastName;

	@Column(nullable = true, length = 10, unique = true)
	private Long mobileNo;

	@Column(length = 10)
	private Long alternateNo;

	@Column(nullable = true, unique = true)
	private String emailId;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = true)
	private String buildingName;

	@Column(nullable = true)
	private String locality;

	@Column(nullable = true)
	private String street;

	@Column(nullable = true, length = 6)
	private Integer pincode;

	@Column(nullable = true)
	private String currentLocation;

	@Column
	private String nearbyLocation;

	@Column
	private String location;

	@Column
	private Timestamp enrolledDate;

	// @Lob
	// private byte[] profilePic;
	@Column
	private String profilePic;

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

	@Column
	private LocalDate DOB;

	@Column
	private Integer experience;

	@Column
	private String timeslot;

	@Column
	private Long vendorId;

	@Column
	private Double latitude;

	@Column
	private Double longitude;

	@Column
	private String geoHash4;

	@Column
	private String geoHash5;

	@Column
	private String geoHash6;

	@Column
	private String geoHash7;

	// private boolean privacy;
	// @Column(name = "key_facts")
	// private boolean keyFacts;

	// Permanent Address
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "permanent_address_id", referencedColumnName = "id")
	private Address permanentAddress;

	// Correspondence Address
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "correspondence_address_id", referencedColumnName = "id")
	private Address correspondenceAddress;

	@PrePersist
	public void prePersist() {
		// Setting the current timestamp formatted as "yyyy-MM-dd HH:mm:ss.SSS"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String formattedDate = sdf.format(System.currentTimeMillis());
		this.enrolledDate = Timestamp.valueOf(formattedDate);
		this.isActive = true;
		this.geoHash4 = GeoHash.withCharacterPrecision(this.latitude, this.longitude, 4).toBase32();
		this.geoHash5 = GeoHash.withCharacterPrecision(this.latitude, this.longitude, 5).toBase32();
		this.geoHash6 = GeoHash.withCharacterPrecision(this.latitude, this.longitude, 6).toBase32();
		this.geoHash7 = GeoHash.withCharacterPrecision(this.latitude, this.longitude, 7).toBase32();

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
		this.isActive = true;
	}

}
