package com.example.dissertation_backend.solution.Sales;

import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.DTO.ArtisanSalesDTO;
import com.example.dissertation_backend.solution.DTO.ProductSalesDTO;
import com.example.dissertation_backend.solution.Orders.Repository.OrderDetailsRepository;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesDataService {

  @Autowired
  private OrderDetailsRepository orderDetailsRepository;

  public List<ProductSalesDTO> getSalesDataForUser(String username) {
    List<Object[]> results = orderDetailsRepository.findProductSalesAndOrderCountByUsername(
      username
    );
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

  public List<SalesDataDTO> getSalesDataGroupedByDayForArtisan(
    LocalDateTime startDate,
    LocalDateTime endDate,
    String username
  ) {
    return orderDetailsRepository
      .findSalesByDateRangeGroupedByDayForArtisan(startDate, endDate, username)
      .stream()
      .map(result ->
        new SalesDataDTO((LocalDateTime) result[1], (Double) result[0])
      )
      .collect(Collectors.toList());
  }

  public List<ArtisanSalesDTO> getTopArtisans() {
    return orderDetailsRepository
      .findSalesByArtisan()
      .stream()
      .map(result ->
        new ArtisanSalesDTO(
          (ArtisanProfile) result[0],
          (Double) result[1],
          ((Long) result[2]).intValue()
        )
      )
      .collect(Collectors.toList());
  }
}
