package com.example.dissertation_backend.solution.Review.Service;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.DTO.ApplicationUserDTO;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.CategoryDTO;
import com.example.dissertation_backend.solution.DTO.ProductAttributeDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.DTO.ReviewDTO;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Review.Model.Review;
import com.example.dissertation_backend.solution.Review.Repository.ReviewRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  @Autowired
  private ReviewRepository reviewRepository;

  public List<ReviewDTO> getAllReviewDTOs() {
    List<Review> review = reviewRepository.findAll();
    return review.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public Optional<ReviewDTO> getReviewByIdDTOs(Integer id) {
    if (id == null) {
      return null;
    }
    Optional<Review> review = reviewRepository.findById(id);
    return review.map(this::convertToDTO);
  }

  public Review addReview(Review review) {
    if (review == null) {
      return null;
    }
    return reviewRepository.save(review);
  }

  public Optional<Review> getReviewById(Integer reviewId) {
    if (reviewId == null) {
      return Optional.empty();
    }
    return reviewRepository.findById(reviewId);
  }

  public Optional<Review> updateReview(
    Integer reviewId,
    @NonNull Review reviewDetails
  ) {
    if (reviewId == null) {
      return Optional.empty();
    }

    // Find the review and throw an exception if not found
    Review existingReview = reviewRepository
      .findById(reviewId)
      .orElseThrow(() -> new NoSuchElementException("Review not found"));

    // Update the existing review with details from reviewDetails
    existingReview.setComment(reviewDetails.getComment());
    existingReview.setRating(reviewDetails.getRating());

    reviewRepository.save(existingReview);
    return Optional.of(existingReview);
  }

  public boolean deleteReview(Integer reviewId) {
    if (reviewId == null) {
      return false;
    }
    Optional<Review> review = reviewRepository.findById(reviewId);
    if (review.isPresent()) {
      review.ifPresent(reviewRepository::delete);
      return true;
    }
    return false;
  }

  public List<ReviewDTO> getReviewsByProductId(Integer productId) {
    if (productId == null) {
      return null;
    }
    List<Review> review = reviewRepository.findByProducts_ProductId(productId);
    return review.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public List<ReviewDTO> getReviewsByUserId(Integer userId) {
    if (userId == null) {
      return null;
    }
    List<Review> review = reviewRepository.findByApplicationUser_UserId(userId);
    return review.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public List<ReviewDTO> getReviewsByArtisanId(Integer artisanId) {
    if (artisanId == null) {
      return null;
    }
    List<Review> review = reviewRepository.findByArtisan_ArtisanId(artisanId);
    return review.stream().map(this::convertToDTO).collect(Collectors.toList());
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
    if (product != null) {
      Set<String> imageUrls = product
        .getImages()
        .stream()
        .map(ProductImages::getImageUrl)
        .collect(Collectors.toSet());

      dto.setProductId(product.getProductId());
      dto.setProductName(product.getProductName());
      dto.setProductDescription(product.getProductDescription());
      dto.setProductPrice(product.getProductPrice());
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
    }

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
