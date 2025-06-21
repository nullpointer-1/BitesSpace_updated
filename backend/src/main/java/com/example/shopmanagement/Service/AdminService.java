package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.ChangePasswordRequest;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.model.Admin;
import com.example.shopmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final EmailService emailService; // Assuming you have an EmailService

    @Autowired
    public AdminService(AdminRepository adminRepository, JwtService jwtService, EmailService emailService) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    /**
     * Handles admin login.
     * @param loginDto Contains username and password for login.
     * @return ResponseEntity with JWT token and basic admin info on success.
     */
    public ResponseEntity<?> login(LoginDto loginDto) {
        Admin admin = adminRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // !!! SECURITY WARNING: Direct plain-text password comparison.
        // In a production app, use a password encoder (e.g., BCryptPasswordEncoder.matches())
        // to compare the hashed password from the database with the provided password.
        if (!loginDto.getPassword().equals(admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(admin.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", token); // Token should be 'jwt' for consistency with frontend expectation
        response.put("username", admin.getUsername());
        response.put("isInitialPassword", admin.isInitialPassword());
        // You might add more admin-specific details here if needed

        return ResponseEntity.ok(response);
    }

    /**
     * Handles admin password change.
     * @param username The username of the admin changing the password (from JWT).
     * @param request ChangePasswordRequest containing old and new passwords.
     * @return ResponseEntity indicating success or failure.
     */
    @Transactional
    public ResponseEntity<?> changePassword(String username, ChangePasswordRequest request) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found."));

        // !!! SECURITY WARNING: Direct plain-text password comparison.
        if (!request.getOldPassword().equals(admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password.");
        }

        // !!! SECURITY WARNING: Storing new password in plain text.
        // Hash the new password before saving.
        admin.setPassword(request.getNewPassword());
        admin.setInitialPassword(false); // Mark as not initial password after change
        adminRepository.save(admin);

        // Optionally re-generate token after password change for security
        String newToken = jwtService.generateToken(admin.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully.");
        response.put("jwt", newToken);
        response.put("username", admin.getUsername());
        response.put("isInitialPassword", admin.isInitialPassword());

        return ResponseEntity.ok(response);
    }

    // You can add other admin-related business logic here (e.g., manage vendors, products, etc.)
}