package com.example.dissertation_backend.solution;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Paths;
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
    // Dotenv dotenv = Dotenv.load();

    // Load environment variables based on the environment
    Dotenv dotenv = null;

    if (Files.exists(Paths.get(".env"))) {
      dotenv = Dotenv.load(); // Load .env file in local development
    }

    // Set system properties for the environment variables
    setSystemProperties(dotenv);

    SpringApplication.run(SolutionApplication.class, args);
  }

  private static void setSystemProperties(Dotenv dotenv) {
    System.setProperty("DB_URL", getEnvValue(dotenv, "DB_URL"));
    System.setProperty("DB_USERNAME", getEnvValue(dotenv, "DB_USERNAME"));
    System.setProperty("DB_PASSWORD", getEnvValue(dotenv, "DB_PASSWORD"));
    System.setProperty("GOOGLE_CLIENT_ID", getEnvValue(dotenv, "GOOGLE_CLIENT_ID"));
    System.setProperty("GOOGLE_CLIENT_SECRET", getEnvValue(dotenv, "GOOGLE_CLIENT_SECRET"));
    System.setProperty("GOOGLE_REDIRECT_URI", getEnvValue(dotenv, "GOOGLE_REDIRECT_URI"));
    System.setProperty("STRIPE_PUBLIC_KEY", getEnvValue(dotenv, "STRIPE_PUBLIC_KEY"));
    System.setProperty("STRIPE_SECRET_KEY", getEnvValue(dotenv, "STRIPE_SECRET_KEY"));
    System.setProperty("MAIL_USERNAME", getEnvValue(dotenv, "MAIL_USERNAME"));
    System.setProperty("MAIL_PASSWORD", getEnvValue(dotenv, "MAIL_PASSWORD"));
    System.setProperty("MAIL_HOST", getEnvValue(dotenv, "MAIL_HOST"));
    System.setProperty("MAIL_PORT", getEnvValue(dotenv, "MAIL_PORT"));
  }

  private static String getEnvValue(Dotenv dotenv, String key) {
    if (dotenv != null) {
      return dotenv.get(key);
    }
    return System.getenv(key);
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
