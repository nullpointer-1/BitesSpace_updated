package com.example.shopmanagement.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.shopmanagement.dto.OrderRequestDto;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void sendContactEmail(String name, String email, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email); // where you receive
        mailMessage.setSubject("Congratulations , your credentials in our cafeteria got generated!!");

      
        mailMessage.setText(message);
        mailMessage.setFrom("sanjeevsaisasank9@gmail.com");
        
        System.out.println(message +" ,"+ name+" , "+email);

        mailSender.send(mailMessage);
    }
    @Async
    public void sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Set to true if you want to send HTML email
            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }
    public void sendOrderConfirmationEmail(String toEmail, String orderId, OrderRequestDto orderDetails) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your_email@example.com"); // Must match spring.mail.username
            message.setTo(toEmail);
            message.setSubject("Food Stall Order Confirmation - Order #" + orderId);

            // Build the email text
            StringBuilder emailText = new StringBuilder();
            emailText.append("Dear Customer,\n\n");
            emailText.append("Your order #" + orderId + " has been successfully placed!\n\n");

            // Add shop details if available (you might need to fetch shop name/address here or pass it in DTO)
            // For now, let's use the currentShopId from OrderRequestDto and assume you can get shop name/address if needed.
            // A better way would be to fetch shop details here using shopRepository if you don't store full shop details in order.
            emailText.append("Shop: " + (orderDetails.getShopId() != null ? "Shop ID: " + orderDetails.getShopId() : "N/A") + "\n");
            // You can fetch shop name/address here using shopRepository.findById(orderDetails.getShopId())
            // For simplicity, let's just use ID for now or assume shopName is passed in OrderRequestDto (if you modified it).

            emailText.append("Items Ordered:\n");
            orderDetails.getItems().forEach(item -> {
                // You would need to fetch product name here from your database using item.getProductId()
                // Or modify OrderRequestDto to include product name
                emailText.append(String.format("- Product ID: %d, Quantity: %d, Price: ₹%.2f\n",
                                                item.getProductId(), item.getQuantity(), item.getPriceAtOrder()));
            });

            emailText.append(String.format("\nTotal Amount: ₹%.2f\n", orderDetails.getTotalAmount()));
            emailText.append(String.format("Estimated Pickup Time: %s\n\n", 
                orderDetails.getEstimatedPickupTime().atZone(java.time.ZoneId.of("Asia/Kolkata")) // Use appropriate timezone
                                .format(DateTimeFormatter.ofPattern("hh:mm a, dd MMMM yyyy")))); // Format time

            emailText.append("Thank you for your order!\n\n");
            emailText.append("Food Stall App Team");

            message.setText(emailText.toString());

            mailSender.send(message);
            System.out.println("Order confirmation email sent to: " + toEmail); // For logging
        } catch (MailException e) {
            System.err.println("Error sending order confirmation email to " + toEmail + ": " + e.getMessage());
            // Log the exception, but don't prevent the order from being placed
            // You might want to store failed email attempts for retry mechanisms
        }
    }
   

    // NEW METHOD: Dummy implementation for order status update email
    public void sendOrderStatusUpdateEmail(String customerEmail, String orderId, String newStatus) {
        System.out.println("Sending order status update email to: " + customerEmail);
        System.out.println("Order ID: " + orderId + " - New Status: " + newStatus);
        // In a real application, you would send a more detailed email about the status change
    }

}