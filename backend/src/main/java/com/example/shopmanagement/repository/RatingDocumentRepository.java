package com.example.shopmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.shopmanagement.document.RatingDocument;

@Repository
public interface RatingDocumentRepository extends MongoRepository<RatingDocument, String> {
	 List<RatingDocument> findByOrderIdAndUserId(String orderId, Long userId);
}
