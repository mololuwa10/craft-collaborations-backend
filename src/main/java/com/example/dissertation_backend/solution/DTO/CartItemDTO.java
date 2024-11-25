package com.example.dissertation_backend.solution.DTO;

public class CartItemDTO {

  private Integer cartItemId;
  private ProductDTO product;
  private Integer quantity;
  private Double totalProductPrice;

  public CartItemDTO() {
    // Default constructor
  }

  public CartItemDTO(
    Integer cartItemId,
    ProductDTO product,
    Integer quantity,
    Double totalProductPrice
  ) {
    this.cartItemId = cartItemId;
    this.product = product;
    this.quantity = quantity;
    this.totalProductPrice = totalProductPrice;
  }

  public Integer getCartItemId() {
    return cartItemId;
  }

  public void setCartItemId(Integer cartItemId) {
    this.cartItemId = cartItemId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public ProductDTO getProduct() {
    return product;
  }

  public void setProduct(ProductDTO product) {
    this.product = product;
  }

  public Double getTotalProductPrice() {
    return totalProductPrice;
  }

  public void setTotalProductPrice(Double totalProductPrice) {
    this.totalProductPrice = totalProductPrice;
  }
}
