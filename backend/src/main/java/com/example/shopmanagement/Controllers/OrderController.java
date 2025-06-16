package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.dto.OrderRequestDto;
import com.example.shopmanagement.dto.OrderResponseDto;
import com.example.shopmanagement.dto.OrderStatusUpdateDto; // This import is correct
import com.example.shopmanagement.Service.OrderService;
import com.example.shopmanagement.document.OrderDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController // Keep @RestController for REST endpoints
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:8081")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // This nested DTO class is correct as you have it
    static class OrderStatusUpdateRequest {
        private String newStatus;
        private Long vendorId; // Keep as Long if your vendor IDs are numeric

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public Long getVendorId() {
            return vendorId;
        }

        public void setVendorId(Long vendorId) {
            this.vendorId = vendorId;
        }
    }

    // --- CRITICAL FIX HERE ---
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDocument> updateOrderStatusHttp(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request) {
        try {
            OrderStatusUpdateDto updateDto = new OrderStatusUpdateDto();
            updateDto.setOrderId(orderId);
            updateDto.setNewStatus(request.getNewStatus());
            updateDto.setVendorId(request.getVendorId());

            // Get the updated OrderDocument from the service
            OrderDocument updatedOrderDocument = orderService.updateOrderStatus(updateDto); // <-- SERVICE SHOULD RETURN THE UPDATED DOC

            // Return the updated OrderDocument in the response body
            return ResponseEntity.ok(updatedOrderDocument); // <-- CHANGE: Return the document
        } catch (RuntimeException e) { // Catch RuntimeException specifically if thrown by service
            System.err.println("Error updating order status via HTTP: " + e.getMessage());
            // It's good practice to send an error message in the body for 404/500
            if (e.getMessage().contains("Order not found")) { // Check for specific message
                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                      .body(null); // Or return an error DTO: .body(new ErrorDto(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null); // Or return an error DTO
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("Unexpected error updating order status via HTTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }


    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            OrderResponseDto response = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new OrderResponseDto(null, "Error placing order: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDocument> getOrderById(@PathVariable String orderId) {
        try {
            Optional<OrderDocument> order = orderService.findByOrderId(orderId);
            if (order.isPresent()) {
                return new ResponseEntity<>(order.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<OrderDocument>> getOrdersByVendorId(@PathVariable Long vendorId) {
        try {
            List<OrderDocument> orders = orderService.getOrdersByVendorId(vendorId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // NEW ENDPOINT: Get orders by customer (user) ID
    @GetMapping("/user/{userId}") // Added a new endpoint to fetch orders by user ID
    public ResponseEntity<List<OrderDocument>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<OrderDocument> orders = orderService.getOrdersByUserId(userId);
            // Optional: Sort orders by date descending before returning
         // Corrected line
            orders.sort((o1, o2) -> o2.getOrderTime().compareTo(o1.getOrderTime()));
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This section was a comment before, so no change is necessary if it's just a comment.
    // If you intend to use @MessageMapping here, remember to create a separate @Controller class for it.
    // @Controller
    // public class OrderWebSocketController {
    //    @Autowired
    //    private OrderService orderService;
    //
    //    @MessageMapping("/order.updateStatus")
    //    public void updateOrderStatusViaWs(@Payload OrderStatusUpdateDto updateDto) {
    //        orderService.updateOrderStatus(updateDto);
    //    }
    // }
}