package com.example.shopmanagement.dto;


public class RevenueDataPointDTO {
    private String month;
    private double revenue;
    private int orders;

    public RevenueDataPointDTO() {
    }

    public RevenueDataPointDTO(String month, double revenue, int orders) {
        this.month = month;
        this.revenue = revenue;
        this.orders = orders;
    }

    // Getters and Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }
}
