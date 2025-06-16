package com.example.shopmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.shopmanagement.document.OrderDocument;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDocumentRepository extends MongoRepository<OrderDocument, String>{

  
	 Optional<OrderDocument> findByOrderId(String orderId);
	    // You can add more methods here, e.g., findByShopId, findByVendorId, findByCustomerEmail
	    List<OrderDocument> findByCustomerEmail(String customerEmail);
	    List<OrderDocument> findByVendorId(Long vendorId);
	    // NEW METHOD: Find orders by userId
	    List<OrderDocument> findByUserId(Long userId);
	   
}