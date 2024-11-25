package com.example.dissertation_backend.solution.Stripe.Model;

public class CheckoutItem {

  private Integer productId;
  private Integer quantity;

  public CheckoutItem() {}

  public CheckoutItem(Integer productId, Integer quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  // Getters
  public Integer getProductId() {
    return productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  // Setters
  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
