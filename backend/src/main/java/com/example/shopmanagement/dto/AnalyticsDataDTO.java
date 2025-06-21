package com.example.shopmanagement.dto;

import java.util.List;

public class AnalyticsDataDTO {
    private List<RevenueDataPointDTO> revenueAndOrdersTrend;
    private List<DistributionDataPointDTO> vendorDistributionByType;
    private List<DistributionDataPointDTO> vendorDistributionByLocation;

    public AnalyticsDataDTO() {
    }

    public AnalyticsDataDTO(List<RevenueDataPointDTO> revenueAndOrdersTrend, List<DistributionDataPointDTO> vendorDistributionByType, List<DistributionDataPointDTO> vendorDistributionByLocation) {
        this.revenueAndOrdersTrend = revenueAndOrdersTrend;
        this.vendorDistributionByType = vendorDistributionByType;
        this.vendorDistributionByLocation = vendorDistributionByLocation;
    }

    // Getters and Setters
    public List<RevenueDataPointDTO> getRevenueAndOrdersTrend() {
        return revenueAndOrdersTrend;
    }

    public void setRevenueAndOrdersTrend(List<RevenueDataPointDTO> revenueAndOrdersTrend) {
        this.revenueAndOrdersTrend = revenueAndOrdersTrend;
    }

    public List<DistributionDataPointDTO> getVendorDistributionByType() {
        return vendorDistributionByType;
    }

    public void setVendorDistributionByType(List<DistributionDataPointDTO> vendorDistributionByType) {
        this.vendorDistributionByType = vendorDistributionByType;
    }

    public List<DistributionDataPointDTO> getVendorDistributionByLocation() {
        return vendorDistributionByLocation;
    }

    public void setVendorDistributionByLocation(List<DistributionDataPointDTO> vendorDistributionByLocation) {
        this.vendorDistributionByLocation = vendorDistributionByLocation;
    }
}
