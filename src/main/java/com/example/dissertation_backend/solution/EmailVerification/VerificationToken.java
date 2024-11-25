package com.example.dissertation_backend.solution.EmailVerification;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "email_token")
  private String token;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser user;

  @Column(name = "token_expiry_date")
  private LocalDateTime expiryDate;

  // Constructors, getters, and setters

  public VerificationToken() {
    super();
  }

  public VerificationToken(
    String token,
    ApplicationUser user,
    LocalDateTime expiryDate
  ) {
    this.token = token;
    this.user = user;
    this.expiryDate = expiryDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }
}
