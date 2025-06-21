// src/main/java/com/example/shopmanagement/Service/ProductService.java
package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.AddProductRequest; // Import Add DTO
import com.example.shopmanagement.dto.UpdateProductRequest; // Import Update DTO
import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.repository.ShopRepository;
import jakarta.transaction.Transactional; // Use Jakarta Transactional
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Generates constructor for final fields (productRepository, shopRepository)
@Transactional // Apply transactional behavior to all methods in this service
public class ProductService {
	@Autowired
    private ProductRepository productRepository;
	@Autowired
    private  ShopRepository shopRepository;

    // Get a product by its ID
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    /**
     * Saves a new product using an AddProductRequest DTO.
     * This method handles finding the Shop and building the Product entity.
     *
     * @param request The DTO containing product details and the shopId.
     * @return The saved Product entity.
     * @throws IllegalArgumentException if the Shop with the given ID is not found.
     */
    public Product saveProduct(AddProductRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found with ID: " + request.getShopId()));

        Product product = new Product(); // Instantiate Product manually
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setRating(request.getRating());
        product.setPreparationTime(request.getPreparationTime());
        product.setVeg(request.isVeg());
        product.setAvailable(request.isAvailable());
        // New fields from Item
        product.setKetoFriendly(request.isKetoFriendly());
        product.setHighProtein(request.isHighProtein());
        product.setLowCarb(request.isLowCarb());
        product.setShop(shop); // Associate with the found shop

        return productRepository.save(product);
    }

    /**
     * Updates an existing product using an UpdateProductRequest DTO.
     * This method fetches the existing product and updates its mutable fields.
     *
     * @param productId The ID of the product to update.
     * @param request The DTO containing the updated product details.
     * @return The updated Product entity.
     * @throws IllegalArgumentException if the Product with the given ID is not found.
     */
    public Product updateProduct(Long productId, UpdateProductRequest request) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Update basic fields from the request DTO
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setAvailable(request.isAvailable());

        // Update additional fields from the request DTO
        existingProduct.setImageUrl(request.getImageUrl());
        existingProduct.setRating(request.getRating());
        existingProduct.setPreparationTime(request.getPreparationTime());
        existingProduct.setVeg(request.isVeg());

        // Update new dietary fields from the request DTO
        existingProduct.setKetoFriendly(request.isKetoFriendly());
        existingProduct.setHighProtein(request.isHighProtein());
        existingProduct.setLowCarb(request.isLowCarb());

        // Note: We are not changing the 'shop' association in a standard product update.
        // If a product needs to be moved to a different shop, it should be a separate, explicit operation.

        return productRepository.save(existingProduct);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get products by their associated shop ID
    public List<Product> getProductsByShopId(Long shopId) {
        return productRepository.findByShop_Id(shopId);
    }

    // Delete a product by its ID
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    /**
     * Toggles the availability status of a product.
     *
     * @param productId The ID of the product to toggle.
     * @return The updated Product entity.
     * @throws IllegalArgumentException if the Product with the given ID is not found.
     */
    public Product toggleAvailability(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        product.setAvailable(!product.isAvailable()); // Toggle the boolean value
        return productRepository.save(product);
    }
}