package com.example.dissertation_backend.solution.Orders.Model;

import com.example.dissertation_backend.solution.Products.Model.Products;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_detail_id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "order_id", referencedColumnName = "order_id")
  private Orders order;

  @ManyToOne
  @JoinColumn(name = "product_id", referencedColumnName = "product_id")
  @JsonIgnore
  private Products product;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "price_at_order")
  private Double priceAtOrder;

  public OrderDetails() {}

  public OrderDetails(
    Orders order,
    Products product,
    Integer quantity,
    Double priceAtOrder
  ) {
    this.order = order;
    this.product = product;
    this.quantity = quantity;
    this.priceAtOrder = priceAtOrder;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Orders getOrder() {
    return order;
  }

  public void setOrder(Orders order) {
    this.order = order;
  }

  public Products getProduct() {
    return product;
  }

  public void setProduct(Products product) {
    this.product = product;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Double getPriceAtOrder() {
    return priceAtOrder;
  }

  public void setPriceAtOrder(Double priceAtOrder) {
    this.priceAtOrder = priceAtOrder;
  }
}
