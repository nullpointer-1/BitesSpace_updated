package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.CredentialDto;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final JwtService jwtService;
    private final EmailService emailService;

    @Autowired
    public VendorService(VendorRepository vendorRepository, JwtService jwtService, EmailService emailService) {
        this.vendorRepository = vendorRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public Vendor addIntoDb(CredentialDto dto) {
        String email = dto.getEmail();
        String userName = dto.getUsername();
        String plainTextPassword = dto.getPassword();

        Vendor vendor = this.getShopById(dto.getVendorId()) // This method name 'getShopById' seems misleading if it's returning a Vendor
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + dto.getVendorId()));

        vendor.setEmail(email);
        vendor.setUsername(userName);
        vendor.setPassword(plainTextPassword); // WARNING: Storing plain-text password is a security risk

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

	// Renamed for clarity: This fetches a Vendor by ID, not a Shop.
    public Optional<Vendor> getVendorEntityById(Long id) {
        return vendorRepository.findById(id);
    }

    public ResponseEntity<?> login(LoginDto loginDto) {
        Vendor vendor = vendorRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!loginDto.getPassword().equals(vendor.getPassword())) { // WARNING: Direct plain-text comparison
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(vendor.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("vendor", convertToDto(vendor)); // Now this DTO will contain shopId
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
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new RuntimeException("Vendor not found with ID: " + id);
        }
        vendorRepository.deleteById(id);
    }

    // Public helper method for converting Vendor entity to VendorDto
    public VendorDto convertToDto(Vendor vendor) {
        Long shopId = (vendor.getShop() != null) ? vendor.getShop().getId() : null; // Get shopId from associated shop
        return new VendorDto(
                vendor.getId(),
                vendor.getName(),
                vendor.getEmail(),
                vendor.getUsername(),
                null, // Password should not be exposed via DTO
                vendor.getContactNumber(),
                vendor.isActive(),
                shopId // <--- PASS THE shopId HERE
        );
    }

    private void updateVendorFromDto(Vendor vendor, VendorDto dto) {
        vendor.setName(dto.getName());
        vendor.setEmail(dto.getEmail());
        vendor.setUsername(dto.getUsername());
        vendor.setContactNumber(dto.getContactNumber());
        vendor.setActive(dto.getActive());
        // Do not update password or shopId via this generic update method unless intended
    }
}
