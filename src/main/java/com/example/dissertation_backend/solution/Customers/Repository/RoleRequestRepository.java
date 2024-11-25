package com.example.dissertation_backend.solution.Customers.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.RoleRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRequestRepository
  extends JpaRepository<RoleRequest, Long> {
  // Add custom query methods if needed
  Optional<RoleRequest> findByUser(ApplicationUser user);
}
