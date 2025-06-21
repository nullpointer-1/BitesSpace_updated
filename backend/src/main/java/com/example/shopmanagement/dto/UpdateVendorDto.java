// src/main/java/com/example/shopmanagement/dto/UpdateVendorDto.java
package com.example.shopmanagement.dto;

// Removed Lombok imports:
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects; // Manually import for equals and hashCode

// Removed Lombok annotations:
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
public class UpdateVendorDto {

    @NotNull(message = "Vendor ID is required for update")
    private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name;

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

    private Long shopId; // Can be null if a vendor is not associated with a shop

    // Manually added No-Argument Constructor
    public UpdateVendorDto() {
    }

    // Manually added All-Argument Constructor
    public UpdateVendorDto(Long id, String name, String email, String username, String password, String contactNumber, Boolean active, Long shopId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contactNumber = contactNumber;
        this.active = active;
        this.shopId = shopId;
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

    // Manually added Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setActive(Boolean active) { this.active = active; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    // Manually added equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateVendorDto that = (UpdateVendorDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(contactNumber, that.contactNumber) && Objects.equals(active, that.active) && Objects.equals(shopId, that.shopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, username, password, contactNumber, active, shopId);
    }

    // Manually added toString
    @Override
    public String toString() {
        return "UpdateVendorDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", username='" + username + '\'' +
               ", password='[PROTECTED]'" +
               ", contactNumber='" + contactNumber + '\'' +
               ", active=" + active +
               ", shopId=" + shopId +
               '}';
    }
}
