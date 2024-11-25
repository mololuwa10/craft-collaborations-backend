package com.example.dissertation_backend.solution.DTO;

import java.time.LocalDateTime;
import java.util.Set;

public class ProductDTO {

  private Integer productId;
  private String productName;
  private String productDescription;
  private Double productPrice;
  private Integer productStockQuantity;
  private ArtisanProfileDTO artisanProfile;
  private LocalDateTime dateTimeListed;
  private LocalDateTime dateTimeUpdated;
  private Double productDiscount;
  private CategoryDTO category;
  private Set<String> imageUrls;
  private Set<ProductAttributeDTO> attributes;
  private Double dynamicPricing;

  public ProductDTO() {
    super();
  }

  public ProductDTO(
    Integer productId,
    String productName,
    String productDescription,
    Double productPrice,
    Integer productStockQuantity,
    Double productDiscount,
    ArtisanProfileDTO artisanProfile,
    CategoryDTO category,
    Set<String> imageUrls,
    LocalDateTime dateTimeListed,
    LocalDateTime dateTimeUpdated,
    Double dynamicPricing
  ) {
    super();
    this.productId = productId;
    this.productName = productName;
    this.productDescription = productDescription;
    this.productPrice = productPrice;
    this.productStockQuantity = productStockQuantity;
    this.productDiscount = productDiscount;
    this.artisanProfile = artisanProfile;
    this.category = category;
    this.imageUrls = imageUrls;
    this.dateTimeListed = dateTimeListed;
    this.dateTimeUpdated = dateTimeUpdated;
    this.dynamicPricing = dynamicPricing;
  }

  public ArtisanProfileDTO getArtisanProfile() {
    return artisanProfile;
  }

  public void setArtisanProfile(ArtisanProfileDTO artisanProfile) {
    this.artisanProfile = artisanProfile;
  }

  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductDescription() {
    return productDescription;
  }

  public void setProductDescription(String productDescription) {
    this.productDescription = productDescription;
  }

  public Double getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(Double productPrice) {
    this.productPrice = productPrice;
  }

  public Integer getProductStockQuantity() {
    return productStockQuantity;
  }

  public void setProductStockQuantity(Integer productStockQuantity) {
    this.productStockQuantity = productStockQuantity;
  }

  public CategoryDTO getCategory() {
    return category;
  }

  public void setCategory(CategoryDTO category) {
    this.category = category;
  }

  public Set<String> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(Set<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  public LocalDateTime getDateTimeListed() {
    return dateTimeListed;
  }

  public void setDateTimeListed(LocalDateTime dateTimeListed) {
    this.dateTimeListed = dateTimeListed;
  }

  public LocalDateTime getDateTimeUpdated() {
    return dateTimeUpdated;
  }

  public void setDateTimeUpdated(LocalDateTime dateTimeUpdated) {
    this.dateTimeUpdated = dateTimeUpdated;
  }

  public Double getProductDiscount() {
    return productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Set<ProductAttributeDTO> getAttributes() {
    return attributes;
  }

  public void setAttributes(Set<ProductAttributeDTO> attributes) {
    this.attributes = attributes;
  }

  public Double getDynamicPricing() {
    return dynamicPricing;
  }

  public void setDynamicPricing(Double dynamicPricing) {
    this.dynamicPricing = dynamicPricing;
  }
}
