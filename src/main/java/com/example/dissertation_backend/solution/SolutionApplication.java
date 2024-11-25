package com.example.dissertation_backend.solution;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
import com.example.dissertation_backend.solution.Customers.Repository.RoleRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
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
    SpringApplication.run(SolutionApplication.class, args);
  }

  @Bean
  CommandLineRunner run(
    RoleRepository roleRepository,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder
  ) {
    return args -> {
      if (roleRepository.findByAuthority("ADMIN").isPresent()) return;

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
        true
      );

      userRepository.save(admin);
    };
  }
}
