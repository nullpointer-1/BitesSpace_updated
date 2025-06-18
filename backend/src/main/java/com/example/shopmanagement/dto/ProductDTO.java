// src/main/java/com/example/shopmanagement/dto/ProductDTO.java
package com.example.shopmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Objects;

// This DTO is for representing Product data, possibly for GET requests
// or for very simple add/update operations where specific DTOs aren't used.
// It's different from AddProductRequest and UpdateProductRequest.
public class ProductDTO {
    private Long id; // Include ID for fetching/updating
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    @NotBlank(message = "Category is required")
    private String category;
    private String imageUrl;
    private Double rating;
    private String preparationTime;
    private boolean isVeg;
    private boolean available; // Should be handled by toggle endpoint
    private boolean isKetoFriendly;
    private boolean isHighProtein;
    private boolean isLowCarb;
    private Long shopId; // To relate to the shop if needed in representation


    // --- Constructors ---
    public ProductDTO() {}

    // Constructor for when converting from Product entity to DTO (GET requests)
    public ProductDTO(Long id, String name, String description, Double price, String category,
                      String imageUrl, Double rating, String preparationTime, boolean isVeg,
                      boolean available, boolean isKetoFriendly, boolean isHighProtein,
                      boolean isLowCarb, Long shopId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.preparationTime = preparationTime;
        this.isVeg = isVeg;
        this.available = available;
        this.isKetoFriendly = isKetoFriendly;
        this.isHighProtein = isHighProtein;
        this.isLowCarb = isLowCarb;
        this.shopId = shopId;
    }

    // --- Getters and Setters (manually written since no Lombok) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getPreparationTime() { return preparationTime; }
    public void setPreparationTime(String preparationTime) { this.preparationTime = preparationTime; }
    public boolean isVeg() { return isVeg; }
    public void setVeg(boolean veg) { isVeg = veg; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isKetoFriendly() { return isKetoFriendly; }
    public void setKetoFriendly(boolean ketoFriendly) { isKetoFriendly = ketoFriendly; }
    public boolean isHighProtein() { return isHighProtein; }
    public void setHighProtein(boolean highProtein) { this.isHighProtein = highProtein; }
    public boolean isLowCarb() { return isLowCarb; }
    public void setLowCarb(boolean lowCarb) { isLowCarb = lowCarb; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    // --- equals(), hashCode(), toString() (manually written since no Lombok) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return isVeg == that.isVeg && available == that.available && isKetoFriendly == that.isKetoFriendly && isHighProtein == that.isHighProtein && isLowCarb == that.isLowCarb && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(price, that.price) && Objects.equals(category, that.category) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(rating, that.rating) && Objects.equals(preparationTime, that.preparationTime) && Objects.equals(shopId, that.shopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, category, imageUrl, rating, preparationTime, isVeg, available, isKetoFriendly, isHighProtein, isLowCarb, shopId);
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", category='" + category + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               ", rating=" + rating +
               ", preparationTime='" + preparationTime + '\'' +
               ", isVeg=" + isVeg +
               ", available=" + available +
               ", isKetoFriendly=" + isKetoFriendly +
               ", isHighProtein=" + isHighProtein +
               ", isLowCarb=" + isLowCarb +
               ", shopId=" + shopId +
               '}';
    }
}
