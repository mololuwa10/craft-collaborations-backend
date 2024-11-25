package com.example.dissertation_backend.solution.GoogleAuthentication.Model;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

  private OAuth2User oauth2User;
  private ApplicationUser applicationUser;

  public CustomOAuth2User(
    OAuth2User oauth2User,
    ApplicationUser applicationUser
  ) {
    this.oauth2User = oauth2User;
    this.applicationUser = applicationUser;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oauth2User
      .getAuthorities()
      .stream()
      .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
      .collect(Collectors.toList());
  }

  @Override
  public String getName() {
    return oauth2User.getAttribute("name");
  }

  public ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public void setApplicationUser(ApplicationUser applicationUser) {
    this.applicationUser = applicationUser;
  }
}
