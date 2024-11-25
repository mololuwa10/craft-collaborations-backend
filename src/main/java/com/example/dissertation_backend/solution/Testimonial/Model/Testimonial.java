package com.example.dissertation_backend.solution.Testimonial.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "testimonial")
public class Testimonial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "testimonial_id")
  private Integer testimonialId;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser applicationUser;

  @Column(name = "testimonial_title")
  private String testimonialTitle;

  @Column(name = "testimonial_content")
  private String testimonial;

  @Column(name = "testimonial_rating")
  private Integer rating;

  @Column(name = "testimonial_date")
  private LocalDateTime testimonialDate;

  @Column(name = "is_approved")
  private Boolean isApproved;

  @Column(name = "is_engaged")
  private Boolean isEngaged;

  // Constructors
  public Testimonial() {
    super();
  }

  public Testimonial(
    Integer testimonialId,
    ApplicationUser applicationUser,
    String testimonial,
    String testimonialTitle,
    Integer rating,
    LocalDateTime testimonialDate
  ) {
    super();
    this.testimonialId = testimonialId;
    this.testimonialTitle = testimonialTitle;
    this.applicationUser = applicationUser;
    this.testimonial = testimonial;
    this.rating = rating;
    this.testimonialDate = testimonialDate;
  }

  // Getters and setters
  public Integer getTestimonialId() {
    return testimonialId;
  }

  public void setTestimonialId(Integer testimonialId) {
    this.testimonialId = testimonialId;
  }

  public ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUser applicationUser) {
    this.applicationUser = applicationUser;
  }

  public String getTestimonial() {
    return testimonial;
  }

  public String getTestimonialTitle() {
    return testimonialTitle;
  }

  public void setTestimonialTitle(String testimonialTitle) {
    this.testimonialTitle = testimonialTitle;
  }

  public void setTestimonial(String testimonial) {
    this.testimonial = testimonial;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public LocalDateTime getTestimonialDate() {
    return testimonialDate;
  }

  public void setTestimonialDate(LocalDateTime testimonialDate) {
    this.testimonialDate = testimonialDate;
  }

  public Boolean getIsApproved() {
    return isApproved;
  }

  public void setIsApproved(Boolean isApproved) {
    this.isApproved = isApproved;
  }

  public Boolean getIsEngaged() {
    return isEngaged;
  }

  public void setIsEngaged(Boolean isEngaged) {
    this.isEngaged = isEngaged;
  }
}
