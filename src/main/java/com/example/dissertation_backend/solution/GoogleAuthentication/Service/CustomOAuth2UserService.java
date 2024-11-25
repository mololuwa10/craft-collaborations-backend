package com.example.dissertation_backend.solution.GoogleAuthentication.Service;

import com.example.dissertation_backend.solution.Authentication.Services.TokenService;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.GoogleAuthentication.Model.CustomOAuth2User;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

// @Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private static final Logger logger = LoggerFactory.getLogger(
    CustomOAuth2UserService.class
  );

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private TokenService tokenService;

  @Override
  public OidcUser loadUser(OAuth2UserRequest userRequest)
    throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);
    logger.debug("OAuth2User loaded: {}", oauth2User.getAttributes());
    if (userRequest instanceof OidcUserRequest) {
      OidcUserRequest oidcUserRequest = (OidcUserRequest) userRequest;
      OidcIdToken idToken = oidcUserRequest.getIdToken();
      ApplicationUser user = (ApplicationUser) processOAuth2User(
        oauth2User,
        idToken
      );
      logger.debug("User processed and saved: {}", user);
      return (OidcUser) user;
    }

    throw new IllegalArgumentException("Expected OIDC User Request");
  }

  private OidcUser processOAuth2User(
    OAuth2User oauth2User,
    OidcIdToken idToken
  ) {
    String email = oauth2User.getAttribute("email");
    ApplicationUser user = userRepository
      .findByUserEmail(email)
      .orElseGet(() -> registerNewUser(oauth2User, email));

    // Wrap OAuth2User in CustomOAuth2User
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(oauth2User, user);

    String token = generateJwtForUser(user);

    Set<GrantedAuthority> userAuthorities = customOAuth2User
      .getAuthorities()
      .stream()
      .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
      .collect(Collectors.toSet());

    Map<String, Object> userAttributes = new HashMap<>(
      oauth2User.getAttributes()
    );
    userAttributes.put("token", token);

    return new DefaultOidcUser(userAuthorities, idToken, "email");
  }

  private ApplicationUser registerNewUser(OAuth2User oauth2User, String email) {
    ApplicationUser newUser = new ApplicationUser();
    newUser.setUser_email(email);
    int randomNumber = new Random().nextInt(90000) + 10000;
    String username = oauth2User.getAttribute("given_name");
    newUser.setUsername(username + randomNumber);
    newUser.setFirstname(oauth2User.getAttribute("given_name"));
    newUser.setLastname(oauth2User.getAttribute("family_name"));
    newUser.setEnabled(true);
    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

    // Assign default role
    Roles userRole = roleRepository
      .findByAuthority("USER")
      .orElseThrow(() -> new RuntimeException("Role USER not found"));
    newUser.setAuthorities(new HashSet<>(Collections.singletonList(userRole)));

    userRepository.save(newUser);
    return newUser;
  }

  private String generateJwtForUser(ApplicationUser user) {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      user,
      null,
      user.getAuthorities()
    );
    return tokenService.generateJwt(authentication);
  }
}
