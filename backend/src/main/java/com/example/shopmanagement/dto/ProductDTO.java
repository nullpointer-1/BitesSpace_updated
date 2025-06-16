package com.example.shopmanagement.dto;

import com.example.shopmanagement.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Example ProductDto
public class ProductDTO {
 private Long id;
 private String name;
 private double price;
 private String imageUrl;
 private String description;
 private boolean isVeg;
 private String category;
 private double rating;
 private String preparationTime;
 private Long shopId; // <-- Add this!
 private String shopName; // <-- Add this if you want to include shop name directly

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

public double getPrice() {
	return price;
}

public void setPrice(double price) {
	this.price = price;
}

public String getImageUrl() {
	return imageUrl;
}

public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public boolean isVeg() {
	return isVeg;
}

public void setVeg(boolean isVeg) {
	this.isVeg = isVeg;
}

public String getCategory() {
	return category;
}

public void setCategory(String category) {
	this.category = category;
}

public double getRating() {
	return rating;
}

public void setRating(double rating) {
	this.rating = rating;
}

public String getPreparationTime() {
	return preparationTime;
}

public void setPreparationTime(String preparationTime) {
	this.preparationTime = preparationTime;
}

public Long getShopId() {
	return shopId;
}

public void setShopId(Long shopId) {
	this.shopId = shopId;
}

public String getShopName() {
	return shopName;
}

public void setShopName(String shopName) {
	this.shopName = shopName;
}
public ProductDTO(){
	
}

// Constructors
 public ProductDTO(Product product) {
     this.id = product.getId();
     this.name = product.getName();
     this.price = product.getPrice();
     this.imageUrl = product.getImageUrl();
     this.description = product.getDescription();
     this.isVeg = product.isVeg();
     this.category = product.getCategory();
     this.rating = product.getRating();
     this.preparationTime = product.getPreparationTime();
     if (product.getShop() != null) {
         this.shopId = product.getShop().getId();
         this.shopName = product.getShop().getName(); // Populate shop name
     }
 }

 // Getters and Setters
 // ...
}