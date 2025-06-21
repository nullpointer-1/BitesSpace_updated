package com.example.shopmanagement.dto;


public class DashboardSummaryDTO {
 private double totalRevenue;
 private long totalOrders;
 private long totalVendors;

 public DashboardSummaryDTO() {
 }

 public DashboardSummaryDTO(double totalRevenue, long totalOrders, long totalVendors) {
     this.totalRevenue = totalRevenue;
     this.totalOrders = totalOrders;
     this.totalVendors = totalVendors;
 }

 // Getters and Setters
 public double getTotalRevenue() {
     return totalRevenue;
 }

 public void setTotalRevenue(double totalRevenue) {
     this.totalRevenue = totalRevenue;
 }

 public long getTotalOrders() {
     return totalOrders;
 }

 public void setTotalOrders(long totalOrders) {
     this.totalOrders = totalOrders;
 }

 public long getTotalVendors() {
     return totalVendors;
 }

 public void setTotalVendors(long totalVendors) {
     this.totalVendors = totalVendors;
 }
}
