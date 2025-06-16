// src/main/java/com/example/shopmanagement/model/User.java
package com.example.shopmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Or 'customers' if you prefer
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phoneNumber; // Stored as "+91XXXXXXXXXX"

    private String name; // This can be null for new users before they set it

    private String currentOtp;
    private LocalDateTime otpExpiry;
    private boolean active; // True if OTP is verified
    private String email;
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// Constructors (optional, Lombok's @Data can generate these)
    public User() {}

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrentOtp() { return currentOtp; }
    public void setCurrentOtp(String currentOtp) { this.currentOtp = currentOtp; }

    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}