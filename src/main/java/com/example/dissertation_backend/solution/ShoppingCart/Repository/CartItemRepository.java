package com.example.dissertation_backend.solution.ShoppingCart.Repository;

// import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.ShoppingCart.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
  @Transactional
  void deleteByShoppingCart_User_UserId(Integer userId);
}
