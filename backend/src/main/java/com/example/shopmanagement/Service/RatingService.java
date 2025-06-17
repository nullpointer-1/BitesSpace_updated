package com.example.shopmanagement.Service;

import com.example.shopmanagement.document.RatingDocument;
import com.example.shopmanagement.dto.RatingRequestDto;
import com.example.shopmanagement.repository.RatingDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List; // Import List

@Service
public class RatingService {

    @Autowired
    private RatingDocumentRepository ratingDocumentRepository;

    @Transactional
    public RatingDocument submitRating(RatingRequestDto ratingRequest) {
        // Basic validation
        if (ratingRequest.getRating() < 1 || ratingRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (ratingRequest.getUserId() == null || ratingRequest.getOrderId() == null || ratingRequest.getShopId() == null) {
            throw new IllegalArgumentException("User ID, Order ID, and Shop ID are required for rating submission.");
        }

        // --- ENFORCE SINGLE RATING PER ORDER PER USER, ALLOWING OVERWRITE ---
        // Fetch all existing ratings for this order and user
        List<RatingDocument> existingRatings = ratingDocumentRepository.findByOrderIdAndUserId(
            ratingRequest.getOrderId(),
            ratingRequest.getUserId()
        );

        if (!existingRatings.isEmpty()) {
            // If there are existing ratings, delete them all to ensure uniqueness.
            // This handles cases where duplicates might have been created previously.
            System.out.println("Found " + existingRatings.size() + " existing ratings for order " +
                                ratingRequest.getOrderId() + " by user " + ratingRequest.getUserId() +
                                ". Deleting them to replace with new rating.");
            ratingDocumentRepository.deleteAll(existingRatings);
        }

        // Create and save the new (or updated/overwritten) rating
        RatingDocument newRating = new RatingDocument(
                ratingRequest.getOrderId(),
                ratingRequest.getUserId(),
                ratingRequest.getShopId(),
                ratingRequest.getRating(),
                ratingRequest.getReview(),
                Instant.now() // Always set current timestamp for new/updated rating
        );
        return ratingDocumentRepository.save(newRating);
    }
}
