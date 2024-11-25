package com.example.dissertation_backend.solution.Orders.Controller;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.FullOrderDTO;
import com.example.dissertation_backend.solution.DTO.OrderDTO;
import com.example.dissertation_backend.solution.DTO.ProductSalesDTO;
import com.example.dissertation_backend.solution.Orders.Service.OrderService;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
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
public class OrderController {

  @Autowired
  private OrderService orderService;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/all-orders")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<?> getAllOrders() {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();

    // Check if the user has the ADMIN role
    if (
      authentication
        .getAuthorities()
        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
    ) {
      List<OrderDTO> orders = orderService.findAllOrdersDTO();
      return ResponseEntity.ok(orders);
    } else {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("Access is denied");
    }
  }

  @GetMapping("/users/my-orders")
  public ResponseEntity<?> getCurrentUserOrdersDTO(Principal principal) {
    // Check if principal is null
    if (principal == null) {
      return ResponseEntity
        .badRequest()
        .body("Error: User not authenticated. Please log in.");
    }

    String username = principal.getName();
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new UsernameNotFoundException(
          "User not found with username: " + username
        )
      );

    List<OrderDTO> orders = orderService.findOrdersByUserDTO(user);
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/artisan/orders")
  public ResponseEntity<?> getOrdersByLoggedInArtisan(Principal principal) {
    try {
      List<FullOrderDTO> orders = orderService.findFullOrdersByArtisanId(
        principal
      );
      return ResponseEntity.ok(orders);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  @GetMapping("/top-selling-product")
  public ResponseEntity<?> getTopSellingProduct() {
    Optional<Products> product = orderService.findTopSellingProduct();
    if (product.isPresent()) {
      return ResponseEntity.ok(product.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/product-sales")
  public ResponseEntity<List<ProductSalesDTO>> getProductSales() {
    List<ProductSalesDTO> productSales = orderService.findAllProductSales();
    if (productSales.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(productSales);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOrder(
    @PathVariable Long id,
    Principal principal
  ) {
    ApplicationUser currentUser = userRepository
      .findByUsername(principal.getName())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    orderService.deleteOrder(id, currentUser);

    return ResponseEntity.ok().body("Order deleted successfully");
  }
}
