package com.example.dissertation_backend.solution.ShoppingCart.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cart_id")
  private Integer cartId;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser user;

  @Column(name = "date_created")
  private LocalDateTime dateCreated;

  @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
  @JsonManagedReference
  private Set<CartItem> cartItems = new HashSet<>();

  // Constructors
  public ShoppingCart() {}

  public ShoppingCart(
    ApplicationUser user,
    LocalDateTime dateCreated,
    Set<CartItem> cartItems
  ) {
    this.user = user;
    this.dateCreated = LocalDateTime.now();
    this.cartItems = cartItems;
  }

  // Getters and Setters
  public Integer getCartId() {
    return cartId;
  }

  public void setCartId(Integer cartId) {
    this.cartId = cartId;
  }

  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(LocalDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Set<CartItem> getCartItems() {
    return cartItems;
  }

  public void setCartItems(Set<CartItem> cartItems) {
    this.cartItems = cartItems;
  }
}
