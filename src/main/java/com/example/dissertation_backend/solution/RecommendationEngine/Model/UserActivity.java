package com.example.dissertation_backend.solution.RecommendationEngine.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Products.Model.Products;
import jakarta.persistence.*;
import java.time.LocalDateTime;

public class UserActivity {

  public enum ActivityType {
    VIEWED_PRODUCT,
    ADDED_TO_CART,
    PURCHASED_PRODUCT,
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_activity_id")
  private Integer userActivityId;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser user;

  @ManyToOne
  @JoinColumn(name = "product_id", referencedColumnName = "product_id")
  private Products product;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_activity_type")
  private ActivityType type;

  @Column(name = "activity_timestamp")
  private LocalDateTime activityTimestamp;

  // Constructors, getters and setters
  public UserActivity() {
    super();
  }

  public UserActivity(
    ApplicationUser user,
    Products product,
    ActivityType type,
    LocalDateTime activityTimestamp
  ) {
    this.user = user;
    this.product = product;
    this.type = type;
    this.activityTimestamp = activityTimestamp;
  }

  public Integer getUserActivityId() {
    return userActivityId;
  }

  public void setUserActivityId(Integer userActivityId) {
    this.userActivityId = userActivityId;
  }

  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public Products getProduct() {
    return product;
  }

  public void setProduct(Products product) {
    this.product = product;
  }

  public ActivityType getType() {
    return type;
  }

  public void setType(ActivityType type) {
    this.type = type;
  }

  public LocalDateTime getActivityTimestamp() {
    return activityTimestamp;
  }

  public void setActivityTimestamp(LocalDateTime activityTimestamp) {
    this.activityTimestamp = activityTimestamp;
  }
}
