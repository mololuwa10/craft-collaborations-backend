package com.example.dissertation_backend.solution.Products.Service;

import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Repository.ProductImageRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

  @Autowired
  private ProductImageRepository productImageRepository;

  public List<ProductImages> getImagesByProduct(Products product) {
    return productImageRepository.findByProduct(product);
  }

  public Optional<ProductImages> getImageById(Integer imageId) {
    if (imageId == null) {
      return Optional.empty();
    }
    return productImageRepository.findById(imageId);
  }

  public ProductImages saveImage(ProductImages image) {
    if (image == null) {
      return null;
    }
    return productImageRepository.save(image);
  }

  public void deleteImage(Integer imageId) {
    if (imageId == null) {
      return;
    }
    productImageRepository.deleteById(imageId);
  }
}
