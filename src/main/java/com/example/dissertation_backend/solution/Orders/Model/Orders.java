package com.example.dissertation_backend.solution.Orders.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Orders {

  public enum Status {
    PENDING,
    DISPATCHED,
    DELIVERED,
    OUT_FOR_DELIVERY,
    CANCELLED,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser userId;

  @Column(name = "quantity")
  private Long quantity;

  @Column(name = "total_price")
  private Double totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @Column(name = "order_date")
  private LocalDateTime orderDateTime;

  @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY
  )
  private Set<OrderDetails> items = new HashSet<>();

  public Orders() {}

  public Orders(
    ApplicationUser userId,
    Long quantity,
    Double totalPrice,
    Status status,
    LocalDateTime orderDateTime
  ) {
    this.userId = userId;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = status;
    this.orderDateTime = orderDateTime;
  }

  public Long getId() {
    return id;
  }

  public ApplicationUser getUserId() {
    return userId;
  }

  public Long getQuantity() {
    return quantity;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public Status getStatus() {
    return status;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserId(ApplicationUser userId) {
    this.userId = userId;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getOrderDateTime() {
    return orderDateTime;
  }

  public void setOrderDateTime(LocalDateTime orderDateTime) {
    this.orderDateTime = orderDateTime;
  }

  public Set<OrderDetails> getItems() {
    return items;
  }

  public void setItems(Set<OrderDetails> items) {
    this.items = items;
  }

  public void addItem(OrderDetails item) {
    items.add(item);
    item.setOrder(this);
  }
}
