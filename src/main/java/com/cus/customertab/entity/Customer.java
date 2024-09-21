package com.cus.customertab.entity;

import java.sql.Timestamp;
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

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerId;

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

    private Timestamp enrolledDate;

    @Lob
    private byte[] profilePic;

    @Enumerated(EnumType.STRING)
    private DocumentType KYC;

    private String idNo;

    @Column(nullable = false)
    private boolean isActive;

	//to automatically set data and isActive field
	@PrePersist
	public void prePersist(){
		this.enrolledDate = new Timestamp(System.currentTimeMillis());
		this.isActive = true;
	}

    //enum methods
    public enum Gender{
        MALE, FEMALE, OTHERS
    }

    public enum DocumentType{
        PAN, AADHAR, DL
    }

    //constructors
	public Customer() {
		super();
	}

	public Customer(Long customerId, String firstName, String middleName, String lastName, Long mobileNo,
			Long alternateNo, String emailId, Gender gender, String buildingName, String locality, String street,
			Integer pincode, String currentLocation, Timestamp enrolledDate, byte[] profilePic,
			DocumentType documentType, String idNo, boolean isActive) {
		super();
		this.customerId = customerId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.mobileNo = mobileNo;
		this.alternateNo = alternateNo;
		this.emailId = emailId;
		this.gender = gender;
		this.buildingName = buildingName;
		this.locality = locality;
		this.street = street;
		this.pincode = pincode;
		this.currentLocation = currentLocation;
		this.enrolledDate = enrolledDate;
		this.profilePic = profilePic;
		this.KYC = documentType;
		this.idNo = idNo;
		this.isActive = isActive;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(Long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Long getAlternateNo() {
		return alternateNo;
	}

	public void setAlternateNo(Long alternateNo) {
		this.alternateNo = alternateNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getLocality() {
		return locality;
	}
	
	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public Timestamp getEnrolledDate() {
		return enrolledDate;
	}

	public void setEnrolledDate(Timestamp enrolledDate) {
		this.enrolledDate = enrolledDate;
	}

	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public DocumentType getDocumentType() {
		return KYC;
	}

	public void setDocumentType(DocumentType documentType) {
		this.KYC = documentType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
