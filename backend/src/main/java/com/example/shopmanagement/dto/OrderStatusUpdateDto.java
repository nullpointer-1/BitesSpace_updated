package com.example.shopmanagement.dto;

public class OrderStatusUpdateDto {
    private String orderId;
    private String newStatus;
    private Long vendorId; // Must match the type used in OrderStatusUpdateRequest

    public OrderStatusUpdateDto() {}
    public OrderStatusUpdateDto(String orderId, String newStatus, Long vendorId) {
        this.orderId = orderId;
        this.newStatus = newStatus;
        this.vendorId = vendorId;
    }

    // Getters and Setters for all fields
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
}