package com.example.shopmanagement.dto;


public class DistributionDataPointDTO {
    private String name;
    private int value;

    public DistributionDataPointDTO() {
    }

    public DistributionDataPointDTO(String name, int value) {
        this.name = name;
        this.value = value;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
