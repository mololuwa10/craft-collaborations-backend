package com.example.dissertation_backend.solution.Customers.Repository;

import com.example.dissertation_backend.solution.Customers.Model.Roles;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
  Optional<Roles> findByAuthority(String authority);
}
