package com.example.dissertation_backend.solution.Stripe.Controller;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.EmailVerification.EmailService;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import com.example.dissertation_backend.solution.Orders.Repository.OrdersRepository;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Repository.ProductRepository;
import com.example.dissertation_backend.solution.Stripe.Model.CheckoutItem;
import com.example.dissertation_backend.solution.Stripe.Model.CheckoutPayment;
import com.example.dissertation_backend.solution.Stripe.Services.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class StripeController {

  @Autowired
  private CheckoutService checkoutService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private OrdersRepository ordersRepository;

  @PostMapping("/create-checkout-session")
  public ResponseEntity<?> createCheckoutSession(
    @Valid @RequestBody CheckoutPayment checkoutPayment
  ) {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String currentUsername = authentication.getName();
      ApplicationUser user = userRepository
        .findByUsername(currentUsername)
        .orElseThrow(() ->
          new UsernameNotFoundException(
            "User not found with username: " + currentUsername
          )
        );

      if (!user.getEnabled()) {
        return ResponseEntity
          .status(HttpStatus.FORBIDDEN)
          .body("User is not verified and cannot checkout.");
      }
      @SuppressWarnings("unused")
      long totalPriceInCents = 0;

      // Using checkoutItems from CheckoutPayment for validations and updates
      List<CheckoutItem> checkoutItems = checkoutPayment.getItems();
      SessionCreateParams.Builder paramsBuilder = SessionCreateParams
        .builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(checkoutPayment.getSuccessUrl())
        .setCancelUrl(checkoutPayment.getCancelUrl());

      // Dynamically adding line items based on checkoutItems
      for (CheckoutItem item : checkoutItems) {
        Products product = productRepository
          .findById(item.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found"));

        double priceToUse = (
            product.getProductDiscount() != null &&
            product.getProductDiscount() > 0
          )
          ? product.getProductDiscount()
          : product.getProductPrice();

        long priceInCents = Math.round(priceToUse * 100);
        totalPriceInCents += priceInCents * item.getQuantity();

        // checkoutPayment.setAmount(totalPriceInCents);
        // checkoutPayment.setQuantity(item.getQuantity());

        paramsBuilder.addLineItem(
          SessionCreateParams.LineItem
            .builder()
            .setPriceData(
              SessionCreateParams.LineItem.PriceData
                .builder()
                .setCurrency(checkoutPayment.getCurrency())
                .setUnitAmount(priceInCents)
                .setProductData(
                  SessionCreateParams.LineItem.PriceData.ProductData
                    .builder()
                    .setName(product.getProductName())
                    .build()
                )
                .build()
            )
            .setQuantity(Long.valueOf(item.getQuantity()))
            .build()
        );
      }

      // Performing checkout validations and updates
      ResponseEntity<?> checkoutResponse = checkoutService.checkout(
        checkoutItems
      );

      if (checkoutResponse.getStatusCode().is2xxSuccessful()) {
        @SuppressWarnings("unchecked")
        Map<String, Object> checkoutInfo = (Map<String, Object>) checkoutResponse.getBody();
        // Extracting necessary details to send the email
        @SuppressWarnings("null")
        Long orderId = (Long) checkoutInfo.get("orderId");
        Orders order = ordersRepository
          .findById(orderId)
          .orElseThrow(() ->
            new RuntimeException("Order not found with ID: " + orderId)
          );

        // Calling EmailService to send an order confirmation email
        emailService.sendOrderConfirmationEmail(order);

        // Creating the Stripe session
        Session session = Session.create(paramsBuilder.build());
        Map<String, String> stripeResponse = new HashMap<>();
        stripeResponse.put("url", session.getUrl());

        stripeResponse.put("orderId", String.valueOf(orderId));

        return ResponseEntity.ok(stripeResponse);
      } else {
        return checkoutResponse;
      }
      // return ResponseEntity.ok(response);
    } catch (StripeException e) {
      e.printStackTrace();
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Failed to create checkout session");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An error occurred while processing your order");
    }
  }
}
