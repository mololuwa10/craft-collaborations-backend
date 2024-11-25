package com.example.dissertation_backend.solution.Category.Repository;

import com.example.dissertation_backend.solution.Category.Model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
  List<Category> findByCategoryName(String categoryName);

  // Find all subcategories for a given parent category
  List<Category> findByParentCategory(Category parentCategory);

  // Find all top-level categories (categories with no parent)
  List<Category> findByParentCategoryIsNull();
}
