// src/main/java/com/example/shopmanagement/dto/VendorDto.java
package com.example.shopmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class VendorDto {
    private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name; // This is the actual vendor's personal name

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    private String password; // Password field, handle with care for security

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private Long shopId;

    // --- NEW FIELDS FOR SHOP DETAILS ---
    // These fields are added to VendorDto so that the frontend can display shop info directly
    private String shopName;     // Corresponds to Shop.name (Outlet Name)
    private String shopAddress;  // Corresponds to Shop.address (Location)
    private String shopCuisine;  // Corresponds to Shop.cuisine (Outlet Type)
    private Boolean shopActive;  // Corresponds to Shop.active

    // Manually added No-Argument Constructor
    public VendorDto() {
    }

    // Manually added All-Argument Constructor for initial creation and mapping
    // Updated to include new shop fields
    public VendorDto(Long id, String name, String email, String username, String password, String contactNumber, Boolean active, Long shopId, String shopName, String shopAddress, String shopCuisine, Boolean shopActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contactNumber = contactNumber;
        this.active = active;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopCuisine = shopCuisine;
        this.shopActive = shopActive;
    }

    // Specific constructor for certain scenarios, e.g., if only username, name, email are known
    public VendorDto(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }


    // Manually added Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getContactNumber() { return contactNumber; }
    public Boolean getActive() { return active; }
    public Long getShopId() { return shopId; }

    // New Getters for Shop Details
    public String getShopName() { return shopName; }
    public String getShopAddress() { return shopAddress; }
    public String getShopCuisine() { return shopCuisine; }
    public Boolean getShopActive() { return shopActive; }


    // Manually added Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setActive(Boolean active) { this.active = active; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    // New Setters for Shop Details
    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setShopAddress(String shopAddress) { this.shopAddress = shopAddress; }
    public void setShopCuisine(String shopCuisine) { this.shopCuisine = shopCuisine; }
    public void setShopActive(Boolean shopActive) { this.shopActive = shopActive; }


    // Manually added equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorDto vendorDto = (VendorDto) o;
        return Objects.equals(id, vendorDto.id) &&
               Objects.equals(name, vendorDto.name) &&
               Objects.equals(email, vendorDto.email) &&
               Objects.equals(username, vendorDto.username) &&
               Objects.equals(password, vendorDto.password) &&
               Objects.equals(contactNumber, vendorDto.contactNumber) &&
               Objects.equals(active, vendorDto.active) &&
               Objects.equals(shopId, vendorDto.shopId) &&
               Objects.equals(shopName, vendorDto.shopName) &&
               Objects.equals(shopAddress, vendorDto.shopAddress) &&
               Objects.equals(shopCuisine, vendorDto.shopCuisine) &&
               Objects.equals(shopActive, vendorDto.shopActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, username, password, contactNumber, active, shopId, shopName, shopAddress, shopCuisine, shopActive);
    }

    // Manually added toString
    @Override
    public String toString() {
        return "VendorDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", username='" + username + '\'' +
               ", password='[PROTECTED]'" +
               ", contactNumber='" + contactNumber + '\'' +
               ", active=" + active +
               ", shopId=" + shopId +
               ", shopName='" + shopName + '\'' +
               ", shopAddress='" + shopAddress + '\'' +
               ", shopCuisine='" + shopCuisine + '\'' +
               ", shopActive=" + shopActive +
               '}';
    }
}