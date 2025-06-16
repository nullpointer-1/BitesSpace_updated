package com.example.shopmanagement.document; // <-- NOTE: 'document' package

// Assuming this is an embedded document that will be part of List<OrderItemDocument> in OrderDocument
public class OrderItemDocument {
    private String productId; // Consistent with product.getId()
    private String productName;  // <-- RENAMED from 'name'
    private double priceAtOrder;    // Stores the priceAtOrder
    private int quantity;
    private String imageUrl;  // Optional, for product image
    private boolean veg;      // Optional, for veg/non-veg status

    // Constructor - MAKE SURE THIS MATCHES EXACTLY WHAT YOU'RE CALLING
    public OrderItemDocument(String productId, String productName, double priceAtOrder, int quantity, String imageUrl, boolean veg) {
        this.productId = productId;
        this.productName = productName;      // <-- Ensure this assignment is present
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.veg = veg;
    }

    // Default constructor for Spring Data MongoDB
    public OrderItemDocument() {}

    // Getters and Setters for all fields (Crucial for MongoDB mapping and frontend serialization)
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

   

    

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPriceAtOrder() {
		return priceAtOrder;
	}

	public void setPriceAtOrder(double priceAtOrder) {
		this.priceAtOrder = priceAtOrder;
	}

	public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isVeg() { return veg; }
    public void setVeg(boolean veg) { this.veg = veg; }
}