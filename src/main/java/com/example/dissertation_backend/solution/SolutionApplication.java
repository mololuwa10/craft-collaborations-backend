package com.example.dissertation_backend.solution;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class SolutionApplication {

  public static void main(String[] args) {
    // Load environment variables from the .env file
    Dotenv dotenv = Dotenv.load();

    // Optionally, you can manually set them as System properties if needed
    System.setProperty("DB_URL", dotenv.get("DB_URL"));
    System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
    System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
    System.setProperty("GOOGLE_REDIRECT_URI", dotenv.get("GOOGLE_REDIRECT_URI"));
    System.setProperty("STRIPE_PUBLIC_KEY", dotenv.get("STRIPE_PUBLIC_KEY"));
    System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
    System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
    System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
    s

    SpringApplication.run(SolutionApplication.class, args);
  }

  @Bean
  CommandLineRunner run(
      RoleRepository roleRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      if (roleRepository.findByAuthority("ADMIN").isPresent())
        return;

      Roles adminRole = roleRepository.save(new Roles("ADMIN"));
      roleRepository.save(new Roles("USER"));

      Set<Roles> roles = new HashSet<>();
      roles.add(adminRole);

      ApplicationUser admin = new ApplicationUser(
          1,
          "Mololuwa",
          "Segilola",
          "segilolamololuwa@gmail.com",
          "Mololuwa",
          passwordEncoder.encode("password123"),
          "",
          "",
          "07473143014",
          "86 Park Lane",
          roles,
          LocalDateTime.now(),
          true);

      userRepository.save(admin);
    };
  }
}
