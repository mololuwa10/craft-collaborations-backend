package com.example.dissertation_backend.solution.Authentication.Services;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  @Autowired
  private JwtEncoder jwtEncoder;

  @Autowired
  private JwtDecoder jwtDecoder;

  public String generateJwt(Authentication auth) {
    Instant now = Instant.now();

    // Cast the Authentication's principal to your ApplicationUser class
    ApplicationUser user = (ApplicationUser) auth.getPrincipal();

    String scope = auth
      .getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));

    JwtClaimsSet claims = JwtClaimsSet
      .builder()
      .issuer("self")
      .issuedAt(now)
      .subject(auth.getName())
      .claim("roles", scope)
      .claim("userId", user.getUserId())
      .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  // New method to decode JWT and extract username
  public String getUsernameFromToken(String token) {
    try {
      Jwt jwt = jwtDecoder.decode(token);
      return jwt.getClaim("sub"); // Assuming the subject ('sub') claim contains the username
    } catch (Exception e) {
      // Handle decoding exceptions (e.g., token expired, signature validation failed)
      return null;
    }
  }
}
