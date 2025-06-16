// src/main/java/com/example/shopmanagement/document/OrderDocument.java
package com.example.shopmanagement.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// import java.time.Instant; // REMOVE THIS IMPORT
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;

@Document(collection = "orders") // This maps to your MongoDB collection name
public class OrderDocument {

    @Id
    private String id; // MongoDB's primary key (usually ObjectId, represented as String)

    private String orderId; // Your custom, user-friendly order ID (e.g., UUID string from frontend)

    private String customerEmail;

    // --- Denormalized Shop Information ---
    private Long shopId; // Reference to SQL Shop ID
    private String shopName; // Denormalized shop name
    private String shopAddress; // Denormalized shop address
    private Long vendorId; // Reference to SQL Vendor ID
    private String vendorName; // Denormalized vendor name
    private String customerName;    // <-- ADD THIS
    private String customerPhone; 
    
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
	private double totalAmount;
    private String status; // E.g., "PLACED", "PREPARING", "READY_FOR_PICKUP", "COMPLETED"
    private Instant orderTime; // CHANGED FROM Instant to String
    private Instant estimatedPickupTime; // CHANGED FROM Instant to String
    private Long userId; 
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	// --- Embedded Order Items ---
    private List<OrderItemDocument> items = new ArrayList<>();

    // Constructors, Getters, Setters
    public OrderDocument() {}

    // Updated constructor to accept String for time fields
    
   

    // Getters and Setters for all fields
    public String getId() { return id; }
    
    public OrderDocument(String id, String orderId, String customerEmail, Long userId,Long shopId, String shopName,
			String shopAddress, Long vendorId, String vendorName, String customerName, String customerPhone,
			double totalAmount, String status, Instant orderTime, Instant estimatedPickupTime,
			List<OrderItemDocument> items) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.customerEmail = customerEmail;
		this.shopId = shopId;
		this.userId=userId;
		this.shopName = shopName;
		this.shopAddress = shopAddress;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.customerName = customerName;
		this.customerPhone = customerPhone;
		this.totalAmount = totalAmount;
		this.status = status;
		this.orderTime = orderTime;
		this.estimatedPickupTime = estimatedPickupTime;
		this.items = items;
	}

	public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getShopAddress() { return shopAddress; }
    public void setShopAddress(String shopAddress) { this.shopAddress = shopAddress; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Updated getters/setters for String time fields
    public Instant getOrderTime() { return orderTime; } // CHANGED RETURN TYPE
    public void setOrderTime(Instant orderTime) { this.orderTime = orderTime; } // CHANGED PARAM TYPE

    public Instant getEstimatedPickupTime() { return estimatedPickupTime; } // CHANGED RETURN TYPE
    public void setEstimatedPickupTime(Instant estimatedPickupTime) { this.estimatedPickupTime = estimatedPickupTime; } // CHANGED PARAM TYPE

    public List<OrderItemDocument> getItems() { return items; }
    public void setItems(List<OrderItemDocument> items) { this.items = items; }
}