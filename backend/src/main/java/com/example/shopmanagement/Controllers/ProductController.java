// src/main/java/com/example/shopmanagement/Controllers/ProductController.java
package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.ProductService;
import com.example.shopmanagement.dto.AddProductRequest; // Import Add DTO
import com.example.shopmanagement.dto.ProductDTO; // Import ProductDTO for GET responses
import com.example.shopmanagement.dto.UpdateProductRequest; // Import Update DTO
import com.example.shopmanagement.model.Product; // Import Product entity
import jakarta.validation.Valid; // For @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // To help with DTO conversion

@RestController
@RequestMapping("/api/products") // General product endpoint
@CrossOrigin(
        origins = "http://127.0.0.1:5500", // your actual front-end origin
        allowCredentials = "true"
)
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Helper method to convert Product entity to ProductDTO
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
                product.getShopId() // Get shopId from the product
        );
    }

    /**
     * Retrieves all products.
     * GET /api/products
     * @return List of all products.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
                                                .map(this::convertToProductDTO)
                                                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Retrieves products associated with a specific shop ID.
     * GET /api/products/shop/{shopId}
     * This endpoint should primarily be used to fetch products for a given shop display.
     * @param shopId The ID of the shop.
     * @return List of products belonging to the specified shop. Returns 200 OK with an empty list if none found.
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductDTO>> getProductsByShopId(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        List<ProductDTO> productDTOs = products.stream()
                                                .map(this::convertToProductDTO)
                                                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Retrieves a single product by its ID.
     * GET /api/products/{id}
     * @param id The ID of the product.
     * @return The product DTO if found, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        return productOptional.map(product -> new ResponseEntity<>(convertToProductDTO(product), HttpStatus.OK))
                              .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new product for a specific shop.
     * POST /api/products
     * @param request The DTO containing product details and the shopId.
     * @return The created product DTO with 201 Created status, or 400 Bad Request if shop not found.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid AddProductRequest request) {
        try {
            Product savedProduct = productService.saveProduct(request);
            return new ResponseEntity<>(convertToProductDTO(savedProduct), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Catches the "Shop not found" exception from the service
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // More appropriate than null body
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing product.
     * PUT /api/products/{id}
     * @param id The ID of the product to update.
     * @param request The DTO containing the updated product details.
     * @return The updated product DTO with 200 OK, or 404 Not Found if product not found.
     */
    @PutMapping("/{id}") // Changed to /api/products/{id} as shopId is not needed in path for update
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        try {
            Product updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            // Catches "Product not found" exception
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggles the availability status of a product.
     * PATCH /api/products/{id}/toggle-availability
     * @param id The ID of the product to toggle.
     * @return The updated product DTO with 200 OK, or 404 Not Found if product not found.
     */
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<ProductDTO> toggleProductAvailability(@PathVariable Long id) {
        try {
            Product updatedProduct = productService.toggleAvailability(id);
            return ResponseEntity.ok(convertToProductDTO(updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error toggling product availability: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/products/{id}
     * @param id The ID of the product to delete.
     * @return 204 No Content if successful, or 404 Not Found if product not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
