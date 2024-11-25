package com.example.dissertation_backend.solution.Review.Repository;

import com.example.dissertation_backend.solution.Review.Model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
  // Add any custom query methods if needed
  public List<Review> findByProducts_ProductId(Integer productId);

  public List<Review> findByApplicationUser_UserId(Integer userId);

  List<Review> findByArtisan_ArtisanId(Integer artisanId);
}
