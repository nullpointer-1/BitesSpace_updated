package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.OrderService;
import com.example.shopmanagement.dto.OrderStatusUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller // This should be a @Controller, not @RestController, for @MessageMapping
public class OrderWebSocketController {

    @Autowired
    private OrderService orderService;

    /**
     * Handles incoming WebSocket messages for order status updates.
     * The frontend sends messages to "/app/order.updateStatus"
     * Spring's SimpleBroker (or full broker) will route this to this method.
     * @param updateDto The DTO containing orderId, newStatus, and vendorId.
     */
    @MessageMapping("/order.updateStatus") // This is the destination for the client to send messages to
    public void updateOrderStatusViaWs(OrderStatusUpdateDto updateDto) {
        System.out.println("Received order status update via WebSocket: " + updateDto.getOrderId() + " to " + updateDto.getNewStatus());
        try {
            // The service will update the DB and send out further WebSocket notifications
            orderService.updateOrderStatus(updateDto);
        } catch (Exception e) {
            System.err.println("Error updating order status via WebSocket: " + e.getMessage());
            // You might want to send an error message back to the client or log it more robustly
        }
    }
}