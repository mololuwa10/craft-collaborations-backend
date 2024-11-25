package com.example.dissertation_backend.solution.Customers.Service;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Model.RoleRequest;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.ArtisanProfileRepository;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRequestRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.utils.EncryptionUtil;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

  // @Autowired
  // private PasswordEncoder encoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private RoleRequestRepository roleRequestRepository;

  @Autowired
  private ArtisanProfileRepository artisanProfileRepository;

  public Optional<ApplicationUser> findById(Integer userId) {
    if (userId == null) {
      return Optional.empty();
    }
    return userRepository.findById(userId);
  }

  public List<ApplicationUser> getAllUsers() {
    List<ApplicationUser> users = userRepository.findAll();
    return users;
  }

  @Transactional
  public ApplicationUser updateUser(
    Integer userId,
    ApplicationUser updatedUser
  ) {
    if (userId == null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "User ID is required"
      );
    }
    Optional<ApplicationUser> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found.");
    }

    ApplicationUser existingUser = userOptional.get();

    // Update user details
    existingUser.setFirstname(updatedUser.getFirstname());
    existingUser.setLastname(updatedUser.getLastname());
    existingUser.setUser_email(updatedUser.getUser_email());
    existingUser.setUsername(updatedUser.getUsername());

    // Checking if a new password is provided and it's not empty
    if (
      updatedUser.getPassword() != null &&
      !updatedUser.getPassword().trim().isEmpty()
    ) {
      // Encode the new password and set it
      String encodedPassword = passwordEncoder.encode(
        updatedUser.getPassword()
      );
      existingUser.setPassword(encodedPassword);
    }

    // Update encrypted bank account number if provided
    if (
      updatedUser.getBankAccountNo() != null &&
      !updatedUser.getBankAccountNo().trim().isEmpty()
    ) {
      String encryptedBankAccountNo = EncryptionUtil.encrypt(
        updatedUser.getBankAccountNo()
      );
      existingUser.setBankAccountNo(encryptedBankAccountNo);
    }

    // Update encrypted bank sort code if provided
    if (
      updatedUser.getBankSortCode() != null &&
      !updatedUser.getBankSortCode().trim().isEmpty()
    ) {
      String encryptedBankSortCode = EncryptionUtil.encrypt(
        updatedUser.getBankSortCode()
      );
      existingUser.setBankSortCode(encryptedBankSortCode);
    }
    existingUser.setContactTelephone(updatedUser.getContactTelephone());
    existingUser.setContactAddress(updatedUser.getContactAddress());

    // Save the updated user
    return userRepository.save(existingUser);
  }

  @Transactional
  public ApplicationUser updateUserByAdmin(
    Integer userId,
    ApplicationUser updatedUser
  ) {
    if (userId == null) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "User ID is required"
      );
    }
    Optional<ApplicationUser> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found.");
    }

    ApplicationUser existingUser = userOptional.get();
    System.out.println("Existing User: " + existingUser);

    // Update user details
    existingUser.setFirstname(updatedUser.getFirstname());
    existingUser.setLastname(updatedUser.getLastname());
    existingUser.setUser_email(updatedUser.getUser_email());
    existingUser.setUsername(updatedUser.getUsername());

    // Update password if provided
    if (
      updatedUser.getPassword() != null &&
      !updatedUser.getPassword().trim().isEmpty()
    ) {
      String encodedPassword = passwordEncoder.encode(
        updatedUser.getPassword()
      );
      existingUser.setPassword(encodedPassword);
    }

    existingUser.setBankAccountNo(updatedUser.getBankAccountNo());
    existingUser.setBankSortCode(updatedUser.getBankSortCode());
    existingUser.setContactTelephone(updatedUser.getContactTelephone());
    existingUser.setContactAddress(updatedUser.getContactAddress());

    // Update roles
    if (
      updatedUser.getAuthorities() != null &&
      !updatedUser.getAuthorities().isEmpty()
    ) {
      Set<Roles> updatedRoles = new HashSet<>();
      for (Roles role : updatedUser.getAuthorities()) {
        Integer roleId = role.getRoleId();
        if (roleId == null) {
          throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Role ID is required"
          );
        }
        Roles foundRole = roleRepository
          .findById(roleId)
          .orElseThrow(() ->
            new RuntimeException("Role not found with id: " + roleId)
          );
        updatedRoles.add(foundRole);
      }
      existingUser.setAuthorities(updatedRoles);
    }

    // Save the updated user
    return userRepository.save(existingUser);
  }

  public List<Roles> getAllRoles() {
    return roleRepository.findAll();
  }

  public ApplicationUser getUserById(Integer userId) {
    if (userId == null) {
      return null;
    }
    return userRepository.findById(userId).orElse(null);
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new UsernameNotFoundException(
          "User not found with username: " + username
        )
      );
    return user;
  }

  public ApplicationUser findByUsername(String username) {
    return userRepository
      .findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  public void requestArtisanRole(String username) {
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    boolean hasRole = false;
    for (Roles role : user.getAuthorities()) {
      if (role.getAuthority().equals("Artisan")) {
        hasRole = true;
        break;
      }
    }
    if (hasRole) {
      throw new RuntimeException("User already has an artisan profile.");
    }

    RoleRequest roleRequest = new RoleRequest();
    roleRequest.setUser(user);

    roleRequestRepository.save(roleRequest);
  }

  @Transactional
  public List<RoleRequest> getAllRoleRequests() {
    return roleRequestRepository.findAll();
  }

  public boolean userHasAuthority(ApplicationUser user, String authority) {
    return user
      .getAuthorities()
      .stream()
      .anyMatch(role -> role.getAuthority().equals(authority));
  }

  @Transactional
  public void approveArtisanRequest(Integer userId) {
    if (userId == null) {
      return;
    }
    ApplicationUser user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
      );

    // Assuming you have a method to check if the user already has an ARTISAN role
    if (!userHasAuthority(user, "ARTISAN")) {
      Roles artisanRole = roleRepository
        .findById(3)
        .orElseThrow(() ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")
        );

      user.getAuthorities().add(artisanRole);
      userRepository.save(user);

      ArtisanProfile artisanProfile = new ArtisanProfile();
      artisanProfile.setArtisan(user);

      artisanProfileRepository.save(artisanProfile);

      // updating the role request to mark it as approved
      roleRequestRepository
        .findByUser(user)
        .ifPresent(roleRequest -> {
          roleRequest.setApproved(true);
          roleRequestRepository.save(roleRequest);
        });
    } else {
      throw new IllegalStateException("User is already an artisan.");
    }
  }

  public void updateUserRoleToArtisan(ApplicationUser user) {
    // Fetch the "ARTISAN" role entity from the database
    Roles artisanRole = roleRepository
      .findByAuthority("ARTISAN")
      .orElseThrow(() -> new IllegalArgumentException("ARTISAN role not found")
      );

    // Remove the "USER" role from the user (assuming it exists)
    user.getAuthorities().removeIf(role -> "USER".equals(role.getAuthority()));

    // Add the "ARTISAN" role to the user
    user.getAuthorities().add(artisanRole);

    // Save the updated user to the database
    userRepository.save(user);

    // Update the Security Context with the new roles
    List<GrantedAuthority> updatedAuthorities = user
      .getAuthorities()
      .stream()
      .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
      .collect(Collectors.toList());

    // Create a new Authentication token with the updated authorities
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
      user.getUsername(),
      user.getPassword(),
      updatedAuthorities
    );

    // Set the new authentication in the SecurityContext
    SecurityContextHolder.getContext().setAuthentication(newAuth);
  }

  public ArtisanProfile createOrUpdateArtisanProfile(
    Integer userId,
    ArtisanProfileDTO profileData
  ) {
    if (userId == null) {
      return null;
    }
    ApplicationUser user = userRepository
      .findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    ArtisanProfile profile = artisanProfileRepository
      .findByArtisan(user)
      .orElse(new ArtisanProfile());

    profile.setArtisan(user);
    profile.setBio(profileData.getBio());
    profile.setProfilePicture(profileData.getProfilePicture());
    profile.setLocation(profileData.getLocation());
    profile.setStoreName(profileData.getStoreName());

    return artisanProfileRepository.save(profile);
  }
}
