package com.example.dissertation_backend.solution.EmailVerification;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository
  extends JpaRepository<VerificationToken, Integer> {
  Optional<VerificationToken> findByToken(String token);
}
