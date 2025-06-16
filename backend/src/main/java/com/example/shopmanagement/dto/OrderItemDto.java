package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId; // SQL Product ID
    private int quantity;
    private double priceAtOrder; // Crucial: Price at the time of order
    // Add product name, image, isVeg here for denormalization if needed
    private String productName;
    private String imageUrl;
    private boolean isVeg;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPriceAtOrder() {
		return priceAtOrder;
	}
	public void setPriceAtOrder(double priceAtOrder) {
		this.priceAtOrder = priceAtOrder;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public boolean isVeg() {
		return isVeg;
	}
	public void setVeg(boolean isVeg) {
		this.isVeg = isVeg;
	}
	public OrderItemDto(Long productId, int quantity, double priceAtOrder, String productName, String imageUrl,
			boolean isVeg) {
		super();
		this.productId = productId;
		this.quantity = quantity;
		this.priceAtOrder = priceAtOrder;
		this.productName = productName;
		this.imageUrl = imageUrl;
		this.isVeg = isVeg;
	}
public OrderItemDto() {
	
}
    // Getters and Setters
    // ...
}