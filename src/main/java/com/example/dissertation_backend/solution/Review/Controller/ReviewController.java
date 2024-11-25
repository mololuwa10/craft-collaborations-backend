package com.example.dissertation_backend.solution.Review.Controller;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
import com.example.dissertation_backend.solution.DTO.ApplicationUserDTO;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.CategoryDTO;
import com.example.dissertation_backend.solution.DTO.ProductAttributeDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.DTO.ReviewDTO;
import com.example.dissertation_backend.solution.Orders.Service.OrderService;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Service.ProductServices;
import com.example.dissertation_backend.solution.Review.Model.Review;
import com.example.dissertation_backend.solution.Review.Repository.ReviewRepository;
import com.example.dissertation_backend.solution.Review.Service.ReviewService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
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
public class ReviewController {

  @Autowired
  private ReviewService reviewService;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ProductServices productServices;

  @Autowired
  private ArtisanProfileService artisanProfileService;

  @Autowired
  private OrderService orderService;

  @GetMapping
  public List<ReviewDTO> getAllReviews() {
    return reviewService.getAllReviewDTOs();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Integer id) {
    return reviewService
      .getReviewByIdDTOs(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/product/{productId}")
  public List<ReviewDTO> getReviewByProductId(@PathVariable Integer productId) {
    return reviewService.getReviewsByProductId(productId);
  }

  @GetMapping("/user/{userId}")
  public List<ReviewDTO> getReviewByUserId(@PathVariable Integer userId) {
    return reviewService.getReviewsByUserId(userId);
  }

  @GetMapping("/artisan/{artisanId}")
  public List<ReviewDTO> getReviewsByArtisanId(
    @PathVariable Integer artisanId
  ) {
    return reviewService.getReviewsByArtisanId(artisanId);
  }

  @PostMapping("/product/{productId}")
  public ResponseEntity<Object> addProductReview(
    @PathVariable Integer productId,
    @RequestBody ReviewDTO reviewDTO,
    Principal principal
  ) {
    // Check if a user is logged in
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a review.");
    }

    // Retrieve the logged-in user's details
    ApplicationUser user = userService.findByUsername(principal.getName());
    if (user == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a review.");
    }

    // Verify that the product exists
    Optional<Products> productOpt = productServices.getProductById(productId);
    if (!productOpt.isPresent()) {
      return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("Product not found.");
    }

    Products product = productOpt.get();

    // Check if the user has ordered and received the product
    boolean canAddReview = orderService.hasUserOrderedAndReceivedProduct(
      user,
      product
    );
    if (!canAddReview) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(
          "You can only add a review for products that you have ordered and received."
        );
    }

    // Create a new Review object and set its fields
    Review review = new Review();
    review.setApplicationUser(user);
    review.setProducts(product);
    review.setReviewTitle(reviewDTO.getReviewTitle());
    review.setRating(reviewDTO.getRating());
    review.setComment(reviewDTO.getComment());
    review.setReviewDate(LocalDateTime.now());

    // Add the review to the database
    Review newReview = reviewService.addReview(review);
    return ResponseEntity.ok(convertToDTO(newReview));
  }

  @PostMapping("/artisan/{artisanId}")
  public ResponseEntity<Object> addArtisanReview(
    @PathVariable Integer artisanId,
    @RequestBody ReviewDTO reviewDTO,
    Principal principal
  ) {
    // Similar logic to addReview, but for artisan reviews
    // Check if a user is logged in
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a review.");
    }

    // Retrieve the logged-in user's details
    ApplicationUser user = userService.findByUsername(principal.getName());
    if (user == null) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You must be logged in to add a review.");
    }

    // Verify that the artisan exists
    Optional<ArtisanProfile> artisanProfile = artisanProfileService.getArtisanProfileById(
      artisanId
    );
    if (!artisanProfile.isPresent()) {
      return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("Artisan not found.");
    }

    // Create a new Review object and set its fields
    Review review = new Review();
    review.setApplicationUser(user);
    review.setArtisan(artisanProfile.get());
    review.setReviewTitle(reviewDTO.getReviewTitle());
    review.setRating(reviewDTO.getRating());
    review.setComment(reviewDTO.getComment());
    review.setReviewDate(LocalDateTime.now());

    // Add the review to the database
    Review newReview = reviewService.addReview(review);
    return ResponseEntity.ok(convertToDTO(newReview));
  }

  @PutMapping("/{reviewId}")
  public ResponseEntity<Object> updateReview(
    @PathVariable Integer reviewId,
    @RequestBody Review reviewDetails,
    Principal principal
  ) {
    if (reviewId == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
    if (!reviewOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Review existingReview = reviewOpt.get();
    ApplicationUser user = userService.findByUsername(principal.getName());

    if (user == null || !existingReview.getApplicationUser().equals(user)) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("Access denied: You did not create this review");
    }

    // Check if the product in the reviewDetails matches the existing one
    if (
      reviewDetails.getProducts() != null &&
      !reviewDetails
        .getProducts()
        .getProductId()
        .equals(existingReview.getProducts().getProductId())
    ) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("Product change in review is not allowed");
    }

    // Updating the review details here
    if (reviewDetails.getRating() != null) {
      existingReview.setRating(reviewDetails.getRating());
    }
    if (reviewDetails.getComment() != null) {
      existingReview.setComment(reviewDetails.getComment());
    }

    // Checking if the reviewDetails has a review date
    if (reviewDetails.getReviewDate() != null) {
      existingReview.setReviewDate(reviewDetails.getReviewDate());
    } else {
      existingReview.setReviewDate(LocalDateTime.now());
    }

    reviewRepository.save(existingReview);
    return ResponseEntity.ok().body(existingReview);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<?> deleteReview(
    @PathVariable Integer reviewId,
    Principal principal
  ) {
    if (reviewId == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
    if (!reviewOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Review review = reviewOpt.get();
    ApplicationUser currentUser = userService.findByUsername(
      principal.getName()
    );

    // Check if the current user is the one who created the review
    if (
      currentUser == null || !review.getApplicationUser().equals(currentUser)
    ) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("Access denied: You are not authorized to delete this review");
    }

    // Delete the review
    reviewRepository.delete(review);
    return ResponseEntity.ok().build();
  }

  private ReviewDTO convertToDTO(Review review) {
    ReviewDTO dto = new ReviewDTO();
    dto.setReviewId(review.getReviewId());
    dto.setReviewTitle(review.getReviewTitle());
    dto.setComment(review.getComment());
    dto.setRating(review.getRating());
    dto.setReviewDate(review.getReviewDate());
    if (review.getProducts() != null) {
      dto.setProducts(convertProductToDTO(review.getProducts()));
    }
    dto.setApplicationUser(
      convertApplicationUserDTO(review.getApplicationUser())
    );

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

  private ProductDTO convertProductToDTO(Products product) {
    ProductDTO dto = new ProductDTO();

    Set<String> imageUrls = product
      .getImages()
      .stream()
      .map(ProductImages::getImageUrl)
      .collect(Collectors.toSet());

    dto.setProductId(product.getProductId());
    dto.setProductName(product.getProductName());
    dto.setProductDescription(product.getProductDescription());
    dto.setDynamicPricing(product.calculatePriceWithCustomizations());
    dto.setProductStockQuantity(product.getProductStockQuantity());
    dto.setArtisanProfile(convertArtisanProfileToDTO(product.getArtisan()));
    dto.setCategory(convertCategoryToDTO(product.getCategory()));
    dto.setImageUrls(imageUrls);
    dto.setDateTimeListed(product.getDateListed());
    dto.setDateTimeUpdated(product.getDateTimeUpdated());
    Set<ProductAttributeDTO> attributeDTOs = product
      .getAttributes()
      .stream()
      .map(attr ->
        new ProductAttributeDTO(
          attr.getProductAttributesId(),
          attr.getProductAttributesKey(),
          attr.getProductAttributesValue(),
          attr.getAffectsPricing()
        )
      )
      .collect(Collectors.toSet());

    dto.setAttributes(attributeDTOs);

    return dto;
  }

  private ArtisanProfileDTO convertArtisanProfileToDTO(
    ArtisanProfile artisanProfile
  ) {
    ArtisanProfileDTO artisanProfileDTO = new ArtisanProfileDTO();
    artisanProfileDTO.setArtisanId(artisanProfile.getArtisanId());
    artisanProfileDTO.setBio(artisanProfile.getBio());
    artisanProfileDTO.setProfilePicture(artisanProfile.getProfilePicture());
    artisanProfileDTO.setLocation(artisanProfile.getLocation());
    artisanProfileDTO.setStoreBanner(artisanProfile.getStoreBanner());
    artisanProfileDTO.setAnnouncements(artisanProfile.getAnnouncements());
    artisanProfileDTO.setBusinessHours(artisanProfile.getBusinessHours());
    artisanProfileDTO.setGallery(artisanProfile.getGallery());
    artisanProfileDTO.setStories(artisanProfile.getStories());
    artisanProfileDTO.setSpecializations(artisanProfile.getSpecializations());
    artisanProfileDTO.setMaterialsUsed(artisanProfile.getMaterialsUsed());
    artisanProfileDTO.setServicesOffered(artisanProfile.getServicesOffered());
    artisanProfileDTO.setExperienceYears(artisanProfile.getExperienceYears());
    artisanProfileDTO.setShippingPolicies(artisanProfile.getShippingPolicies());
    artisanProfileDTO.setReturnPolicy(artisanProfile.getReturnPolicy());
    artisanProfileDTO.setPaymentOptions(artisanProfile.getPaymentOptions());
    artisanProfileDTO.setTermsConditions(artisanProfile.getTermsConditions());
    artisanProfileDTO.setPrivacyPolicy(artisanProfile.getPrivacyPolicy());
    artisanProfileDTO.setCommunicationPreferences(
      artisanProfile.getCommunicationPreferences()
    );
    artisanProfileDTO.setPreferredLanguage(
      artisanProfile.getPreferredLanguage()
    );

    // Map other fields from ApplicationUser to ArtisanProfileDTO as needed
    artisanProfileDTO.setFirstname(artisanProfile.getArtisan().getFirstname());
    artisanProfileDTO.setLastname(artisanProfile.getArtisan().getLastname());
    artisanProfileDTO.setUser_email(
      artisanProfile.getArtisan().getUser_email()
    );
    artisanProfileDTO.setBankAccountNo(
      artisanProfile.getArtisan().getBankAccountNo()
    );
    artisanProfileDTO.setBankSortCode(
      artisanProfile.getArtisan().getBankSortCode()
    );
    artisanProfileDTO.setContactTelephone(
      artisanProfile.getArtisan().getContactTelephone()
    );
    artisanProfileDTO.setContactAddress(
      artisanProfile.getArtisan().getContactAddress()
    );

    return artisanProfileDTO;
  }

  private CategoryDTO convertCategoryToDTO(Category category) {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setCategoryId(category.getCategoryId());
    categoryDTO.setCategoryName(category.getCategoryName());
    categoryDTO.setCategoryDescription(category.getCategoryDescription());
    categoryDTO.setCategoryImageUrl(category.getCategoryImageUrl());
    categoryDTO.setParentCategoryId(
      category.getParentCategory() != null
        ? category.getParentCategory().getCategoryId()
        : null
    );

    return categoryDTO;
  }
}
