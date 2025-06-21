package com.example.shopmanagement.model;

import jakarta.persistence.*; // Import JPA annotations
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity
@Table(name = "shops") // Maps this entity to a SQL table named "shops"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

	
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments ID in SQL databases
    private Long id; // ID type typically Long for SQL auto-increment

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address; // Changed from 'location' for clarity with frontend

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "cuisine") // Changed from 'shopType' to 'cuisine' for direct mapping
    private String cuisine;

    @Column(nullable = false)
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL) // Correct for One-to-One relationship in SQL
    @JoinColumn(name = "vendor_id", referencedColumnName  = "id")// Links to vendor's ID column
    private Vendor vendor; // One shop has one vendor

    // --- New fields to match frontend's 'foodStalls' structure ---
    @Column(name = "image_url")
    private String imageUrl; // For the 'image' property in frontend
    private Double rating;
    @Column(name = "delivery_time")
    private String deliveryTime; // To match 'time' (e.g., "15-20 min")
    private String distance; // To match 'distance' (e.g., "0.5 km")
    private String speciality;
    @Column(name = "is_veg")
    private boolean isVeg;
    private boolean featured;

    @Column(name = "created_at", updatable = false) // 'updatable = false' often for createdAt
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // These JPA lifecycle callbacks are correct for SQL databases
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public Shop(Long id, String name, String address, String contactNumber, String cuisine, boolean active,
			Vendor vendor, String imageUrl, Double rating, String deliveryTime, String distance, String speciality,
			boolean isVeg, boolean featured, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.contactNumber = contactNumber;
		this.cuisine = cuisine;
		this.active = active;
		this.vendor = vendor;
		this.imageUrl = imageUrl;
		this.rating = rating;
		this.deliveryTime = deliveryTime;
		this.distance = distance;
		this.speciality = speciality;
		this.isVeg = isVeg;
		this.featured = featured;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Shop() {
		// TODO Auto-generated constructor stub
	}

	@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}