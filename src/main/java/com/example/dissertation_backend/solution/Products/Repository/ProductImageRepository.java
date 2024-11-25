package com.example.dissertation_backend.solution.Products.Repository;

import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository
  extends JpaRepository<ProductImages, Integer> {
  // Additional custom query methods if needed
  List<ProductImages> findByProduct(Products product);
}
