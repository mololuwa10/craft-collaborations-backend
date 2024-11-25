package com.example.dissertation_backend.solution.Category.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Integer categoryId;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "category_description")
  private String categoryDescription;

  @Column(name = "category_image_url")
  private String categoryImageUrl;

  // Reference to the parent category
  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Category parentCategory;

  // List of subcategories
  @JsonManagedReference
  @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
  private List<Category> subCategories = new ArrayList<>();

  // Constructor
  public Category() {
    super();
  }

  // GETTERS AND SETTERSS
  public Category(String name) {
    this.categoryName = name;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryDescription() {
    return categoryDescription;
  }

  public void setCategoryDescription(String categoryDescription) {
    this.categoryDescription = categoryDescription;
  }

  public String getCategoryImageUrl() {
    return categoryImageUrl;
  }

  public void setCategoryImageUrl(String categoryImageUrl) {
    this.categoryImageUrl = categoryImageUrl;
  }

  public Category getParentCategory() {
    return parentCategory;
  }

  public void setParentCategory(Category parentCategory) {
    this.parentCategory = parentCategory;
  }

  public List<Category> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<Category> subCategories) {
    this.subCategories = subCategories;
  }

  // Helper method to add a subcategory
  public void addSubCategory(Category subCategory) {
    this.subCategories.add(subCategory);
    subCategory.setParentCategory(this);
  }
}
