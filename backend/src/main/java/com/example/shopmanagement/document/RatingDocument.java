package com.example.shopmanagement.document;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant; // For timestamp

@Document(collection = "ratings") // Maps to your MongoDB collection name
public class RatingDocument {

    @Id
    private String id; // MongoDB's primary key (ObjectId)

    private String orderId; // Reference to the custom order ID (UUID)
    private Long userId;    // ID of the customer who submitted the rating
    private Long shopId;    // ID of the shop being rated
    private int rating;     // 1-5 star rating
    private String review;  // Optional text review
    private Instant submissionDate; // When the rating was submitted

    public RatingDocument() {
    }

    public RatingDocument(String orderId, Long userId, Long shopId, int rating, String review, Instant submissionDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.shopId = shopId;
        this.rating = rating;
        this.review = review;
        this.submissionDate = submissionDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }
}
