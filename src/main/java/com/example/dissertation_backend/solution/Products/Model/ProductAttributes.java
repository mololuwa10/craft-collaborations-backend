package com.example.dissertation_backend.solution.Products.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "products_attributes")
public class ProductAttributes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_attributes_id")
  private Integer productAttributesId;

  @Column(name = "attribute_key")
  private String productAttributesKey;

  @Column(name = "value")
  private String productAttributesValue;

  @Column(name = "affects_pricing")
  private Boolean affectsPricing;

  @ManyToOne
  @JoinColumn(
    name = "product_id",
    nullable = false,
    referencedColumnName = "product_id"
  )
  @JsonBackReference
  private Products product;

  // Constructors
  public ProductAttributes() {
    super();
  }

  public ProductAttributes(
    String productAttributesKey,
    String productAttributesValue,
    Products product
  ) {
    this.productAttributesKey = productAttributesKey;
    this.productAttributesValue = productAttributesValue;
    this.product = product;
  }

  // Getters and Setters
  public Integer getProductAttributesId() {
    return productAttributesId;
  }

  public void setProductAttributesIdId(Integer productAttributesId) {
    this.productAttributesId = productAttributesId;
  }

  public String getProductAttributesKey() {
    return productAttributesKey;
  }

  public void setProductAttributesKey(String productAttributesKey) {
    this.productAttributesKey = productAttributesKey;
  }

  public String getProductAttributesValue() {
    return productAttributesValue;
  }

  public void setProductAttributesValue(String productAttributesValue) {
    this.productAttributesValue = productAttributesValue;
  }

  public Products getProduct() {
    return product;
  }

  public void setProduct(Products product) {
    this.product = product;
  }

  public Boolean getAffectsPricing() {
    return affectsPricing;
  }

  public void setAffectsPricing(Boolean affectsPricing) {
    this.affectsPricing = affectsPricing;
  }
}
