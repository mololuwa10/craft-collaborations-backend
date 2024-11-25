package com.example.dissertation_backend.solution.DTO;

import java.util.List;

public class FullOrderDTO {

  private OrderDTO order;
  private List<OrderDetailsDTO> orderDetails;

  // Constructors
  public FullOrderDTO() {}

  public FullOrderDTO(OrderDTO order, List<OrderDetailsDTO> orderDetails) {
    this.order = order;
    this.orderDetails = orderDetails;
  }

  // Getters and Setters
  public OrderDTO getOrder() {
    return order;
  }

  public void setOrder(OrderDTO order) {
    this.order = order;
  }

  public List<OrderDetailsDTO> getOrderDetails() {
    return orderDetails;
  }

  public void setOrderDetails(List<OrderDetailsDTO> orderDetails) {
    this.orderDetails = orderDetails;
  }
}
