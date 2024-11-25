package com.example.dissertation_backend.solution.Review.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Integer reviewId;

  @ManyToOne
  @JoinColumn(name = "product_id", referencedColumnName = "product_id")
  private Products products;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser applicationUser;

  @ManyToOne
  @JoinColumn(name = "artisan_id", referencedColumnName = "artisan_id")
  private ArtisanProfile artisan;

  @Column(name = "review_title")
  private String reviewTitle;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "comment")
  private String comment;

  @Column(name = "review_date")
  @JsonFormat(
    shape = JsonFormat.Shape.STRING,
    pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  )
  // @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDateTime reviewDate;

  // Constructors
  public Review() {
    super();
  }

  public Review(
    Integer reviewId,
    Products products,
    ApplicationUser applicationUser,
    String reviewTitle,
    Integer rating,
    String comment,
    LocalDateTime reviewDate,
    ArtisanProfile artisanProfile
  ) {
    super();
    this.reviewId = reviewId;
    this.products = products;
    this.applicationUser = applicationUser;
    this.reviewTitle = reviewTitle;
    this.rating = rating;
    this.comment = comment;
    this.reviewDate = reviewDate;
    this.artisan = artisanProfile;
  }

  // Getters and setters
  public Integer getReviewId() {
    return reviewId;
  }

  public void setReviewId(Integer reviewId) {
    this.reviewId = reviewId;
  }

  public String getReviewTitle() {
    return reviewTitle;
  }

  public void setReviewTitle(String reviewTitle) {
    this.reviewTitle = reviewTitle;
  }

  public Products getProducts() {
    return products;
  }

  public void setProducts(Products products) {
    this.products = products;
  }

  public ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUser applicationUser) {
    this.applicationUser = applicationUser;
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

  public ArtisanProfile getArtisan() {
    return artisan;
  }

  public void setArtisan(ArtisanProfile artisan) {
    this.artisan = artisan;
  }
}
