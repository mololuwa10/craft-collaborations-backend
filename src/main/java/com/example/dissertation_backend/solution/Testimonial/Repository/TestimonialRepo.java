package com.example.dissertation_backend.solution.Testimonial.Repository;

import com.example.dissertation_backend.solution.Testimonial.Model.Testimonial;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialRepo extends JpaRepository<Testimonial, Integer> {
  List<Testimonial> findByApplicationUser_UserId(Integer userId);
}
