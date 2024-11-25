package com.example.dissertation_backend.solution.Products.Repository;

import com.example.dissertation_backend.solution.Products.Model.Products;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
  extends JpaRepository<Products, Integer>, JpaSpecificationExecutor<Products> {
  List<Products> findByArtisan_ArtisanId(Integer artisanId);

  List<Products> findByCategory_CategoryId(Integer categoryId);

  List<Products> findAllByCategory_CategoryIdIn(Set<Integer> categoryIds);
}
