package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.OtpService;
import com.example.shopmanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = "http://localhost:5173",    // Your React frontend's actual port
        allowCredentials = "true"
)
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    // DTO for incoming requests for mobile number
    static class MobileRequest {
        public String mobileNumber;
    }

    // DTO for incoming requests for OTP verification
    static class OtpVerificationRequest {
        public String mobileNumber;
        public String otp;
    }

    // NEW DTO for registration (name and email)
    static class RegistrationRequest {
        public String mobileNumber;
        public String name;
        public String email; // NEW FIELD
    }

    // Send OTP endpoint (remains the same)
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody MobileRequest request) {
        String mobileNumber = request.mobileNumber;
        if (mobileNumber == null || mobileNumber.length() != 10) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid mobile number. Must be 10 digits."));
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // Generate 6-digit OTP
        String responseMessage = otpService.sendOtp(mobileNumber, otp);

        if (responseMessage.startsWith("✅")) {
            return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "OTP sent successfully to +91" + mobileNumber + "!"
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "message", "Failed to send OTP: " + responseMessage.replace("❌ Error sending OTP: ", "")
            ));
        }
    }

    // Verify OTP and determine user status (Updated to return userEmail)
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        String mobileNumber = request.mobileNumber;
        String otp = request.otp;

        if (mobileNumber == null || otp == null || mobileNumber.length() != 10 || otp.length() != 6) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid mobile number or OTP format."));
        }

        boolean isVerified = otpService.verifyOtp(mobileNumber, otp);

        if (isVerified) {
            Optional<User> userOptional = otpService.findUserByPhoneNumber(mobileNumber);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // A user is "new" if their name or email is not set
                boolean isNewUser = (user.getName() == null || user.getName().trim().isEmpty() || 
                                     user.getEmail() == null || user.getEmail().trim().isEmpty());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("success", true);
                responseBody.put("message", "OTP Verified Successfully!");
                responseBody.put("isNewUser", isNewUser);
                responseBody.put("userId", user.getId());
                responseBody.put("mobileNumber", mobileNumber);
                responseBody.put("userName", user.getName());
                responseBody.put("userEmail", user.getEmail()); // NEW: Include user's email in response

                return ResponseEntity.ok().body(responseBody);
            } else {
                return ResponseEntity.status(500).body(Map.of("success", false, "message", "User not found after OTP verification."));
            }
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Invalid or Expired OTP."));
        }
    }

    // Register user's name and email (Endpoint changed to /register, DTO updated)
    @PostMapping("/register") // Changed endpoint path from /register-name to /register
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        String mobileNumber = request.mobileNumber;
        String name = request.name;
        String email = request.email; // Get email from the request

        if (mobileNumber == null || name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mobile number, name, and email are required."));
        }

        try {
            User updatedUser = otpService.updateUserNameAndEmail(mobileNumber, name, email); // NEW service method call
            return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Registration complete! Welcome, " + updatedUser.getName() + "!",
                        "userId", updatedUser.getId(),
                        "userName", updatedUser.getName(),
                        "mobileNumber", mobileNumber,
                        "userEmail", updatedUser.getEmail() // NEW: Return user email in response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
