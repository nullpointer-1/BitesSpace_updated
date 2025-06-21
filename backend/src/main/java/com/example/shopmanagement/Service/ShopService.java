package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.ShopDto; // Use ShopDto for input
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.model.Vendor; // Import Vendor model
import com.example.shopmanagement.repository.ShopRepository;
import com.example.shopmanagement.repository.VendorRepository; // Import VendorRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Import LocalDateTime
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final VendorRepository vendorRepository; // Inject VendorRepository

    @Autowired
    public ShopService(ShopRepository shopRepository, VendorRepository vendorRepository) {
        this.shopRepository = shopRepository;
        this.vendorRepository = vendorRepository;
    }

    /**
     * Saves a new shop and its associated vendor to the database.
     * This method now accepts a ShopDto which contains both shop and vendor details.
     * Due to cascading, saving the Shop will also save the Vendor.
     * @param shopDto The DTO containing the shop and vendor details.
     * @return The newly created Shop entity.
     */
    @Transactional
    public Shop createShopAndVendor(ShopDto shopDto) {
        // First, check for existing vendor username or email to prevent duplicates
        if (vendorRepository.findByUsername(shopDto.getVendorUsername()).isPresent()) {
            throw new IllegalArgumentException("Vendor username already exists: " + shopDto.getVendorUsername());
        }
        if (vendorRepository.findByEmail(shopDto.getVendorEmail()).isPresent()) {
            throw new IllegalArgumentException("Vendor email already exists: " + shopDto.getVendorEmail());
        }

        // Create Vendor entity from ShopDto
        Vendor vendor = new Vendor();
        vendor.setName(shopDto.getVendorName());
        vendor.setEmail(shopDto.getVendorEmail());
        vendor.setUsername(shopDto.getVendorUsername());
        vendor.setPassword(shopDto.getVendorPassword()); // !!! SECURITY WARNING: Plain-text password. Hash this!
        vendor.setContactNumber(shopDto.getVendorContactNumber());
        vendor.setActive(shopDto.getActive()); // Assuming vendor active status is tied to shop's active status
        // Set creation/update times for vendor (though @PrePersist handles it if not set explicitly)
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        // Create Shop entity from ShopDto
        Shop shop = new Shop();
        shop.setName(shopDto.getName());
        shop.setAddress(shopDto.getAddress());
        shop.setContactNumber(shopDto.getContactNumber());
        shop.setCuisine(shopDto.getCuisine());
        shop.setActive(shopDto.getActive());
        shop.setImageUrl(shopDto.getImageUrl());
        shop.setRating(shopDto.getRating());
        shop.setDeliveryTime(shopDto.getDeliveryTime());
        shop.setDistance(shopDto.getDistance());
        shop.setSpeciality(shopDto.getSpeciality());
        shop.setVeg(shopDto.isVeg());
        shop.setFeatured(shopDto.isFeatured());
        // Set creation/update times for shop (though @PrePersist handles it if not set explicitly)
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());

        // Link vendor to shop
        shop.setVendor(vendor);
        vendor.setShop(shop); // Also set the shop on the vendor side for bidirectional relationship consistency

        // Save the shop; due to CascadeType.ALL, the vendor will be saved automatically
        return shopRepository.save(shop);
    }

    /**
     * Retrieves all shops.
     * @return A list of all Shop entities.
     */
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    /**
     * Retrieves a shop by its ID.
     * @param id The ID of the shop.
     * @return An Optional containing the Shop if found, or empty.
     */
    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    // You can add more business logic for updating, deleting shops here
    // If you need to return a ShopDto from this service, you'd add a convertToDto method.
    public ShopDto convertToDto(Shop shop) {
        Long vendorId = (shop.getVendor() != null) ? shop.getVendor().getId() : null;
        String vendorName = (shop.getVendor() != null) ? shop.getVendor().getName() : null;
        String vendorEmail = (shop.getVendor() != null) ? shop.getVendor().getEmail() : null;
        String vendorUsername = (shop.getVendor() != null) ? shop.getVendor().getUsername() : null;
        String vendorContactNumber = (shop.getVendor() != null) ? shop.getVendor().getContactNumber() : null;
        // Do NOT expose vendor password here

        return new ShopDto(
            shop.getId(),
            shop.getName(),
            shop.getAddress(),
            shop.getContactNumber(),
            shop.getCuisine(),
            shop.getImageUrl(),
            shop.getRating(),
            shop.getDeliveryTime(),
            shop.getDistance(),
            shop.getSpeciality(),
            shop.isVeg(),
            shop.isFeatured(),
            shop.isActive(),
            vendorId,
            vendorName,
            vendorEmail,
            vendorUsername,
            null, // Password must be null for security
            vendorContactNumber
        );
    }
}