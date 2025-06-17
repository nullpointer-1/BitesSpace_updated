package com.example.shopmanagement.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.shopmanagement.document.SupportTicketDocument;

@Repository
public interface SupportTicketDocumentRepository extends MongoRepository<SupportTicketDocument, String> {
	 Optional<SupportTicketDocument> findByTicketId(String ticketId);
}
