package com.example.dissertation_backend.solution.DTO;

import java.util.List;

public class AddToCartRequest {

  private Integer userId;
  private Integer quantity;
  private List<Integer> attributeIds;

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public List<Integer> getAttributeIds() {
    return attributeIds;
  }

  public void setAttributeIds(List<Integer> attributeIds) {
    this.attributeIds = attributeIds;
  }
}
