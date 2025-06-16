package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.ShopDto;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository; // Keep if needed for finding vendors
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShopDTOService {

    @Autowired // Inject VendorRepository if you need to fetch existing vendors
    private VendorRepository vendorRepository;

    // Convert ShopDto to Shop entity
    public Shop convertToEntity(ShopDto shopDto) {
        Shop shop = new Shop();
        // ID is usually set only for updates, not for new creations (auto-generated)
        // If shopDto.getId() is null, it's a new shop. If not null, it's an update.
        if (shopDto.getId() != null) {
            shop.setId(shopDto.getId());
        }

        shop.setName(shopDto.getName());
        shop.setAddress(shopDto.getAddress()); // Changed from getLocation()
        shop.setContactNumber(shopDto.getContactNumber());
        shop.setCuisine(shopDto.getCuisine()); // Changed from getShopType()
        shop.setActive(shopDto.getActive() != null ? shopDto.getActive() : true); // Handle potential null for active

        // --- Map new fields from DTO to Entity ---
        shop.setImageUrl(shopDto.getImageUrl());
        shop.setRating(shopDto.getRating());
        shop.setDeliveryTime(shopDto.getDeliveryTime());
        shop.setDistance(shopDto.getDistance());
        shop.setSpeciality(shopDto.getSpeciality());
        shop.setVeg(shopDto.isVeg()); // Note: boolean getter is 'isVeg()'
        shop.setFeatured(shopDto.isFeatured());

        // Handle Vendor Relationship:
        // IMPORTANT: The current logic *always* creates a new vendor.
        // In a real application, you would typically:
        // 1. Check if shopDto.getVendorId() is present: Fetch existing vendor using vendorRepository.findById().
        // 2. If vendorId is null AND vendorName/Email etc. are present: Create a new vendor and save it.
        // For now, adhering to your provided logic of creating a new vendor for the shop.
        Vendor vendor = new Vendor();
        if (shopDto.getVendorId() != null) {
            // If a vendor ID is provided, try to fetch the existing vendor
            // This is a more robust approach than always creating a new one
            vendor = vendorRepository.findById(shopDto.getVendorId())
                        .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + shopDto.getVendorId()));
        } else {
            // If no vendor ID, create a new vendor entity
            vendor.setName(shopDto.getVendorName());
            vendor.setEmail(shopDto.getVendorEmail());
            vendor.setUsername(shopDto.getVendorUsername());
            // In a real app, hash and store the password securely, don't pass plain text
            vendor.setPassword(shopDto.getVendorPassword());
            vendor.setContactNumber(shopDto.getVendorContactNumber());
            vendor.setActive(true);
            // You might need to save the new vendor via vendorRepository.save(vendor)
            // if it's a new vendor being created with the shop.
            // This service method would then become more complex or separated.
        }

        // Set bidirectional relationship
        shop.setVendor(vendor);
        vendor.setShop(shop); // Essential for bidirectional @OneToOne

        // Set timestamps for new entities (PrePersist will handle for existing)
        if (shop.getId() == null) { // Only set createdAt for new shops
            shop.setCreatedAt(LocalDateTime.now());
        }
        shop.setUpdatedAt(LocalDateTime.now()); // Always set updatedAt

        return shop;
    }


    // Convert Shop entity to ShopDto
    public ShopDto convertToDTO(Shop shop) {
        Vendor vendor = shop.getVendor(); // Get the associated vendor

        // Construct ShopDto, mapping all fields including the new ones
        return new ShopDto(
            shop.getId(),
            shop.getName(),
            shop.getAddress(), // Changed from getLocation()
            shop.getContactNumber(),
            shop.getCuisine(), // Changed from getShopType()
            // --- Map new fields from Entity to DTO ---
            shop.getImageUrl(),
            shop.getRating(),
            shop.getDeliveryTime(),
            shop.getDistance(),
            shop.getSpeciality(),
            shop.isVeg(),
            shop.isFeatured(),
            shop.isActive(), // Use isActive() for boolean getter

            // Vendor DTO fields
            vendor != null ? vendor.getId() : null,
            vendor != null ? vendor.getName() : null,
            vendor != null ? vendor.getEmail() : null,
            vendor != null ? vendor.getUsername() : null,
            // SECURITY NOTE: DO NOT expose password in DTOs sent to frontend
            // vendor != null ? vendor.getPassword() : null, // Removed for security
            null, // Password should not be sent to the frontend
            vendor != null ? vendor.getContactNumber() : null
        );
    }
}