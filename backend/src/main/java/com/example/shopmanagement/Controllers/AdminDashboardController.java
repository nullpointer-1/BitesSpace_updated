package com.example.shopmanagement.Controllers;
//src/main/java/com/yourcompany/foodapp/admin/controller/AdminDashboardController.java

import com.example.shopmanagement.dto.AnalyticsDataDTO;
import com.example.shopmanagement.dto.DashboardSummaryDTO;
import com.example.shopmanagement.Service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:8081") // Adjust this to your frontend URL
public class AdminDashboardController {

 @Autowired
 private AdminDashboardService adminDashboardService;

 @GetMapping("/summary")
 public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
     DashboardSummaryDTO summary = adminDashboardService.getDashboardSummary();
     return ResponseEntity.ok(summary);
 }

 @GetMapping("/analytics")
 public ResponseEntity<AnalyticsDataDTO> getAnalyticsData() {
     AnalyticsDataDTO analytics = adminDashboardService.getAnalyticsData();
     return ResponseEntity.ok(analytics);
 }
}
