package com.example.dissertation_backend.solution.Orders.Service;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.FullOrderDTO;
import com.example.dissertation_backend.solution.DTO.OrderDTO;
import com.example.dissertation_backend.solution.DTO.OrderDetailsDTO;
import com.example.dissertation_backend.solution.DTO.ProductSalesDTO;
import com.example.dissertation_backend.solution.Orders.Model.OrderDetails;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import com.example.dissertation_backend.solution.Orders.Repository.OrderDetailsRepository;
import com.example.dissertation_backend.solution.Orders.Repository.OrdersRepository;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.security.Principal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private OrderDetailsRepository orderDetailsRepository;

  @Autowired
  private UserRepository userRepository;

  public Orders saveOrder(Orders orders) {
    if (orders == null) {
      return null;
    }

    return ordersRepository.save(orders);
  }

  public Optional<Orders> getOrderById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return ordersRepository.findById(id);
  }

  public List<OrderDTO> findAllOrdersDTO() {
    List<Orders> allOrders = ordersRepository.findAll();

    // Convert each Orders entity to OrderDTO
    return allOrders
      .stream()
      .map(OrderDTO::fromEntity)
      .collect(Collectors.toList());
  }

  public List<OrderDTO> findOrdersByUserDTO(ApplicationUser user) {
    List<Orders> orders = ordersRepository.findByUserId(user);
    return orders
      .stream()
      .map(OrderDTO::fromEntity)
      .collect(Collectors.toList());
  }

  public List<FullOrderDTO> findFullOrdersByArtisanId(Principal principal) {
    // Retrieve the username from the Principal object
    String username = principal.getName();

    // Find the user by username
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new UsernameNotFoundException(
          "User not found with username: " + username
        )
      );

    // Check if the user has an artisan profile
    if (user.getArtisanProfile() == null) {
      throw new RuntimeException("Logged in user is not an artisan");
    }

    Integer artisanId = user.getArtisanProfile().getArtisanId();

    // Use the artisanId to find order details
    List<OrderDetails> orderDetailsList = orderDetailsRepository.findByProduct_Artisan_ArtisanId(
      artisanId
    );
    return orderDetailsList
      .stream()
      .collect(Collectors.groupingBy(OrderDetails::getOrder))
      .entrySet()
      .stream()
      .map(entry -> {
        OrderDTO orderDTO = OrderDTO.fromEntity(entry.getKey());
        List<OrderDetailsDTO> detailsDTOs = entry
          .getValue()
          .stream()
          .map(OrderDetailsDTO::fromEntity)
          .collect(Collectors.toList());
        return new FullOrderDTO(orderDTO, detailsDTOs);
      })
      .collect(Collectors.toList());
  }

  public Optional<Products> findTopSellingProduct() {
    List<Object[]> productSales = orderDetailsRepository.findProductSales();
    return productSales
      .stream()
      .max((a, b) ->
        Integer.compare(((Long) a[1]).intValue(), ((Long) b[1]).intValue())
      )
      .map(result -> (Products) result[0]);
  }

  public List<ProductSalesDTO> findAllProductSales() {
    List<Object[]> results = orderDetailsRepository.findProductSalesAndOrderCount();
    return results
      .stream()
      .map(result ->
        new ProductSalesDTO(
          (Products) result[0],
          ((Long) result[1]).intValue(),
          ((Long) result[2]).intValue()
        )
      )
      .collect(Collectors.toList());
  }

  public void deleteOrder(Long orderId, ApplicationUser currentUser) {
    // Check for non-null orderId and currentUser
    if (orderId == null || currentUser == null) {
      throw new IllegalArgumentException("Order ID and User cannot be null");
    }

    // Find the order by ID and User
    Orders order = ordersRepository
      .findByIdAndUserId(orderId, currentUser)
      .orElseThrow(() ->
        new IllegalStateException("No matching order found for this user")
      );

    // Perform the delete operation
    ordersRepository.delete(order);
  }

  @Scheduled(fixedDelayString = "PT1H")
  public void updateOrderStatuses() {
    List<Orders> allOrders = ordersRepository.findAll();
    LocalDateTime now = LocalDateTime.now();

    for (Orders order : allOrders) {
      LocalDateTime orderTime = order.getOrderDateTime();
      Duration duration = Duration.between(orderTime, now);

      if (
        duration.toHours() <= 6 && order.getStatus() == Orders.Status.PENDING
      ) {
        continue;
      } else if (
        duration.toHours() <= 12 &&
        order.getStatus() != Orders.Status.DISPATCHED
      ) {
        order.setStatus(Orders.Status.DISPATCHED);
      } else if (
        duration.toHours() <= 18 &&
        order.getStatus() != Orders.Status.OUT_FOR_DELIVERY
      ) {
        order.setStatus(Orders.Status.OUT_FOR_DELIVERY);
      } else if (
        duration.toHours() >= 20 && order.getStatus() != Orders.Status.DELIVERED
      ) {
        order.setStatus(Orders.Status.DELIVERED);
      }

      ordersRepository.save(order);
    }
  }

  public boolean hasUserOrderedAndReceivedProduct(
    ApplicationUser user,
    Products product
  ) {
    List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrder_UserIdAndProductAndOrder_Status(
      user,
      product,
      Orders.Status.DELIVERED
    );

    return !orderDetailsList.isEmpty();
  }
}
