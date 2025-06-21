package com.example.shopmanagement.Service;
//src/main/java/com/yourcompany/foodapp/admin/service/AdminDashboardService.java


import com.example.shopmanagement.dto.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminDashboardService {

 public DashboardSummaryDTO getDashboardSummary() {
     // In a real application, this would fetch data from databases
     // E.g., count total revenue from orders, count total orders, count active vendors
     return new DashboardSummaryDTO(
         1_500_000.75, // totalRevenue
         8500L,        // totalOrders
         120L          // totalVendors
     );
 }

 public AnalyticsDataDTO getAnalyticsData() {
     // Mock data for revenue and orders trend
     List<RevenueDataPointDTO> revenueAndOrdersTrend = Arrays.asList(
         new RevenueDataPointDTO("Jan", 100000, 500),
         new RevenueDataPointDTO("Feb", 110000, 550),
         new RevenueDataPointDTO("Mar", 105000, 520),
         new RevenueDataPointDTO("Apr", 120000, 600),
         new RevenueDataPointDTO("May", 130000, 650),
         new RevenueDataPointDTO("Jun", 140000, 700)
     );

     // Mock data for vendor distribution by type (e.g., cuisine types or restaurant types)
     List<DistributionDataPointDTO> vendorDistributionByType = Arrays.asList(
         new DistributionDataPointDTO("Fast Food", 30),
         new DistributionDataPointDTO("Casual Dining", 45),
         new DistributionDataPointDTO("Fine Dining", 10),
         new DistributionDataPointDTO("Cafeteria", 25)
     );

     // Mock data for vendor distribution by location (e.g., cities)
     List<DistributionDataPointDTO> vendorDistributionByLocation = Arrays.asList(
         new DistributionDataPointDTO("New York", 50),
         new DistributionDataPointDTO("Los Angeles", 30),
         new DistributionDataPointDTO("Chicago", 20),
         new DistributionDataPointDTO("Houston", 15)
     );

     return new AnalyticsDataDTO(revenueAndOrdersTrend, vendorDistributionByType, vendorDistributionByLocation);
 }
}
