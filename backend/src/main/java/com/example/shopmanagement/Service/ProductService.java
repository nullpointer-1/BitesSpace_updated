package com.example.shopmanagement.Service;

import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.model.Shop; // Import Shop model
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.repository.ShopRepository; // Import ShopRepository
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    // Inject repositories for Product and Shop
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShopRepository shopRepository; // Now injecting ShopRepository instead of VendorRepository

    // --- REMOVED: getProductsByVendorId (Products are linked to Shops) ---
    // If you need to find products by vendor, you'd first find shops for that vendor, then products for those shops.

    // Get a product by its ID
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    // Save a new product for a specific shop
    // Changed vendorId to shopId
    public Product saveProduct(Product product, Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with ID: " + shopId));
        product.setShop(shop); // Set the shop for the product
        return productRepository.save(product);
    }

    // Update an existing product for a specific shop
    // Changed vendorId to shopId
    public Product updateProduct(Long productId, Product updatedProduct, Long shopId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with ID: " + shopId));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setAvailable(updatedProduct.isAvailable());

        // --- NEW FIELDS TO UPDATE ---
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setRating(updatedProduct.getRating());
        existingProduct.setPreparationTime(updatedProduct.getPreparationTime());
        existingProduct.setVeg(updatedProduct.isVeg()); // For boolean 'isVeg'

        existingProduct.setShop(shop); // Set the shop for the product

        return productRepository.save(existingProduct);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // --- This method remains the same and now perfectly aligns with the Product model ---
    public List<Product> getProductsByShopId(Long shopId) {
        return productRepository.findByShop_Id(shopId);
    }

    // Delete a product
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // Update availability of a product
    public Product updateAvailability(Long productId, boolean available) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        product.setAvailable(available);
        return productRepository.save(product);
    }
}