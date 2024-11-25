package com.example.dissertation_backend.solution.Customers.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
  extends JpaRepository<ApplicationUser, Integer> {
  Optional<ApplicationUser> findByUsername(String username);
  // finding by email
  Optional<ApplicationUser> findByUserEmail(String user_email);
}
