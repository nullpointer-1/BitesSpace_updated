package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.ShopService;
import com.example.shopmanagement.dto.ShopDto; // Change import to ShopDto
import com.example.shopmanagement.model.Shop;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shops")

public class AdminShopController {

    private final ShopService shopService;

    @Autowired
    public AdminShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Endpoint to add a new shop and its associated vendor.
     * POST /api/admin/shops
     * This endpoint now accepts a ShopDto which contains both shop and vendor details.
     * @param shopDto DTO containing details for the new shop and vendor.
     * @return The created Shop entity (converted to ShopDto) with HTTP status 201 Created.
     */
    @PostMapping
    public ResponseEntity<ShopDto> addShopAndVendor(@Valid @RequestBody ShopDto shopDto) { // Changed input DTO
        try {
            Shop newShop = shopService.createShopAndVendor(shopDto); // Call new service method
            return new ResponseEntity<>(shopService.convertToDto(newShop), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error adding shop and vendor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error adding shop and vendor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all shops (and implicitly their associated vendors if joined).
     * GET /api/admin/shops
     * @return ResponseEntity containing a list of ShopDto objects.
     */
    @GetMapping
    public ResponseEntity<List<ShopDto>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        List<ShopDto> shopDtos = shops.stream()
                                      .map(shopService::convertToDto)
                                      .collect(Collectors.toList());
        return ResponseEntity.ok(shopDtos);
    }

    /**
     * Retrieves a shop by its ID.
     * GET /api/admin/shops/{id}
     * @param id The ID of the shop to retrieve.
     * @return ResponseEntity with the Shop entity or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable Long id) {
        return shopService.getShopById(id)
                .map(shopService::convertToDto) // Convert to DTO before returning
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Further endpoints for updating/deleting shops can be added here
}
