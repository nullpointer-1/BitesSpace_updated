package com.example.shopmanagement.Service;

import com.example.shopmanagement.model.User;
import com.example.shopmanagement.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.whatsappFrom}")
    private String fromWhatsApp;

    @Autowired
    private UserRepository userRepository;

    // Helper method to format phone number to E.164 format (+91XXXXXXXXXX)
    private String formatPhoneNumber(String rawPhoneNumber) {
        if (!rawPhoneNumber.startsWith("+")) {
            return "+91" + rawPhoneNumber;
        }
        return rawPhoneNumber;
    }

    // Send OTP
    public String sendOtp(String rawPhoneNumber, String otp) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        final String formattedPhoneNumber = formatPhoneNumber(rawPhoneNumber);

        try {
            // Find existing user or create a new one if not found
            Optional<User> userOptional = userRepository.findByPhoneNumber(formattedPhoneNumber);
            User user = userOptional.orElseGet(() -> {
                User newUser = new User();
                newUser.setPhoneNumber(formattedPhoneNumber);
                // For new users created here, name and email will be set in the /register step
                newUser.setName(null); // Explicitly set to null for new users at this stage
                newUser.setEmail(null); // Explicitly set to null for new users at this stage
                return newUser;
            });

            LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(5);

            // Update OTP and expiry in the User object
            user.setCurrentOtp(otp);
            user.setOtpExpiry(otpExpiry);
            user.setActive(false); // User is not active until OTP is verified
            userRepository.save(user); // Save or update the user

            // Send OTP via WhatsApp using Twilio
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhoneNumber),
                    new PhoneNumber(fromWhatsApp),
                    "Your OTP is: " + otp + ". It is valid for 5 minutes. Do not share this code."
            ).create();

            return "✅ OTP Sent! SID: " + message.getSid();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error sending OTP: " + e.getMessage();
        }
    }

    // Verify OTP
    public boolean verifyOtp(String rawPhoneNumber, String inputOtp) {
        final String formattedPhoneNumber = formatPhoneNumber(rawPhoneNumber);

        Optional<User> userOptional = userRepository.findByPhoneNumber(formattedPhoneNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                return false;
            }

            if (inputOtp.equals(user.getCurrentOtp())) {
                user.setActive(true);
                user.setCurrentOtp(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public void clearOtp(String rawPhoneNumber) {
        final String formattedPhoneNumber = formatPhoneNumber(rawPhoneNumber);
        Optional<User> userOptional = userRepository.findByPhoneNumber(formattedPhoneNumber);
        userOptional.ifPresent(user -> {
            user.setCurrentOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
        });
    }

    public Optional<User> findUserByPhoneNumber(String rawPhoneNumber) {
        final String formattedPhoneNumber = formatPhoneNumber(rawPhoneNumber);
        return userRepository.findByPhoneNumber(formattedPhoneNumber);
    }

    // NEW METHOD to update user name and email
    public User updateUserNameAndEmail(String rawPhoneNumber, String name, String email) {
        final String formattedPhoneNumber = formatPhoneNumber(rawPhoneNumber);
        Optional<User> userOptional = userRepository.findByPhoneNumber(formattedPhoneNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(name);
            user.setEmail(email); // Set the email here
            return userRepository.save(user);
        }
        throw new RuntimeException("User with phone number " + rawPhoneNumber + " not found to update name and email.");
    }
}
