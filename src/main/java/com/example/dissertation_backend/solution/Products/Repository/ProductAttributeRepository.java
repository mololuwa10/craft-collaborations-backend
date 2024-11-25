package com.example.dissertation_backend.solution.Products.Repository;

import com.example.dissertation_backend.solution.Products.Model.ProductAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductAttributeRepository
  extends JpaRepository<ProductAttributes, Integer> {
  @Modifying
  @Transactional
  @Query(
    "DELETE FROM ProductAttributes pa WHERE pa.product.productId = :productId"
  )
  void deleteByProductId(@Param("productId") Integer productId);
}
