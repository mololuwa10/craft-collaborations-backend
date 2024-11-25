package com.example.dissertation_backend.solution.Customers.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtisanProfileRepository
  extends JpaRepository<ArtisanProfile, Integer> {
  // Add custom query methods if needed
  Optional<ArtisanProfile> findByArtisan(ApplicationUser artisan);
  Optional<ArtisanProfile> findByArtisan_UserId(Integer userId);
  List<ArtisanProfile> findAllByCreationDateAfter(Date date);
}
