package com.example.dissertation_backend.solution.DTO;

import java.time.LocalDateTime;

public class ReviewDTO {

  private Integer reviewId;
  private ProductDTO products;
  private ApplicationUserDTO applicationUser;
  private String reviewTitle;
  private Integer rating;
  private String comment;
  private LocalDateTime reviewDate;

  public ReviewDTO() {
    super();
  }

  public ReviewDTO(
    Integer reviewId,
    ProductDTO products,
    ApplicationUserDTO applicationUser,
    String reviewTitle,
    Integer rating,
    String comment,
    LocalDateTime reviewDate
  ) {
    super();
    this.reviewId = reviewId;
    this.products = products;
    this.applicationUser = applicationUser;
    this.reviewTitle = reviewTitle;
    this.rating = rating;
    this.comment = comment;
    this.reviewDate = reviewDate;
  }

  public Integer getReviewId() {
    return reviewId;
  }

  public void setReviewId(Integer reviewId) {
    this.reviewId = reviewId;
  }

  public ProductDTO getProducts() {
    return products;
  }

  public void setProducts(ProductDTO products) {
    this.products = products;
  }

  public ApplicationUserDTO getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUserDTO applicationUser) {
    this.applicationUser = applicationUser;
  }

  public String getReviewTitle() {
    return reviewTitle;
  }

  public void setReviewTitle(String reviewTitle) {
    this.reviewTitle = reviewTitle;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public LocalDateTime getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(LocalDateTime reviewDate) {
    this.reviewDate = reviewDate;
  }
}
