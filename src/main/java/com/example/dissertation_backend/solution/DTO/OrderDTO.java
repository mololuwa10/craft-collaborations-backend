package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Orders.Model.Orders;
import com.example.dissertation_backend.solution.Orders.Model.Orders.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {

  private Long id;
  private ApplicationUserDTO user;
  private Long quantity;
  private Double totalPrice;
  private Status status;
  private LocalDateTime orderDateTime;
  private List<OrderDetailsDTO> items;

  // Constructors, Getters, and Setters

  public OrderDTO() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ApplicationUserDTO getUser() {
    return user;
  }

  public void setUser(ApplicationUserDTO user) {
    this.user = user;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getOrderDateTime() {
    return orderDateTime;
  }

  public void setOrderDateTime(LocalDateTime orderDateTime) {
    this.orderDateTime = orderDateTime;
  }

  public List<OrderDetailsDTO> getItems() {
    return items;
  }

  public void setItems(List<OrderDetailsDTO> items) {
    this.items = items;
  }

  // Converts an Orders entity to an OrderDTO
  public static OrderDTO fromEntity(Orders order) {
    OrderDTO dto = new OrderDTO();
    dto.setId(order.getId());
    dto.setUser(convertApplicationUserDTO(order.getUserId()));
    dto.setQuantity(order.getQuantity());
    dto.setTotalPrice(order.getTotalPrice());
    dto.setStatus(order.getStatus());
    dto.setOrderDateTime(order.getOrderDateTime());
    dto.setItems(
      order
        .getItems()
        .stream()
        .map(OrderDetailsDTO::fromEntity)
        .collect(Collectors.toList())
    );

    return dto;
  }

  private static ApplicationUserDTO convertApplicationUserDTO(
    ApplicationUser applicationUser
  ) {
    ApplicationUserDTO dto = new ApplicationUserDTO();
    dto.setUserId(applicationUser.getUserId());
    dto.setFirstname(applicationUser.getFirstname());
    dto.setLastname(applicationUser.getLastname());
    dto.setUsername(applicationUser.getUsername());
    dto.setPassword(applicationUser.getPassword());
    dto.setUser_email(applicationUser.getUser_email());
    dto.setBankAccountNo(applicationUser.getBankAccountNo());
    dto.setBankSortCode(applicationUser.getBankSortCode());
    dto.setContactTelephone(applicationUser.getContactTelephone());
    dto.setContactAddress(applicationUser.getContactAddress());
    dto.setAuthorities(applicationUser.getAuthorities());
    dto.setDateJoined(applicationUser.getDateJoined());

    return dto;
  }
}
