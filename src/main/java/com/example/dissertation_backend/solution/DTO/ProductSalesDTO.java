package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Products.Model.Products;

public class ProductSalesDTO {

  private Products product;
  private int totalQuantitySold;
  private int totalOrders;

  public ProductSalesDTO() {
    // Default constructor
    super();
  }

  public ProductSalesDTO(
    Products product,
    int totalQuantitySold,
    int totalOrders
  ) {
    this.product = product;
    this.totalQuantitySold = totalQuantitySold;
    this.totalOrders = totalOrders;
  }

  public Products getProduct() {
    return product;
  }

  public void setProduct(Products product) {
    this.product = product;
  }

  public int getTotalQuantitySold() {
    return totalQuantitySold;
  }

  public void setTotalQuantitySold(int totalQuantitySold) {
    this.totalQuantitySold = totalQuantitySold;
  }

  public int getTotalOrders() {
    return totalOrders;
  }

  public void setTotalOrders(int totalOrders) {
    this.totalOrders = totalOrders;
  }
}
