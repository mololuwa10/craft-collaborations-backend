package com.example.dissertation_backend.solution.EmailVerification;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin("*")
public class VerificationController {

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/verify")
  public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
    VerificationToken verificationToken = verificationTokenRepository
      .findByToken(token)
      .orElseThrow(() -> new RuntimeException("Invalid token"));

    if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      return ResponseEntity.badRequest().body("Token expired");
    }

    ApplicationUser user = verificationToken.getUser();
    user.setEnabled(true);
    userRepository.save(user);

    return ResponseEntity.ok("Account verified successfully");
  }

  // Checking if the user is verified
  @GetMapping("/check-verification")
  public ResponseEntity<?> checkVerificationStatus(Principal principal) {
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("User is not logged in");
    }

    String username = principal.getName();
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    return ResponseEntity.ok(user.getEnabled());
  }
}
