package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.OrderResponseDto; // Keep if still used elsewhere for other DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.shopmanagement.document.OrderDocument; // Import OrderDocument

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Changed parameter type from OrderResponseDto to OrderDocument
    public void sendNewOrderNotification(String vendorUsername, OrderDocument order) {
        // Send a message to a specific topic for this vendor's orders
        // The frontend (OrderQueue.tsx) will subscribe to /topic/orders/{vendorUsername}
        messagingTemplate.convertAndSend("/topic/orders/" + vendorUsername, order);
        System.out.println("Sent new order notification for order " + order.getOrderId() + " to vendor " + vendorUsername);
    }

    // Method to notify customers about order status changes
    public void sendOrderStatusUpdateToCustomer(String customerEmail, OrderDocument updatedOrder) {
        // The customer's tracking page will subscribe to /topic/orders/{orderId}
        // For simplicity, let's also send to customerEmail topic for broader updates,
        // or just rely on the orderId topic if the customer page is order-specific.
        // Using orderId topic for customer tracking page is more direct for specific order updates.
        messagingTemplate.convertAndSend("/topic/orders/" + updatedOrder.getOrderId(), updatedOrder);
        System.out.println("Sent order status update for order " + updatedOrder.getOrderId() + " to customer: " + updatedOrder.getStatus());
    }

    // Method to notify vendor again for internal dashboard updates (e.g., if multiple staff manage orders)
    public void sendOrderStatusUpdateToVendor(String vendorUsername, OrderDocument updatedOrder) {
        // This is good if the OrderQueue in the vendor dashboard needs to react to other vendors' actions
        // or if you want confirmation of your own action via WebSocket.
        // We'll send it back to the same topic the vendor listens on for new orders, so OrderQueue can update its list.
        messagingTemplate.convertAndSend("/topic/orders/" + vendorUsername, updatedOrder);
        System.out.println("Sent order status update for order " + updatedOrder.getOrderId() + " to vendor " + vendorUsername + ": " + updatedOrder.getStatus());
    }

    public void sendGeneralNotification(String vendorUsername, String message) {
        messagingTemplate.convertAndSend("/topic/notifications/" + vendorUsername, new NotificationMessage(message));
    }

    private static class NotificationMessage {
        private String message;

        public NotificationMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}