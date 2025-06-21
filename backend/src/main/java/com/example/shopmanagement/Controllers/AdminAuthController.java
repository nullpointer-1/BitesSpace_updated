package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.AdminService;
import com.example.shopmanagement.Service.JwtService;
import com.example.shopmanagement.dto.ChangePasswordRequest;
import com.example.shopmanagement.dto.LoginDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@CrossOrigin(
        origins = "[http://127.0.0.1:5500](http://127.0.0.1:5500)", // Your frontend origin
        allowCredentials = "true"
)
public class AdminAuthController {

    private final AdminService adminService;
    private final JwtService jwtService; // Needed to extract username from token

    @Autowired
    public AdminAuthController(AdminService adminService, JwtService jwtService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    /**
     * Admin login endpoint.
     * POST /api/admin/auth/login
     * @param loginDto Contains username and password.
     * @return JWT token and admin details.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        return adminService.login(loginDto);
    }

    /**
     * Admin change password endpoint.
     * POST /api/admin/auth/change-password
     * Requires a valid JWT token in the Authorization header.
     * @param authHeader Authorization header with Bearer token.
     * @param request ChangePasswordRequest DTO.
     * @return Success message or error.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String username = jwtService.getUsernameFromToken(token); // Get username from token
        return adminService.changePassword(username, request);
    }
}
