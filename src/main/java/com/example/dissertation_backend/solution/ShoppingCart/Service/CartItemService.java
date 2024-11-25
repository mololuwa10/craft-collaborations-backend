package com.example.dissertation_backend.solution.ShoppingCart.Service;

import com.example.dissertation_backend.solution.Exception.CartItemNotFoundException;
import com.example.dissertation_backend.solution.ShoppingCart.Model.CartItem;
import com.example.dissertation_backend.solution.ShoppingCart.Repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

  @Autowired
  private CartItemRepository cartItemRepository;

  public CartItem findById(Integer cartItemId)
    throws CartItemNotFoundException {
    if (cartItemId == null) {
      throw new IllegalArgumentException("Cart item ID must not be null");
    }

    return cartItemRepository
      .findById(cartItemId)
      .orElseThrow(() ->
        new CartItemNotFoundException(
          "Cart item not found with id: " + cartItemId
        )
      );
  }
}
