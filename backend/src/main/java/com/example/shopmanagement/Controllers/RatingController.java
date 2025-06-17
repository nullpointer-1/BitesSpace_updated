package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.document.RatingDocument;
import com.example.shopmanagement.dto.RatingRequestDto;
import com.example.shopmanagement.Service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:8081") // Ensure your frontend URL is correct
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(@RequestBody RatingRequestDto ratingRequest) {
        try {
            if (ratingRequest.getRating() < 1 || ratingRequest.getRating() > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
            }
            if (ratingRequest.getUserId() == null || ratingRequest.getOrderId() == null || ratingRequest.getShopId() == null) {
                return ResponseEntity.badRequest().body("User ID, Order ID, and Shop ID are required for rating submission.");
            }
            RatingDocument savedRating = ratingService.submitRating(ratingRequest);
            return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting rating: " + e.getMessage());
        }
    }
}
