package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.document.SupportTicketDocument;
import com.example.shopmanagement.dto.TicketRequestDto;
import com.example.shopmanagement.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:8081") // Ensure your frontend URL is correct
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/raise")
    public ResponseEntity<?> raiseTicket(@RequestBody TicketRequestDto ticketRequest) {
        try {
            if (ticketRequest.getUserId() == null || ticketRequest.getOrderId() == null || ticketRequest.getShopId() == null ||
                ticketRequest.getTicketType() == null || ticketRequest.getSubject() == null || ticketRequest.getDescription() == null ||
                ticketRequest.getSubject().trim().isEmpty() || ticketRequest.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("All ticket fields (type, subject, description, user, order, shop) are required.");
            }
            SupportTicketDocument savedTicket = ticketService.raiseTicket(ticketRequest);
            return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error raising ticket: " + e.getMessage());
        }
    }
    
}
