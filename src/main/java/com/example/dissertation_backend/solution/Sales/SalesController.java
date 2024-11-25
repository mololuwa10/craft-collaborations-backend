package com.example.dissertation_backend.solution.Sales;

import com.example.dissertation_backend.solution.DTO.ArtisanSalesDTO;
import com.example.dissertation_backend.solution.DTO.ProductSalesDTO;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
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
public class SalesController {

  @Autowired
  private SalesDataService salesDataService;

  @GetMapping("/my-sales")
  public ResponseEntity<?> getMySalesData(Principal principal) {
    if (principal != null) {
      // Retrieve the username from the Principal object
      String username = principal.getName();
      // Optionally use the Authentication object to get more details if needed
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      if (
        authentication != null &&
        authentication
          .getAuthorities()
          .stream()
          .anyMatch(a ->
            a.getAuthority().equals("ROLE_ARTISAN") ||
            a.getAuthority().equals("ROLE_ADMIN")
          )
      ) {
        List<ProductSalesDTO> salesData = salesDataService.getSalesDataForUser(
          username
        );
        return ResponseEntity.ok(salesData);
      }
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
  }

  @GetMapping("/time-series")
  public ResponseEntity<?> getSalesDataByDate(
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    Principal principal
  ) {
    if (principal != null) {
      // Retrieve the username from the Principal object
      String username = principal.getName();
      // Optionally use the Authentication object to get more details if needed
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      if (
        authentication != null &&
        authentication
          .getAuthorities()
          .stream()
          .anyMatch(a ->
            a.getAuthority().equals("ROLE_ARTISAN") ||
            a.getAuthority().equals("ROLE_ADMIN")
          )
      ) {
        List<SalesDataDTO> salesData = salesDataService.getSalesDataGroupedByDayForArtisan(
          startDate,
          endDate,
          username
        );
        return ResponseEntity.ok(salesData);
      }
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
  }

  @GetMapping("/top-artisans")
  public ResponseEntity<?> getTopArtisans() {
    List<ArtisanSalesDTO> topArtisans = salesDataService.getTopArtisans();
    return ResponseEntity.ok(topArtisans);
  }
}
