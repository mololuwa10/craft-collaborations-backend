package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;

public class UserDetailsDTO {

  private ApplicationUser user;
  private ArtisanProfile artisanProfile;

  public UserDetailsDTO(ApplicationUser user, ArtisanProfile artisanProfile) {
    this.user = user;
    this.artisanProfile = artisanProfile;
  }

  // Getters and setters
  public ApplicationUser getUser() {
    return user;
  }

  public void setUser(ApplicationUser user) {
    this.user = user;
  }

  public ArtisanProfile getArtisanProfile() {
    return artisanProfile;
  }

  public void setArtisanProfile(ArtisanProfile artisanProfile) {
    this.artisanProfile = artisanProfile;
  }
}
