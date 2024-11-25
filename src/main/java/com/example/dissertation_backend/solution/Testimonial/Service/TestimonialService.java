package com.example.dissertation_backend.solution.Testimonial.Service;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.DTO.ApplicationUserDTO;
import com.example.dissertation_backend.solution.DTO.TestimonialDTO;
import com.example.dissertation_backend.solution.Testimonial.Model.Testimonial;
import com.example.dissertation_backend.solution.Testimonial.Repository.TestimonialRepo;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestimonialService {

  @Autowired
  private TestimonialRepo testimonialRepo;

  public List<TestimonialDTO> getAllTestimonialDTOs() {
    List<Testimonial> testimonials = testimonialRepo.findAll();
    return testimonials
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<TestimonialDTO> getAllApprovedTestimonialDTOs() {
    List<Testimonial> testimonials = testimonialRepo
      .findAll()
      .stream()
      .filter(Testimonial::getIsApproved)
      .collect(Collectors.toList());
    return testimonials
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public Optional<TestimonialDTO> getTestimonialByIdDTOs(Integer id) {
    if (id == null) {
      return null;
    }
    Optional<Testimonial> testimonial = testimonialRepo.findById(id);
    return testimonial.map(this::convertToDTO);
  }

  public Optional<TestimonialDTO> approveTestimonial(Integer id) {
    if (id == null) {
      return null;
    }
    Optional<Testimonial> testimonialOpt = testimonialRepo.findById(id);
    if (testimonialOpt.isPresent()) {
      Testimonial testimonial = testimonialOpt.get();
      testimonial.setIsApproved(true);
      testimonialRepo.save(testimonial);
      return Optional.of(convertToDTO(testimonial));
    }
    return Optional.empty();
  }

  public List<TestimonialDTO> getTestimonialsByUserId(Integer userId) {
    if (userId == null) {
      return Collections.emptyList();
    }
    List<Testimonial> testimonials = testimonialRepo.findByApplicationUser_UserId(
      userId
    );
    return testimonials
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public Testimonial saveOrUpdateTestimonial(Testimonial testimonial) {
    if (testimonial == null) {
      return null;
    }
    return testimonialRepo.save(testimonial);
  }

  // Deletes the specified review from the database
  public void deleteTestimonial(Integer id) {
    if (id == null) {
      return;
    }
    testimonialRepo.deleteById(id);
  }

  private TestimonialDTO convertToDTO(Testimonial testimonial) {
    TestimonialDTO dto = new TestimonialDTO();
    dto.setTestimonialId(testimonial.getTestimonialId());
    dto.setTestimonialTitle(testimonial.getTestimonialTitle());
    dto.setRating(testimonial.getRating());
    dto.setComment(testimonial.getTestimonial());
    dto.setTestimonialDate(testimonial.getTestimonialDate());
    dto.setApplicationUser(
      convertApplicationUserDTO(testimonial.getApplicationUser())
    );
    dto.setIsApproved(testimonial.getIsApproved());

    return dto;
  }

  private ApplicationUserDTO convertApplicationUserDTO(
    ApplicationUser applicationUser
  ) {
    ApplicationUserDTO dto = new ApplicationUserDTO();
    dto.setUserId(applicationUser.getUserId());
    dto.setFirstname(applicationUser.getFirstname());
    dto.setLastname(applicationUser.getLastname());
    dto.setUsername(applicationUser.getUsername());
    dto.setPassword(applicationUser.getPassword());
    dto.setUser_email(applicationUser.getUser_email());
    dto.setBankAccountNo(applicationUser.getBankAccountNo());
    dto.setBankSortCode(applicationUser.getBankSortCode());
    dto.setContactTelephone(applicationUser.getContactTelephone());
    dto.setContactAddress(applicationUser.getContactAddress());
    dto.setAuthorities(applicationUser.getAuthorities());
    dto.setDateJoined(applicationUser.getDateJoined());

    return dto;
  }
}
