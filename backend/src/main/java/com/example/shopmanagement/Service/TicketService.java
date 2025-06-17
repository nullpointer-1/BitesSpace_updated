package com.example.shopmanagement.Service;

import com.example.shopmanagement.document.SupportTicketDocument;
import com.example.shopmanagement.dto.TicketRequestDto;
import com.example.shopmanagement.repository.SupportTicketDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID; // For generating unique ticket IDs

@Service
public class TicketService {

    @Autowired
    private SupportTicketDocumentRepository supportTicketDocumentRepository;

    @Transactional
    public SupportTicketDocument raiseTicket(TicketRequestDto ticketRequest) {
        // You might want to add validation here, e.g., check if orderId exists.

        String generatedTicketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SupportTicketDocument ticket = new SupportTicketDocument(
                generatedTicketId,
                ticketRequest.getOrderId(),
                ticketRequest.getUserId(),
                ticketRequest.getShopId(),
                ticketRequest.getTicketType(),
                ticketRequest.getSubject(),
                ticketRequest.getDescription(),
                "OPEN", // Initial status
                Instant.now() // Set submission date on the backend
        );
        return supportTicketDocumentRepository.save(ticket);
    }
}
