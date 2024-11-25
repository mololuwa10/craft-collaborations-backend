package com.example.dissertation_backend.solution.Exception;

public class CartItemNotFoundException extends RuntimeException {

  public CartItemNotFoundException(String message) {
    super(message);
  }
}
