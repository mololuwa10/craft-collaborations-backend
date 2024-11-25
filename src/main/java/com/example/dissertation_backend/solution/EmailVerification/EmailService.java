package com.example.dissertation_backend.solution.EmailVerification;

import com.example.dissertation_backend.solution.Orders.Model.OrderDetails;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public EmailService(
    JavaMailSender mailSender,
    SpringTemplateEngine templateEngine
  ) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  // Order Confirmation email
  public void sendOrderConfirmationEmail(Orders order) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "EEEE, MMMM d, yyyy"
    );
    String formattedDate = order.getOrderDateTime().format(formatter);
    Context context = new Context();
    context.setVariable("order", order);
    context.setVariable("order.id", order.getId());
    context.setVariable("order.totalPrice", order.getTotalPrice());
    context.setVariable("deliveryDate", formattedDate);

    // Add each item of the order
    List<Map<String, Object>> items = new ArrayList<>();
    for (OrderDetails orderDetail : order.getItems()) {
      Map<String, Object> itemMap = new HashMap<>();
      itemMap.put("productName", orderDetail.getProduct().getProductName());
      itemMap.put("quantity", orderDetail.getQuantity());
      itemMap.put("price", orderDetail.getPriceAtOrder());
      itemMap.put(
        "imageUrl",
        orderDetail.getProduct().getImages().iterator().next().getImageUrl()
      ); // To get just one image
      items.add(itemMap);
    }

    context.setVariable("items", items);

    String body = templateEngine.process("orderConfirmation", context);

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

    try {
      helper.setTo(order.getUserId().getUser_email());
      helper.setSubject("Crafts Collaboration Order Confirmation");
      helper.setText(body, true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
