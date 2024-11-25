package com.example.dissertation_backend.solution.Customers.Controllers;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Category.Repository.CategoryRepository;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
// import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Model.Roles;
// import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
// import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
// import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.Products.Service.ProductServices;
import com.example.dissertation_backend.solution.Testimonial.Model.Testimonial;
import com.example.dissertation_backend.solution.Testimonial.Repository.TestimonialRepo;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.PATCH,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class AdminController {

  @Autowired
  private UserService userService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductServices productServices;

  @Autowired
  private TestimonialRepo testimonialRepo;

  @GetMapping("/")
  public String helloAdminController() {
    return "Admin access level";
  }

  @GetMapping
  public List<ProductDTO> getAllProducts() {
    return productServices.getAllProductDTOs();
  }

  @GetMapping("/allUsers")
  public List<ApplicationUser> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
    try {
      ApplicationUser user = userService.getUserById(userId);
      if (user != null) {
        return ResponseEntity.ok(user);
      } else {
        return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("User not found");
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/allCategories")
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @GetMapping("/allRoles")
  public List<Roles> getAllRoles() {
    return userService.getAllRoles();
  }

  // Update user details and roles by Admin
  @PutMapping("/updateUser/{userId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<?> updateUserByAdmin(
    @PathVariable Integer userId,
    @RequestBody ApplicationUser updatedUser
  ) {
    try {
      ApplicationUser user = userService.updateUserByAdmin(userId, updatedUser);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/approve-artisan/{userId}")
  public ResponseEntity<?> approveArtisanRequest(@PathVariable Integer userId) {
    userService.approveArtisanRequest(userId);
    return ResponseEntity.ok("User approved as an artisan.");
  }

  @PatchMapping("/approveTestimonial/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<?> approveTestimonial(
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
    testimonial.setIsApproved(true);
    testimonialRepo.save(testimonial);

    return ResponseEntity.ok().build();
  }
}
