package com.example.dissertation_backend.solution.Testimonial.Controller;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
import com.example.dissertation_backend.solution.DTO.ApplicationUserDTO;
import com.example.dissertation_backend.solution.DTO.TestimonialDTO;
import com.example.dissertation_backend.solution.Testimonial.Model.Testimonial;
import com.example.dissertation_backend.solution.Testimonial.Repository.TestimonialRepo;
import com.example.dissertation_backend.solution.Testimonial.Service.TestimonialService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class TestimonialController {

  @Autowired
  private TestimonialService testimonialService;

  @Autowired
  private UserService userService;

  @Autowired
  private TestimonialRepo testimonialRepo;

  @GetMapping
  public List<TestimonialDTO> getAllTestimonials() {
    return testimonialService.getAllTestimonialDTOs();
  }

  @GetMapping("/approvedTestimonials")
  public List<TestimonialDTO> getAllApprovedTestimonials() {
    return testimonialService.getAllApprovedTestimonialDTOs();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestimonialDTO> getTestimonialById(
    @PathVariable Integer id
  ) {
    Optional<TestimonialDTO> testimonialDtoOpt = testimonialService.getTestimonialByIdDTOs(
      id
    );

    // Check if the Optional contains a value
    if (testimonialDtoOpt.isPresent()) {
      // Return 200 OK with the testimonial DTO
      return ResponseEntity.ok(testimonialDtoOpt.get());
    } else {
      // Return 404 Not Found if the testimonial doesn't exist
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<TestimonialDTO>> getTestimonialsByUserId(
    @PathVariable Integer userId
  ) {
    List<TestimonialDTO> testimonials = testimonialService.getTestimonialsByUserId(
      userId
    );
    return ResponseEntity.ok(testimonials);
  }

  @PostMapping
  public ResponseEntity<Object> saveTestimonial(
    @RequestBody TestimonialDTO testimonialDTO,
    Principal principal
  ) {
    // Check if a user is logged in
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a testimonial.");
    }

    // Retrieve the logged-in user's details
    ApplicationUser user = userService.findByUsername(principal.getName());
    if (user == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a testimonial.");
    }

    // Create a new Testimonial object and set its fields
    Testimonial testimonial = new Testimonial();
    testimonial.setApplicationUser(user);
    testimonial.setTestimonialTitle(testimonialDTO.getTestimonialTitle());
    testimonial.setRating(testimonialDTO.getRating());
    testimonial.setTestimonial(testimonialDTO.getComment());
    testimonial.setTestimonialDate(LocalDateTime.now());
    testimonial.setIsApproved(false);

    // Add the review to the database
    Testimonial newTestimonial = testimonialService.saveOrUpdateTestimonial(
      testimonial
    );
    return ResponseEntity.ok(convertToDTO(newTestimonial));
  }

  @PutMapping("/user/{id}")
  public ResponseEntity<Object> updateTestimonial(
    @PathVariable Integer id,
    @RequestBody TestimonialDTO testimonialDTO,
    Principal principal
  ) {
    // Check if a user is logged in
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to update a testimonial.");
    }

    // Retrieve the logged-in user's details
    ApplicationUser user = userService.findByUsername(principal.getName());
    if (user == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to update a testimonial.");
    }

    // Fetch the existing testimonial
    if (id == null) {
      return null;
    }
    Optional<Testimonial> existingTestimonialOpt = testimonialRepo.findById(id);
    if (!existingTestimonialOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Testimonial existingTestimonial = existingTestimonialOpt.get();

    // Check if the current user is the one who created the testimonial
    if (!existingTestimonial.getApplicationUser().equals(user)) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(
          "Access denied: You are not authorized to update this testimonial"
        );
    }

    // Update the testimonial with new details
    existingTestimonial.setRating(testimonialDTO.getRating());
    existingTestimonial.setTestimonial(testimonialDTO.getComment());
    existingTestimonial.setTestimonialDate(LocalDateTime.now());

    // Save the updated testimonial
    Testimonial updatedTestimonial = testimonialService.saveOrUpdateTestimonial(
      existingTestimonial
    );

    return ResponseEntity.ok(convertToDTO(updatedTestimonial));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTestimonial(
    @PathVariable Integer id,
    Principal principal
  ) {
    if (id == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Optional<Testimonial> testimonialOpt = testimonialRepo.findById(id);
    if (!testimonialOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Testimonial testimonial = testimonialOpt.get();
    ApplicationUser currentUser = userService.findByUsername(
      principal.getName()
    );

    // Check if the current user is the one who created the testimonial
    if (
      currentUser == null ||
      !testimonial.getApplicationUser().equals(currentUser)
    ) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(
          "Access denied: You are not authorized to delete this testimonial"
        );
    }

    // Delete the testimonial
    testimonialRepo.delete(testimonial);
    return ResponseEntity.ok().build();
  }

  private TestimonialDTO convertToDTO(Testimonial testimonial) {
    TestimonialDTO dto = new TestimonialDTO();
    dto.setTestimonialId(testimonial.getTestimonialId());
    dto.setRating(testimonial.getRating());
    dto.setComment(testimonial.getTestimonial());
    dto.setTestimonialDate(testimonial.getTestimonialDate());
    dto.setApplicationUser(
      convertApplicationUserDTO(testimonial.getApplicationUser())
    );
    dto.setTestimonialTitle(testimonial.getTestimonialTitle());
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
