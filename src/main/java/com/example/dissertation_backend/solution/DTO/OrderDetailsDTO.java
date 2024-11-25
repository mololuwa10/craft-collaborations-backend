package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderDetailsDTO {

  private Integer id;
  private ProductDTO productDTO;
  private String productName;
  private Integer quantity;
  private Double priceAtOrder;

  // Constructors
  public OrderDetailsDTO() {}

  public OrderDetailsDTO(
    Integer id,
    ProductDTO productDTO,
    String productName,
    Integer quantity,
    Double priceAtOrder
  ) {
    this.id = id;
    this.productDTO = productDTO;
    this.productName = productName;
    this.quantity = quantity;
    this.priceAtOrder = priceAtOrder;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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

  // Conversion method from entity to DTO
  public static OrderDetailsDTO fromEntity(
    com.example.dissertation_backend.solution.Orders.Model.OrderDetails orderDetail
  ) {
    OrderDetailsDTO dto = new OrderDetailsDTO();
    dto.setId(orderDetail.getId());
    dto.setProductDTO(convertProductToDTO(orderDetail.getProduct()));
    dto.setProductName(orderDetail.getProduct().getProductName());
    dto.setQuantity(orderDetail.getQuantity());
    dto.setPriceAtOrder(orderDetail.getPriceAtOrder());
    return dto;
  }

  private static ProductDTO convertProductToDTO(Products product) {
    ProductDTO dto = new ProductDTO();

    Set<String> imageUrls = product
      .getImages()
      .stream()
      .map(ProductImages::getImageUrl)
      .collect(Collectors.toSet());

    dto.setProductId(product.getProductId());
    dto.setProductName(product.getProductName());
    dto.setProductDescription(product.getProductDescription());
    dto.setProductPrice(product.getProductPrice());
    dto.setProductDiscount(product.getProductDiscount());
    dto.setProductStockQuantity(product.getProductStockQuantity());
    // dto.setArtisanProfile(convertArtisanProfileToDTO(product.getArtisan()));
    // dto.setCategory(convertCategoryToDTO(product.getCategory()));
    dto.setImageUrls(imageUrls);
    dto.setDateTimeListed(product.getDateListed());
    dto.setDateTimeUpdated(product.getDateTimeUpdated());

    Set<ProductAttributeDTO> attributeDTOs = product
      .getAttributes()
      .stream()
      .map(attr ->
        new ProductAttributeDTO(
          attr.getProductAttributesId(),
          attr.getProductAttributesKey(),
          attr.getProductAttributesValue(),
          attr.getAffectsPricing()
        )
      )
      .collect(Collectors.toSet());

    dto.setAttributes(attributeDTOs);

    return dto;
  }
}
