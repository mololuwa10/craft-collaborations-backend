package com.example.dissertation_backend.solution.DTO;

import java.time.LocalDateTime;

public class TestimonialDTO {

  private Integer testimonialId;
  private ApplicationUserDTO applicationUser;
  private String testimonialTitle;
  private Integer rating;
  private String comment;
  private LocalDateTime testimonialDate;
  private Boolean isApproved;

  // constructor
  public TestimonialDTO(
    Integer testimonialId,
    ApplicationUserDTO applicationUser,
    Integer rating,
    String testimonialTitle,
    String comment,
    LocalDateTime testimonialDate,
    Boolean isApproved
  ) {
    super();
    this.testimonialId = testimonialId;
    this.applicationUser = applicationUser;
    this.testimonialTitle = testimonialTitle;
    this.rating = rating;
    this.comment = comment;
    this.testimonialDate = testimonialDate;
    this.isApproved = isApproved;
  }

  // Getters and setters
  public Integer getTestimonialId() {
    return testimonialId;
  }

  public void setTestimonialId(Integer testimonialId) {
    this.testimonialId = testimonialId;
  }

  public ApplicationUserDTO getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUserDTO applicationUser) {
    this.applicationUser = applicationUser;
  }

  public String getTestimonialTitle() {
    return testimonialTitle;
  }

  public void setTestimonialTitle(String testimonialTitle) {
    this.testimonialTitle = testimonialTitle;
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

  public LocalDateTime getTestimonialDate() {
    return testimonialDate;
  }

  public Boolean getIsApproved() {
    return isApproved;
  }

  public void setIsApproved(Boolean isApproved) {
    this.isApproved = isApproved;
  }

  public void setTestimonialDate(LocalDateTime testimonialDate) {
    this.testimonialDate = testimonialDate;
  }

  public TestimonialDTO() {
    super();
  }
}
