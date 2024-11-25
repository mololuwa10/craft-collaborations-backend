package com.example.dissertation_backend.solution.DTO;

public class CategoryDTO {

  private Integer categoryId;
  private String categoryName;
  private String categoryDescription;
  private String categoryImageUrl;
  private Integer parentCategoryId;

  public CategoryDTO() {
    super();
  }

  public CategoryDTO(
    Integer categoryId,
    String categoryName,
    String categoryDescription,
    String categoryImageUrl,
    Integer parentCategoryId
  ) {
    super();
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.categoryDescription = categoryDescription;
    this.categoryImageUrl = categoryImageUrl;
    this.parentCategoryId = parentCategoryId;
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

  public Integer getParentCategoryId() {
    return parentCategoryId;
  }

  public void setParentCategoryId(Integer parentCategoryId) {
    this.parentCategoryId = parentCategoryId;
  }
}
