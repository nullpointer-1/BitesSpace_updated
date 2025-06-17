package com.example.shopmanagement.dto;

//No need for Instant here, as submissionDate is set on backend
public class RatingRequestDto {
 private String orderId;
 private Long userId;
 private Long shopId;
 private int rating;
 private String review;

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
}
