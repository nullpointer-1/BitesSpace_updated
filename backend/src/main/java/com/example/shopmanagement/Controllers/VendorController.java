// src/main/java/com/example/shopmanagement/Controllers/VendorController.java
package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.JwtService;
import com.example.shopmanagement.Service.ProductService; // Keep ProductService
import com.example.shopmanagement.Service.VendorService;
import com.example.shopmanagement.dto.AddProductRequest; // Import Add DTO
import com.example.shopmanagement.dto.CredentialDto;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.ProductDTO; // Import ProductDTO for GET responses
import com.example.shopmanagement.dto.UpdateProductRequest; // Import Update DTO
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Product; // Import Product model if needed for raw entity returns
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository; // Keep VendorRepository

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // For DTO conversion

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(
        origins = "http://127.0.0.1:5500",   // your actual front-end origin
        allowCredentials = "true"
)
public class VendorController {

    private final VendorService vendorService;
    private final VendorRepository vendorRepository; // Still used for /me endpoint
    private final ProductService productService; // ProductService is needed
    // private final ProductRepository productRepository; // Not directly used in the controller's logic now, can remove
    private final JwtService jwtService;

    // Use constructor injection for all dependencies
    @Autowired
    public VendorController(
            VendorService vendorService,
            VendorRepository vendorRepository,
            ProductService productService,
            // ProductRepository productRepository, // Remove if not directly used
            JwtService jwtService) {
        this.vendorService = vendorService;
        this.vendorRepository = vendorRepository;
        this.productService = productService;
        // this.productRepository = productRepository; // Remove if not directly used
        this.jwtService = jwtService;
    }

    // Helper method to convert Product entity to ProductDTO for responses
    private ProductDTO convertToProductDTO(Product product) {
        // Ensure ProductDTO constructor matches the fields you want to expose
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getRating(),
                product.getPreparationTime(),
                product.isVeg(),
                product.isAvailable(),
                product.isKetoFriendly(),
                product.isHighProtein(),
                product.isLowCarb(),
                product.getShopId() // Include shopId if ProductDTO has it
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        return vendorService.login(loginDto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getVendorDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String username = jwtService.getUsernameFromToken(token);
        Vendor vendor = vendorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Vendor not found for username: " + username));

        return ResponseEntity.ok(vendorService.convertToDto(vendor)); // Using VendorService's convertToDto
    }

    // This method can be removed if VendorService.convertToDto is used everywhere
    // public VendorDto convertToDto(Vendor vendor) {
    //     return new VendorDto(vendor.getUsername(), vendor.getName(), vendor.getEmail());
    // }

    @GetMapping
    public ResponseEntity<List<VendorDto>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendorById(@PathVariable Long id) {
        try {
            VendorDto vendorDto = vendorService.getVendorById(id);
            return ResponseEntity.ok(vendorDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- PRODUCT RELATED ENDPOINTS (KEPT AS PER REQUEST) ---

    // Get all products for a specific shop (assuming vendorId here refers to shopId linked to that vendor)
    // frontend calls /{vendorId}/products, so we keep vendorId in path and map it to shopId
    @GetMapping("/{shopId}/products") // Changed vendorId to shopId for clarity as it maps to Product.shop
    public ResponseEntity<List<ProductDTO>> getVendorProducts(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        List<ProductDTO> productDTOs = products.stream()
                                                .map(this::convertToProductDTO)
                                                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Add a new product for a specific shop.
     * The path variable is named `vendorId` to match frontend, but treated as `shopId`.
     * POST /api/vendors/{vendorId}/products
     * @param shopId The ID of the shop (passed as vendorId from frontend).
     * @param request DTO containing product details.
     * @return Success message or error.
     */
    @PostMapping("/{shopId}/products") // Changed vendorId to shopId for clarity
    public ResponseEntity<ProductDTO> addProduct(@PathVariable("shopId") Long shopId, @RequestBody @Valid AddProductRequest request) {
        try {
            // Set the shopId in the request DTO from the path variable
            request.setShopId(shopId);
            Product savedProduct = productService.saveProduct(request);
            return new ResponseEntity<>(convertToProductDTO(savedProduct), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // e.g., Shop not found
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing product.
     * The path variable `vendorId` is noted, but not directly used for product update in service.
     * PUT /api/vendors/{vendorId}/products/{productId}
     * @param shopId The ID of the shop (passed as vendorId from frontend).
     * @param productId The ID of the product to update.
     * @param request DTO containing updated product details.
     * @return Updated product DTO or error.
     */
    @PutMapping("/{shopId}/products/{productId}") // Changed vendorId to shopId
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("shopId") Long shopId, @PathVariable Long productId, @RequestBody @Valid UpdateProductRequest request) {
        try {
            // Note: The shopId from the path is not passed to productService.updateProduct
            // because the service's updateProduct method only takes productId and the update DTO.
            // If you need to verify that the product actually belongs to this shopId, you'd add
            // a check in the service or here.
            Product updatedProduct = productService.updateProduct(productId, request);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Product not found
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggles the availability of a product.
     * The path variable `vendorId` is noted, but not directly used by service's toggle method.
     * PATCH /api/vendors/{vendorId}/products/{productId}/toggle-availability
     * (Renamed from PUT and changed to PATCH for semantic correctness)
     * @param shopId The ID of the shop (passed as vendorId from frontend).
     * @param productId The ID of the product.
     * @return Updated product DTO or error.
     */
    @PatchMapping("/{shopId}/products/{productId}/toggle-availability") // Changed vendorId to shopId, changed method to PATCH
    public ResponseEntity<ProductDTO> toggleProductAvailability(@PathVariable("shopId") Long shopId, @PathVariable Long productId) {
        try {
            Product updatedProduct = productService.toggleAvailability(productId);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Product not found
        } catch (Exception e) {
            System.err.println("Error toggling product availability: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Deletes a product.
     * The path variable `vendorId` is noted, but not directly used by service's delete method.
     * DELETE /api/vendors/{vendorId}/products/{productId}
     * @param shopId The ID of the shop (passed as vendorId from frontend).
     * @param productId The ID of the product to delete.
     * @return 204 No Content or error.
     */
    @DeleteMapping("/{shopId}/products/{productId}") // Changed vendorId to shopId
    public ResponseEntity<Void> deleteProduct(@PathVariable("shopId") Long shopId, @PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Product not found
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- OTHER VENDOR RELATED ENDPOINTS ---

    // Handle order completion (example - uncomment if active)
    // @PutMapping("/orders/{orderId}/complete")
    // public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
    //     vendorService.completeOrder(orderId);
    //     return ResponseEntity.ok().build();
    // }

    // Get all vendor orders (example - uncomment if active)
    // @GetMapping("/orders")
    // public ResponseEntity<List<?>> getVendorOrders() {
    //     return ResponseEntity.ok(vendorService.getOrders());
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        try {
            vendorService.deleteVendor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
