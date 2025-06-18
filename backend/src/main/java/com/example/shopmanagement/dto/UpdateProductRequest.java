// src/main/java/com/example/shopmanagement/dto/UpdateProductRequest.java
package com.example.shopmanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String name;
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
	public boolean isKetoFriendly() {
		return isKetoFriendly;
	}
	public void setKetoFriendly(boolean isKetoFriendly) {
		this.isKetoFriendly = isKetoFriendly;
	}
	public boolean isHighProtein() {
		return isHighProtein;
	}
	public void setHighProtein(boolean isHighProtein) {
		this.isHighProtein = isHighProtein;
	}
	public boolean isLowCarb() {
		return isLowCarb;
	}
	public void setLowCarb(boolean isLowCarb) {
		this.isLowCarb = isLowCarb;
	}
	private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Double rating;
    private String preparationTime;
    private boolean isVeg;
    private boolean available; // Don't set default, client explicitly sends
    private boolean isKetoFriendly;
    private boolean isHighProtein;
    private boolean isLowCarb;
    // Optionally, if you allow changing the shop, add:
    // private Long shopId;
}