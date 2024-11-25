package com.example.dissertation_backend.solution.ShoppingCart.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.ShoppingCart.Model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository
  extends JpaRepository<ShoppingCart, Integer> {
  // Custom methods if necessary
  Optional<ShoppingCart> findByUser(ApplicationUser user);
}
