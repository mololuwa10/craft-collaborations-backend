package com.example.dissertation_backend.solution.Authentication.Services;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.ArtisanProfileRepository;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.LoginResponseDTO;
import com.example.dissertation_backend.solution.EmailVerification.VerificationToken;
import com.example.dissertation_backend.solution.EmailVerification.VerificationTokenRepository;
import com.example.dissertation_backend.solution.Exception.InvalidCredentialsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ArtisanProfileRepository artisanProfileRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private JavaMailSender mailSender;

  public ResponseEntity<?> registerUser(
    String firstname,
    String lastname,
    String user_email,
    String username,
    String password,
    String bankAccountNo,
    String bankSortCode,
    String contactTelephone,
    String contactAddress,
    LocalDateTime dateJoined,
    Boolean enabled
  ) {
    // Check if username exists
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(Collections.singletonMap("error", "Username already exists"));
    }

    // Check if email exists
    if (userRepository.findByUserEmail(user_email).isPresent()) {
      return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(Collections.singletonMap("error", "Email already exists"));
    }
    String encodedPassword = passwordEncoder.encode(password);
    Roles userRole = roleRepository.findByAuthority("USER").get();

    Set<Roles> authorities = new HashSet<>();
    authorities.add(userRole);

    ApplicationUser newUser = new ApplicationUser(
      0,
      firstname,
      lastname,
      user_email,
      username,
      encodedPassword,
      bankAccountNo,
      bankSortCode,
      contactTelephone,
      contactAddress,
      authorities,
      dateJoined,
      false
    );

    newUser = userRepository.save(newUser);

    // Generate verification token
    String token = UUID.randomUUID().toString();
    createVerificationToken(newUser, token);

    // Send verification email
    sendVerificationEmail(newUser, token);

    return ResponseEntity.ok(newUser);
  }

  public LoginResponseDTO loginUser(String username, String password) {
    try {
      Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
      );

      String token = tokenService.generateJwt(auth);

      ApplicationUser user = userRepository
        .findByUsername(username)
        .orElseThrow(() ->
          new UsernameNotFoundException(
            "User not found with username: " + username
          )
        );

      boolean isVerified = user.isEnabled();
      ArtisanProfileDTO artisanDetails = null;
      if (userIsArtisan(user)) {
        // Fetch artisan details
        artisanDetails = fetchArtisanDetailsForUser(user);
      }

      return new LoginResponseDTO(
        userRepository.findByUsername(username).get(),
        token,
        artisanDetails,
        isVerified
      );
    } catch (AuthenticationException e) {
      throw new InvalidCredentialsException("Invalid credentials");
    }
  }

  private ArtisanProfileDTO fetchArtisanDetailsForUser(ApplicationUser user) {
    ArtisanProfile artisanProfile = artisanProfileRepository
      .findByArtisan_UserId(user.getUserId())
      .orElseThrow(() ->
        new EntityNotFoundException("Artisan profile not found")
      );
    return convertArtisanProfileToDTO(artisanProfile);
  }

  private ArtisanProfileDTO convertArtisanProfileToDTO(
    ArtisanProfile artisanProfile
  ) {
    ArtisanProfileDTO artisanProfileDTO = new ArtisanProfileDTO();
    artisanProfileDTO.setArtisanId(artisanProfile.getArtisanId());
    artisanProfileDTO.setBio(artisanProfile.getBio());
    artisanProfileDTO.setProfilePicture(artisanProfile.getProfilePicture());
    artisanProfileDTO.setLocation(artisanProfile.getLocation());
    artisanProfileDTO.setStoreName(artisanProfile.getStoreName());
    artisanProfileDTO.setStoreBanner(artisanProfile.getStoreBanner());
    artisanProfileDTO.setAnnouncements(artisanProfile.getAnnouncements());
    artisanProfileDTO.setBusinessHours(artisanProfile.getBusinessHours());
    artisanProfileDTO.setGallery(artisanProfile.getGallery());
    artisanProfileDTO.setStories(artisanProfile.getStories());
    artisanProfileDTO.setSpecializations(artisanProfile.getSpecializations());
    artisanProfileDTO.setMaterialsUsed(artisanProfile.getMaterialsUsed());
    artisanProfileDTO.setServicesOffered(artisanProfile.getServicesOffered());
    artisanProfileDTO.setExperienceYears(artisanProfile.getExperienceYears());
    artisanProfileDTO.setShippingPolicies(artisanProfile.getShippingPolicies());
    artisanProfileDTO.setReturnPolicy(artisanProfile.getReturnPolicy());
    artisanProfileDTO.setPaymentOptions(artisanProfile.getPaymentOptions());
    artisanProfileDTO.setTermsConditions(artisanProfile.getTermsConditions());
    artisanProfileDTO.setPrivacyPolicy(artisanProfile.getPrivacyPolicy());
    artisanProfileDTO.setCommunicationPreferences(
      artisanProfile.getCommunicationPreferences()
    );
    artisanProfileDTO.setPreferredLanguage(
      artisanProfile.getPreferredLanguage()
    );

    // Map other fields from ApplicationUser to ArtisanProfileDTO as needed
    // artisanProfileDTO.setFirstname(artisanProfile.getArtisan().getFirstname());
    // artisanProfileDTO.setLastname(artisanProfile.getArtisan().getLastname());
    // artisanProfileDTO.setUser_email(
    //   artisanProfile.getArtisan().getUser_email()
    // );
    // artisanProfileDTO.setBankAccountNo(
    //   artisanProfile.getArtisan().getBankAccountNo()
    // );
    // artisanProfileDTO.setBankSortCode(
    //   artisanProfile.getArtisan().getBankSortCode()
    // );
    // artisanProfileDTO.setContactTelephone(
    //   artisanProfile.getArtisan().getContactTelephone()
    // );
    // artisanProfileDTO.setContactAddress(
    //   artisanProfile.getArtisan().getContactAddress()
    // );

    return artisanProfileDTO;
  }

  private boolean userIsArtisan(ApplicationUser user) {
    return user
      .getAuthorities()
      .stream()
      .anyMatch(role -> "ARTISAN".equals(role.getAuthority()));
  }

  public void sendVerificationEmail(ApplicationUser user, String token) {
    String recipientAddress = user.getUser_email();
    String subject = "Craft Collaboration Registration Confirmation";
    String confirmationUrl = "/api/email/verify?token=" + token;
    String message = "Please click the link to verify your account: ";

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message + "http://localhost:8080" + confirmationUrl);
    mailSender.send(email);
  }

  public void createVerificationToken(ApplicationUser user, String token) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken(token);
    verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
    verificationTokenRepository.save(verificationToken);
  }
}
