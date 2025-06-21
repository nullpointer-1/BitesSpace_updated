// src/main/java/com/example/shopmanagement/Service/VendorService.java
package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.AddVendorDto;
import com.example.shopmanagement.dto.ChangePasswordRequest;
import com.example.shopmanagement.dto.CredentialDto;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.UpdateVendorDto;
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.ShopRepository;
import com.example.shopmanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final ShopRepository shopRepository;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Autowired
    public VendorService(VendorRepository vendorRepository, ShopRepository shopRepository, JwtService jwtService, EmailService emailService) {
        this.vendorRepository = vendorRepository;
        this.shopRepository = shopRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public Vendor addIntoDb(CredentialDto dto) {
        String email = dto.getEmail();
        String userName = dto.getUsername();
        String plainTextPassword = dto.getPassword();

        Vendor vendor = this.getShopById(dto.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + dto.getVendorId()));

        vendor.setEmail(email);
        vendor.setUsername(userName);
        vendor.setPassword(plainTextPassword); // !!! SECURITY WARNING: Storing plain-text password is a severe security risk.

        String subject = "Congratulations, your credentials for our cafeteria have been generated!";
        String message = "Dear " + userName + ",\n\n"
                        + "The admin has generated your credentials. "
                        + "Your username is: " + userName + "\n"
                        + "Your current password is: " + plainTextPassword + "\n\n"
                        + "Please change your password after your first login for security reasons.\n\n"
                        + "Thank you!";

        emailService.sendContactEmail(userName, email, message);

        return vendorRepository.save(vendor);
    }

    public Optional<Vendor> getShopById(Long id) {
        return vendorRepository.findById(id);
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        Vendor vendor = vendorRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!loginDto.getPassword().equals(vendor.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(vendor.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("jwt", token);
        response.put("vendor", convertToDto(vendor));
        response.put("isInitialPassword", vendor.getPassword().equals("initialPassword123"));

        System.out.println("Generated Token: " + token);

        return ResponseEntity.ok(response);
    }

    public List<VendorDto> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public VendorDto getVendorById(Long id) {
        return vendorRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + id));
    }

    @Transactional
    public VendorDto addVendor(AddVendorDto addVendorDto) {
        if (vendorRepository.findByUsername(addVendorDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + addVendorDto.getUsername());
        }
        if (vendorRepository.findByEmail(addVendorDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + addVendorDto.getEmail());
        }

        Shop shop = shopRepository.findById(addVendorDto.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with ID: " + addVendorDto.getShopId()));

        Vendor newVendor = new Vendor();
        newVendor.setName(addVendorDto.getName());
        newVendor.setEmail(addVendorDto.getEmail());
        newVendor.setUsername(addVendorDto.getUsername());
        newVendor.setPassword(addVendorDto.getPassword()); // !!! SECURITY WARNING: Plain-text password. Hash this!
        newVendor.setContactNumber(addVendorDto.getContactNumber());
        newVendor.setActive(addVendorDto.getActive());
        newVendor.setShop(shop);

        Vendor savedVendor = vendorRepository.save(newVendor);
        return convertToDto(savedVendor);
    }

    @Transactional
    public VendorDto updateVendor(Long id, UpdateVendorDto updateVendorDto) {
        Vendor existingVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with ID: " + id));

        // Uniqueness checks for username and email
        // Only check if the username/email is changing AND if the new username/email already exists for *another* vendor
        if (!existingVendor.getUsername().equals(updateVendorDto.getUsername())) {
            if (vendorRepository.findByUsername(updateVendorDto.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already in use: " + updateVendorDto.getUsername());
            }
        }
        if (!existingVendor.getEmail().equals(updateVendorDto.getEmail())) {
            if (vendorRepository.findByEmail(updateVendorDto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use: " + updateVendorDto.getEmail());
            }
        }

        // Update fields from DTO to existing entity
        existingVendor.setName(updateVendorDto.getName());
        existingVendor.setEmail(updateVendorDto.getEmail());
        existingVendor.setUsername(updateVendorDto.getUsername());
        existingVendor.setContactNumber(updateVendorDto.getContactNumber());
        existingVendor.setActive(updateVendorDto.getActive());

        // Only update password if a new one is provided in the DTO
        if (updateVendorDto.getPassword() != null && !updateVendorDto.getPassword().isEmpty()) {
            existingVendor.setPassword(updateVendorDto.getPassword()); // !!! SECURITY WARNING: Plain-text password. Hash this!
        }

        // Update associated shop if shopId is provided/changed
        if (updateVendorDto.getShopId() != null) {
            Shop newShop = shopRepository.findById(updateVendorDto.getShopId())
                    .orElseThrow(() -> new IllegalArgumentException("Shop not found with ID: " + updateVendorDto.getShopId()));
            existingVendor.setShop(newShop);
        } else {
            // If shopId is null in DTO, disassociate the vendor from any shop
            existingVendor.setShop(null);
        }

        // Save the updated entity
        Vendor updatedVendor = vendorRepository.save(existingVendor);
        return convertToDto(updatedVendor);
    }

    @Transactional
    public ResponseEntity<?> changePassword(String username, ChangePasswordRequest request) {
        Vendor vendor = vendorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Vendor not found."));

        if (!request.getOldPassword().equals(vendor.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password.");
        }

        vendor.setPassword(request.getNewPassword());
        vendorRepository.save(vendor);

        String newToken = jwtService.generateToken(vendor.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully.");
        response.put("jwt", newToken);
        response.put("vendor", convertToDto(vendor));
        response.put("isInitialPassword", false);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new RuntimeException("Vendor not found with ID: " + id);
        }
        vendorRepository.deleteById(id);
    }

    /**
     * Converts a Vendor entity to a VendorDto, now including associated Shop details.
     * @param vendor The Vendor entity to convert.
     * @return The corresponding VendorDto.
     */
    public VendorDto convertToDto(Vendor vendor) {
        Long shopId = (vendor.getShop() != null) ? vendor.getShop().getId() : null;
        String shopName = null;
        String shopAddress = null;
        String shopCuisine = null;
        Boolean shopActive = null;

        // If a shop is associated, populate its details
        if (vendor.getShop() != null) {
            Shop shop = vendor.getShop();
            shopName = shop.getName();
            shopAddress = shop.getAddress();
            shopCuisine = shop.getCuisine();
            shopActive = shop.isActive(); // Use isActive() for primitive boolean
        }

        return new VendorDto(
                vendor.getId(),
                vendor.getName(), // Vendor's personal name
                vendor.getEmail(),
                vendor.getUsername(),
                null, // Password should NOT be exposed via DTO for security
                vendor.getContactNumber(),
                vendor.isActive(), // Vendor's active status
                shopId,
                shopName,      // Outlet Name
                shopAddress,   // Outlet Location
                shopCuisine,   // Outlet Type
                shopActive     // Outlet Active Status
        );
    }
}
