package com.example.dissertation_backend.solution.DTO;

import java.util.List;

public class ShoppingCartDTO {
  private Integer cartId;
  private List<CartItemDTO> cartItems;

  public ShoppingCartDTO() {
    // Default constructor
  }

  public ShoppingCartDTO(Integer cartId, List<CartItemDTO> cartItems) {
    this.cartId = cartId;
    this.cartItems = cartItems;
  }

  public Integer getCartId() {
    return cartId;
  }

  public void setCartId(Integer cartId) {
    this.cartId = cartId;
  }

  public List<CartItemDTO> getCartItems() {
    return cartItems;
  }

  public void setCartItems(List<CartItemDTO> cartItems) {
    this.cartItems = cartItems;
  }

}
