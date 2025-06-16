package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.Service.ProductService; // Assuming you have a ProductService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint to get all products (optional)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
   
    // --- New Endpoint for fetching products by shop ID ---
  
 // In ProductController.java
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Product>> getProductsByShopId(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        if (products.isEmpty()) {
            // Ensure it returns an empty list, not null or an error
            return ResponseEntity.ok(products); // Explicitly return 200 OK with an empty list
        }
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}") // This maps to /api/products/{id}
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returns 404 if product not found
        }
    }

    // You might also have endpoints for creating, updating, deleting products
}