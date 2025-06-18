package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects; // Make sure this import is present

@Data // Keeps Lombok annotations for boilerplate code
@NoArgsConstructor // Lombok generates no-arg constructor
// Removed @AllArgsConstructor here as we'll explicitly define the main constructor
public class VendorDto {
    private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    private String password; // Consider removing this from DTO if not needed for response

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private Long shopId; // <--- ADD THIS NEW FIELD

    // Primary constructor that includes shopId
    public VendorDto(Long id, String name, String email, String username, String password, String contactNumber, Boolean active, Long shopId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.contactNumber = contactNumber;
        this.active = active;
        this.shopId = shopId; // Initialize shopId
    }

    // You had an old constructor for (username, name, email) - keep if still used, but probably not the main one
    public VendorDto(String username, String name, String email) {
        this.username=username;
        this.name=name;
        this.email=email;
    }

    // --- Getters and Setters (Lombok's @Data will generate these, but explicit definitions are also fine) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Long getShopId() { return shopId; } // <--- NEW GETTER
    public void setShopId(Long shopId) { this.shopId = shopId; } // <--- NEW SETTER

    // Override equals, hashCode, toString to include the new shopId field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorDto vendorDto = (VendorDto) o;
        return Objects.equals(id, vendorDto.id) &&
               Objects.equals(name, vendorDto.name) &&
               Objects.equals(email, vendorDto.email) &&
               Objects.equals(username, vendorDto.username) &&
               Objects.equals(password, vendorDto.password) && // Consider removing password from equals/hashCode
               Objects.equals(contactNumber, vendorDto.contactNumber) &&
               Objects.equals(active, vendorDto.active) &&
               Objects.equals(shopId, vendorDto.shopId); // Include shopId
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, username, password, contactNumber, active, shopId); // Include shopId
    }

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
               ", shopId=" + shopId + // Include shopId
               '}';
    }
}
