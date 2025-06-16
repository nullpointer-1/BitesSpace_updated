package com.example.shopmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime; // Added for timestamps

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String category;

    // --- Added fields to match frontend expectations ---
    @Column(name = "image_url") // Maps to 'image' in frontend
    private String imageUrl;

    private Double rating; // For product ratings

    @Column(name = "preparation_time") // Matches frontend's 'preparationTime'
    private String preparationTime; // e.g., "15-20 min"

    @Column(name = "is_veg")
    private boolean isVeg; // For vegetarian flag

    @Column(nullable = false)
    private boolean available = true; // Default is true

    // --- CRITICAL CHANGE: Product now belongs to a SHOP ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @JsonIgnore // <-- Add this annotation
     private Shop shop; // A product belongs to one shop

    // --- Added timestamps for better data management (optional but good practice) ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(String preparationTime) {
		this.preparationTime = preparationTime;
	}

	public boolean isVeg() {
		return isVeg;
	}

	public void setVeg(boolean isVeg) {
		this.isVeg = isVeg;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
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

	public Product(Long id, String name, String description, Double price, String category, String imageUrl,
			Double rating, String preparationTime, boolean isVeg, boolean available, Shop shop, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.imageUrl = imageUrl;
		this.rating = rating;
		this.preparationTime = preparationTime;
		this.isVeg = isVeg;
		this.available = available;
		this.shop = shop;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Product() {
		
	}
	 public Long getShopId() {
	        return (this.shop != null) ? this.shop.getId() : null;
	    }
}