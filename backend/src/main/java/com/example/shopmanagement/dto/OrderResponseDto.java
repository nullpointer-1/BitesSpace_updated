package com.example.shopmanagement.dto;
public class OrderResponseDto {
    private String orderId; // Or Long if your order ID is numeric
    private String message;

    public OrderResponseDto(String orderId, String message) {
        this.orderId = orderId;
        this.message = message;
    }

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public OrderResponseDto() {
		
	}
    // Getters and Setters
    // ...
}