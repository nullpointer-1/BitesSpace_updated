package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    public ShopDto(Long id, String name, String address, String contactNumber, String cuisine, String imageUrl,
			Double rating, String deliveryTime, String distance, String speciality, boolean isVeg, boolean featured,
			Boolean active, Long vendorId, String vendorName, String vendorEmail, String vendorUsername,
			String vendorPassword, String vendorContactNumber) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.contactNumber = contactNumber;
		this.cuisine = cuisine;
		this.imageUrl = imageUrl;
		this.rating = rating;
		this.deliveryTime = deliveryTime;
		this.distance = distance;
		this.speciality = speciality;
		this.isVeg = isVeg;
		this.featured = featured;
		this.active = active;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.vendorEmail = vendorEmail;
		this.vendorUsername = vendorUsername;
		this.vendorPassword = vendorPassword;
		this.vendorContactNumber = vendorContactNumber;
	}

	private Long id; // ID is usually Long for SQL entities
    private String name;
    private String address; // Matches the 'address' in frontend and model
    private String contactNumber;
    private String cuisine; // Matches the 'cuisine' in frontend and model (formerly shopType)

    // New fields to match frontend's 'foodStalls' structure
    private String imageUrl;
    private Double rating;
    private String deliveryTime; // To match 'time'
    private String distance;
    private String speciality;
    private boolean isVeg;
    private boolean featured;

    private Boolean active; // Use Boolean for consistency with DTOs, can be boolean in entity

    // Vendor fields (for creating/updating shop with vendor info)
    private Long vendorId; // If associating with an existing vendor
    private String vendorName;
    private String vendorEmail;
    private String vendorUsername;
    private String vendorPassword; // Be careful with passwords in DTOs for security!
    private String vendorContactNumber;

    // Optional: Add constructor for common conversion scenario if needed
    // This constructor helps convert a Shop entity to ShopDto
    public ShopDto(Long id, String name, String address, String contactNumber, String cuisine,
                   String imageUrl, Double rating, String deliveryTime, String distance,
                   String speciality, boolean isVeg, boolean featured, Boolean active,
                   Long vendorId, String vendorName, String vendorEmail,
                   String vendorUsername, String vendorContactNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.cuisine = cuisine;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.distance = distance;
        this.speciality = speciality;
        this.isVeg = isVeg;
        this.featured = featured;
        this.active = active;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.vendorUsername = vendorUsername;
        this.vendorContactNumber = vendorContactNumber;
        // Password is intentionally excluded here for security when converting FROM entity
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public boolean isVeg() {
		return isVeg;
	}

	public void setVeg(boolean isVeg) {
		this.isVeg = isVeg;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorEmail() {
		return vendorEmail;
	}

	public void setVendorEmail(String vendorEmail) {
		this.vendorEmail = vendorEmail;
	}

	public String getVendorUsername() {
		return vendorUsername;
	}

	public void setVendorUsername(String vendorUsername) {
		this.vendorUsername = vendorUsername;
	}

	public String getVendorPassword() {
		return vendorPassword;
	}

	public void setVendorPassword(String vendorPassword) {
		this.vendorPassword = vendorPassword;
	}

	public String getVendorContactNumber() {
		return vendorContactNumber;
	}

	public void setVendorContactNumber(String vendorContactNumber) {
		this.vendorContactNumber = vendorContactNumber;
	}
}