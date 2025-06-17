package com.example.shopmanagement.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant; // For timestamps

@Document(collection = "support_tickets") // Maps to your MongoDB collection name
public class SupportTicketDocument {

    @Id
    private String id; // MongoDB's primary key (ObjectId)

    private String ticketId; // Your custom generated ticket ID (e.g., TKT123456)
    private String orderId; // Reference to the custom order ID (UUID)
    private Long userId; // ID of the customer who raised the ticket
    private Long shopId; // ID of the shop the ticket is related to
    private String ticketType; // E.g., "food-quality", "missing-items", "other"
    private String subject;
    private String description;
    private String status; // E.g., "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"
    private Instant submissionDate;
    private Instant resolutionDate; // Optional: when the ticket was resolved
    private String resolvedBy; // Optional: who resolved the ticket (e.g., admin username)

    public SupportTicketDocument() {
    }

    public SupportTicketDocument(String ticketId, String orderId, Long userId, Long shopId, String ticketType, String subject, String description, String status, Instant submissionDate) {
        this.ticketId = ticketId;
        this.orderId = orderId;
        this.userId = userId;
        this.shopId = shopId;
        this.ticketType = ticketType;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.submissionDate = submissionDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Instant getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Instant resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
}
