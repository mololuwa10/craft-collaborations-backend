package com.example.dissertation_backend.solution.Customers.Controllers;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Repository.ArtisanProfileRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
import com.example.dissertation_backend.solution.DTO.UserDetailsDTO;
import java.security.Principal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ArtisanProfileRepository artisanProfileRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ArtisanProfileService artisanProfileService;

  @GetMapping("/")
  public String helloUserController() {
    return "User access level";
  }

  @GetMapping("/allArtisans")
  public List<ArtisanProfile> getAllArtisanProfiles() {
    return artisanProfileService.getAllArtisanProfiles();
  }

  @GetMapping("/info")
  public ResponseEntity<UserDetailsDTO> getUserDetails() {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      String username = jwt.getClaim("sub");
      Optional<ApplicationUser> optionalUser = userRepository.findByUsername(
        username
      );
      if (optionalUser.isPresent()) {
        ApplicationUser user = optionalUser.get();
        user.setPassword(null);

        ArtisanProfile artisanProfile = null;
        if (userIsArtisan(user)) {
          artisanProfile = fetchArtisanProfile(user);
        }

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
          user,
          artisanProfile
        );

        return ResponseEntity.ok(userDetailsDTO);
      } else {
        throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "User not found"
        );
      }
    } else {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "User not authenticated"
      );
    }
  }

  private boolean userIsArtisan(ApplicationUser user) {
    return user
      .getAuthorities()
      .stream()
      .anyMatch(role -> "ARTISAN".equals(role.getAuthority()));
  }

  private ArtisanProfile fetchArtisanProfile(ApplicationUser user) {
    return artisanProfileRepository
      .findByArtisan_UserId(user.getUserId())
      .orElse(null);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<?> updateUser(
    @PathVariable Integer userId,
    @RequestBody ApplicationUser updatedUser
  ) {
    Authentication authentication = SecurityContextHolder
      .getContext()
      .getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      Long authenticatedUserIdLong = jwt.getClaim("userId");

      // converting Long to Integer
      Integer authenticatedUserId = authenticatedUserIdLong != null
        ? authenticatedUserIdLong.intValue()
        : null;

      // Check if the authenticated user has an admin role
      boolean isAdmin = authentication
        .getAuthorities()
        .stream()
        .anyMatch(grantedAuthority ->
          grantedAuthority.getAuthority().equals("ROLE_ADMIN")
        );

      // Checking if the authenticated user is trying to update their own details
      if (
        !isAdmin &&
        (authenticatedUserId == null || !userId.equals(authenticatedUserId))
      ) {
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body("You can only update your own details.");
      }

      try {
        ApplicationUser user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
      } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    } else {
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("User not authenticated");
    }
  }

  @PostMapping("/request-artisan")
  public ResponseEntity<?> requestArtisanRole(Principal principal) {
    userService.requestArtisanRole(principal.getName());
    return ResponseEntity.ok(
      "Request to become an artisan submitted successfully."
    );
  }

  @PostMapping("/createArtisanProfile")
  public ResponseEntity<?> createArtisanProfile() {
    try {
      // Extract username or userId from the security context
      String username = SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getName();

      // Use the username to find the user (adjust based on your user service)
      ApplicationUser user = userRepository
        .findByUsername(username)
        .orElseThrow(() ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

      if (!user.getEnabled()) {
        return ResponseEntity
          .status(HttpStatus.FORBIDDEN)
          .body("User is not verified and cannot create an artisan profile.");
      }

      // Check user's roles
      boolean isUser = user
        .getAuthorities()
        .stream()
        .anyMatch(role -> "USER".equals(role.getAuthority()));

      boolean isArtisan = user
        .getAuthorities()
        .stream()
        .anyMatch(role -> "ARTISAN".equals(role.getAuthority()));

      if (isArtisan) {
        return ResponseEntity
          .badRequest()
          .body("User already has an artisan profile.");
      } else if (isUser) {
        // Check if the user already has an artisan profile
        Optional<ArtisanProfile> existingProfile = artisanProfileService.findByArtisan(
          user
        );
        if (existingProfile.isPresent()) {
          return ResponseEntity
            .badRequest()
            .body("Artisan profile already exists for this user.");
        }

        // Update the role of the user to ARTISAN
        userService.updateUserRoleToArtisan(user);

        // Create a new artisan profile for the user
        ArtisanProfile newProfile = new ArtisanProfile();
        newProfile.setArtisan(user);
        newProfile.setBio("Default bio");
        newProfile.setCreationDate(new Date());

        ArtisanProfile createdProfile = artisanProfileService.saveOrUpdateArtisanProfile(
          newProfile
        );

        return ResponseEntity.ok(createdProfile);
      } else {
        return ResponseEntity
          .badRequest()
          .body("User does not have permission to create an artisan profile.");
      }
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
    }
  }

  @GetMapping("/newArtisans")
  public List<ArtisanProfile> getNewArtisans() {
    return artisanProfileService.findNewArtisansToHighlight();
  }
}
