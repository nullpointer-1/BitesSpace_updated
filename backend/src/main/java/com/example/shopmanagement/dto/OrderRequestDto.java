package com.example.shopmanagement.dto;

import java.time.Instant;
import java.util.List;

public class OrderRequestDto {
    private String customerEmail;
    private String customerName;    // <-- ADD THIS FIELD
    private String customerPhone;   // <-- ADD THIS FIELD
    private Long shopId; // SQL Shop ID
    private List<OrderItemDto> items;
    private double totalAmount;
    private String status;
    private Instant orderDate;
    private Instant estimatedPickupTime;
    private Long userId;
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	// --- Constructor updated to include new fields ---
    public OrderRequestDto(String customerEmail, String customerName, String customerPhone,Long userId, Long shopId,
                           List<OrderItemDto> items, double totalAmount, String status,
                           Instant orderDate, Instant estimatedPickupTime) {
        super();
        this.customerEmail = customerEmail;
        this.customerName = customerName; // Assign new field
        this.customerPhone = customerPhone;
        this.userId=userId;// Assign new field
        this.shopId = shopId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.estimatedPickupTime = estimatedPickupTime;
    }

    public OrderRequestDto() {
        // Default constructor
    }

    // --- Add Getters and Setters for customerName and customerPhone ---
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    // Existing Getters and Setters
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public Long getShopId() {
        return shopId;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public List<OrderItemDto> getItems() {
        return items;
    }
    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Instant getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }
    public Instant getEstimatedPickupTime() {
        return estimatedPickupTime;
    }
    public void setEstimatedPickupTime(Instant estimatedPickupTime) {
        this.estimatedPickupTime = estimatedPickupTime;
    }
}