package com.example.dissertation_backend.solution.Stripe.Services;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.Orders.Model.OrderDetails;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import com.example.dissertation_backend.solution.Orders.Repository.OrdersRepository;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Repository.ProductRepository;
import com.example.dissertation_backend.solution.ShoppingCart.Repository.CartItemRepository;
import com.example.dissertation_backend.solution.Stripe.Model.CheckoutItem;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CartItemRepository cartItemRepository;

  @Transactional
  public ResponseEntity<?> checkout(List<CheckoutItem> checkoutItems) {
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
        .body("User is not verified and cannot checkout an item");
    }

    Orders order = new Orders();
    order.setUserId(user);
    order.setStatus(Orders.Status.PENDING);
    order.setOrderDateTime(LocalDateTime.now());
    order.setItems(new HashSet<>());

    double totalPrice = 0.0;
    for (CheckoutItem item : checkoutItems) {
      Products product = productRepository
        .findById(item.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found"));
      if (product.getProductStockQuantity() < item.getQuantity()) {
        throw new RuntimeException(
          "Insufficient stock for product " + product.getProductName()
        );
      }

      double priceToUse = (
          product.getProductDiscount() != null &&
          product.getProductDiscount() > 0
        )
        ? product.getProductDiscount()
        : product.getProductPrice();

      OrderDetails orderDetails = new OrderDetails();
      orderDetails.setOrder(order);
      orderDetails.setProduct(product);
      orderDetails.setQuantity(item.getQuantity());
      orderDetails.setPriceAtOrder(priceToUse);
      order.addItem(orderDetails);

      product.setProductStockQuantity(
        product.getProductStockQuantity() - item.getQuantity()
      );
      productRepository.save(product);

      totalPrice += priceToUse * item.getQuantity();
    }

    double roundedTotalPrice = Double.parseDouble(
      String.format("%.2f", totalPrice)
    );

    order.setTotalPrice(roundedTotalPrice); // Set the total price of the order
    order.setQuantity(calculateTotalQuantity(checkoutItems)); // Set the total quantity of the order
    ordersRepository.save(order); // Save the order and its items due to cascade

    cartItemRepository.deleteByShoppingCart_User_UserId(user.getUserId());
    LocalDateTime estimatedDeliveryDate = order.getOrderDateTime().plusDays(3);

    // Collect order item details
    List<Map<String, Object>> itemDetailsList = new ArrayList<>();
    for (OrderDetails item : order.getItems()) {
      Map<String, Object> itemDetails = new HashMap<>();
      itemDetails.put("productName", item.getProduct().getProductName());
      itemDetails.put("quantity", item.getQuantity());
      itemDetails.put("price", item.getPriceAtOrder());
      itemDetails.put(
        "imageUrl",
        item.getProduct().getImages().iterator().next().getImageUrl()
      );
      // Include any other relevant product details
      itemDetailsList.add(itemDetails);
    }

    // Prepare the response map
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Checkout successful.");
    response.put("orderId", order.getId());
    response.put("totalPrice", order.getTotalPrice());
    response.put("estimatedDeliveryDate", estimatedDeliveryDate);
    response.put("orderItems", itemDetailsList);

    return ResponseEntity.ok(response);
  }

  private long calculateTotalQuantity(List<CheckoutItem> checkoutItems) {
    return checkoutItems.stream().mapToLong(CheckoutItem::getQuantity).sum();
  }
}
