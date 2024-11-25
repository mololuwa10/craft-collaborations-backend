package com.example.dissertation_backend.solution.DTO;

public class AddToCartResponse {

  private String message;
  private CartItemDTO cartItemDTO;

  public AddToCartResponse(String message, CartItemDTO cartItemDTO) {
    this.message = message;
    this.cartItemDTO = cartItemDTO;
  }

  // Getters and Setters
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public CartItemDTO getCartItem() {
    return cartItemDTO;
  }

  public void setCartItem(CartItemDTO cartItemDTO) {
    this.cartItemDTO = cartItemDTO;
  }
}
