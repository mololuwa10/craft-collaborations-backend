package com.example.dissertation_backend.solution.Customers.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_requests")
public class RoleRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_request_id")
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private ApplicationUser user;

  private boolean isApproved = false;

  public RoleRequest() {
    super();
  }

  public RoleRequest(ApplicationUser user) {
    super();
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public boolean isApproved() {
    return isApproved;
  }

  public void setApproved(boolean isApproved) {
    this.isApproved = isApproved;
  }
}
