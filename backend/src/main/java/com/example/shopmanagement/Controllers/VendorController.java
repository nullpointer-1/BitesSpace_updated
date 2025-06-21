package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.JwtService;
import com.example.shopmanagement.Service.ProductService;
import com.example.shopmanagement.Service.VendorService;
import com.example.shopmanagement.dto.AddProductRequest;
import com.example.shopmanagement.dto.ChangePasswordRequest; // New import
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.ProductDTO;
import com.example.shopmanagement.dto.UpdateProductRequest;
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendors") // This controller handles non-admin vendor operations and product operations
@CrossOrigin(
        origins = "[http://127.0.0.1:5500](http://127.0.0.1:5500)",   // your actual front-end origin
        allowCredentials = "true"
)
public class VendorController {

    private final VendorService vendorService;
    private final VendorRepository vendorRepository;
    private final ProductService productService;
    private final JwtService jwtService;

    @Autowired
    public VendorController(
            VendorService vendorService,
            VendorRepository vendorRepository,
            ProductService productService,
            JwtService jwtService) {
        this.vendorService = vendorService;
        this.vendorRepository = vendorRepository;
        this.productService = productService;
        this.jwtService = jwtService;
    }

    private ProductDTO convertToProductDTO(Product product) {
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
                product.getShopId()
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

        return ResponseEntity.ok(vendorService.convertToDto(vendor));
    }

    /**
     * Endpoint for vendors to change their password.
     * POST /api/vendors/change-password
     * The username is extracted from the JWT token.
     * @param authHeader Authorization header containing the JWT token.
     * @param request ChangePasswordRequest DTO containing old and new passwords.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changeVendorPassword(@RequestHeader("Authorization") String authHeader,
                                                  @Valid @RequestBody ChangePasswordRequest request) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        String username = jwtService.getUsernameFromToken(token);
        return vendorService.changePassword(username, request);
    }

    // --- PRODUCT RELATED ENDPOINTS ---

    @GetMapping("/{shopId}/products")
    public ResponseEntity<List<ProductDTO>> getVendorProducts(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        List<ProductDTO> productDTOs = products.stream()
                                                .map(this::convertToProductDTO)
                                                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping("/{shopId}/products")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable("shopId") Long shopId, @RequestBody @Valid AddProductRequest request) {
        try {
            request.setShopId(shopId);
            Product savedProduct = productService.saveProduct(request);
            return new ResponseEntity<>(convertToProductDTO(savedProduct), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{shopId}/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("shopId") Long shopId, @PathVariable Long productId, @RequestBody @Valid UpdateProductRequest request) {
        try {
            Product updatedProduct = productService.updateProduct(productId, request);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{shopId}/products/{productId}/toggle-availability")
    public ResponseEntity<ProductDTO> toggleProductAvailability(@PathVariable("shopId") Long shopId, @PathVariable Long productId) {
        try {
            Product updatedProduct = productService.toggleAvailability(productId);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            System.err.println("Error toggling product availability: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error toggling product availability: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{shopId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("shopId") Long shopId, @PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}