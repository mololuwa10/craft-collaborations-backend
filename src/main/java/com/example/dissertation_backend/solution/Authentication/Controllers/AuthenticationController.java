package com.example.dissertation_backend.solution.Authentication.Controllers;

import com.example.dissertation_backend.solution.Authentication.Services.AuthenticationService;
import com.example.dissertation_backend.solution.DTO.LoginResponseDTO;
import com.example.dissertation_backend.solution.DTO.RegistrationDTO;
import com.example.dissertation_backend.solution.Exception.InvalidCredentialsException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthenticationController {

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO body) {
    LocalDateTime now = LocalDateTime.now();
    return authenticationService.registerUser(
      body.getFirstname(),
      body.getLastname(),
      body.getUser_email(),
      body.getUsername(),
      body.getPassword(),
      body.getBankAccountNo(),
      body.getBankSortCode(),
      body.getContactTelephone(),
      body.getContactAddress(),
      now,
      body.getEnabled()
    );
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> loginUser(
    @RequestBody RegistrationDTO body
  ) {
    try {
      LoginResponseDTO response = authenticationService.loginUser(
        body.getUsername(),
        body.getPassword()
      );
      return ResponseEntity.ok(response);
    } catch (InvalidCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }
}
