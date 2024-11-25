package com.example.dissertation_backend.solution.Products.Service;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Category.Repository.CategoryRepository;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.CategoryDTO;
import com.example.dissertation_backend.solution.DTO.ProductAttributeDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.Products.Model.ProductAttributes;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Repository.ProductAttributeRepository;
import com.example.dissertation_backend.solution.Products.Repository.ProductRepository;
import com.example.dissertation_backend.solution.Review.Model.Review;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
// Spring Data imports
import org.springframework.stereotype.Service;
// Utility imports
import org.springframework.util.StringUtils;

@Service
public class ProductServices {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductAttributeRepository attributeRepository;

  public Page<ProductDTO> searchProducts(
    String searchTerm,
    Double minPrice,
    Double maxPrice,
    List<Integer> categoryIds,
    List<Integer> artisanIds,
    Integer minRating,
    Pageable pageable
  ) {
    Specification<Products> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      String normalizedSearchTerm = searchTerm
        .replaceAll("[!.,]", "")
        .toLowerCase();

      // Search term related predicates
      if (StringUtils.hasText(searchTerm)) {
        List<Predicate> searchPredicates = new ArrayList<>();

        // Product name
        searchPredicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(root.get("productName")),
            "%" + normalizedSearchTerm + "%"
          )
        );

        // Category name
        Join<Products, Category> categoryJoin = root.join(
          "category",
          JoinType.LEFT
        );
        searchPredicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(categoryJoin.get("categoryName")),
            "%" + normalizedSearchTerm + "%"
          )
        );

        // Store name
        Join<Products, ArtisanProfile> artisanJoin = root.join(
          "artisan",
          JoinType.LEFT
        );
        searchPredicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(artisanJoin.get("storeName")),
            "%" + normalizedSearchTerm + "%"
          )
        );

        // Location filter
        Join<Products, ArtisanProfile> artisanLocationJoin = root.join(
          "artisan",
          JoinType.LEFT
        );
        searchPredicates.add(
          criteriaBuilder.like(
            criteriaBuilder.lower(artisanLocationJoin.get("location")),
            "%" + normalizedSearchTerm + "%"
          )
        );

        // Combine search-related predicates with OR
        predicates.add(
          criteriaBuilder.or(searchPredicates.toArray(new Predicate[0]))
        );
      }

      // Price range filter
      if (minPrice != null && maxPrice != null) {
        predicates.add(
          criteriaBuilder.between(root.get("productPrice"), minPrice, maxPrice)
        );
      }

      // Category ID filter
      if (categoryIds != null && !categoryIds.isEmpty()) {
        Join<Products, Category> categoryFilterJoin = root.join(
          "category",
          JoinType.INNER
        );
        predicates.add(categoryFilterJoin.get("categoryId").in(categoryIds));
      }

      // store name filter
      if (artisanIds != null && !artisanIds.isEmpty()) {
        Join<Products, ArtisanProfile> artisanProfileJoin = root.join(
          "artisan",
          JoinType.INNER
        );
        predicates.add(artisanProfileJoin.get("artisanId").in(artisanIds));
      }

      // Minimum rating filter
      if (minRating != null) {
        Subquery<Double> ratingSubquery = query.subquery(Double.class);
        Root<Review> reviewRoot = ratingSubquery.from(Review.class);
        ratingSubquery
          .select(criteriaBuilder.avg(reviewRoot.get("rating")))
          .where(
            criteriaBuilder.equal(
              reviewRoot.get("product"),
              root.get("productId")
            )
          );

        predicates.add(
          criteriaBuilder.greaterThanOrEqualTo(
            ratingSubquery,
            minRating.doubleValue()
          )
        );
      }

      query.distinct(true);
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    // Execute the query with the provided specification and pagination
    Page<Products> productsPage = productRepository.findAll(spec, pageable);

    // Convert the product entities to DTOs
    return productsPage.map(this::convertToDTO);
  }

  public List<ProductDTO> getAllProductDTOs() {
    List<Products> products = productRepository.findAll();
    return products
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public Optional<Products> getProductById(Integer id) {
    if (id == null) {
      return null;
    }
    Optional<Products> products = productRepository.findById(id);
    return products;
  }

  public Optional<ProductDTO> getProductByIdDTOs(Integer id) {
    if (id == null) {
      return null;
    }
    Optional<Products> products = productRepository.findById(id);
    return products.map(this::convertToDTO);
  }

  public List<ProductDTO> getProductsByParentCategory(Integer categoryId) {
    // This method assumes that products directly under a parent category do not belong to any subcategory
    List<Products> products = productRepository.findByCategory_CategoryId(
      categoryId
    );
    return products
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<ProductDTO> getProductsBySubCategory(Integer subCategoryId) {
    List<Products> products = productRepository.findByCategory_CategoryId(
      subCategoryId
    );
    return products
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<ProductDTO> getProductsByCategoryAndSubcategories(
    Integer categoryId
  ) {
    if (categoryId == null) {
      return new ArrayList<>();
    }

    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    if (!categoryOpt.isPresent()) {
      return new ArrayList<>();
    }

    Set<Integer> categoryIds = collectCategoryIdsIncludingSubcategories(
      categoryOpt.get()
    );
    List<Products> products = productRepository.findAllByCategory_CategoryIdIn(
      categoryIds
    );
    return products
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  public List<ProductDTO> getProductsByArtisanId(Integer artisanId) {
    List<Products> products = productRepository.findByArtisan_ArtisanId(
      artisanId
    );
    return products
      .stream()
      .map(this::convertToDTO)
      .collect(Collectors.toList());
  }

  @Transactional
  public Products saveOrUpdateProduct(
    Products product,
    List<ProductAttributes> newAttributes
  ) {
    if (product == null) {
      return null;
    }

    // Set attributes and link them back to the product
    for (ProductAttributes attribute : newAttributes) {
      attribute.setProduct(product);
      product.addAttribute(attribute); // Ensure this method sets both sides of the relationship
    }

    // Calculate dynamic pricing after all attributes are set
    product.setProductPrice(product.calculatePriceWithCustomizations());

    // Save the product, which now includes updated price and linked attributes
    return productRepository.save(product);
  }

  // @Transactional
  // public Products saveOrUpdateProduct(
  //   Products product,
  //   List<ProductAttributes> newAttributes
  // ) {
  //   if (product == null) {
  //     return null;
  //   }

  //   // Set attributes first and calculate dynamic pricing
  //   product.setAttributes(new HashSet<>(newAttributes));
  //   for (ProductAttributes attribute : newAttributes) {
  //     attribute.setProduct(product);
  //   }
  //   product.setProductPrice(product.calculatePriceWithCustomizations());

  //   // First, save the product to generate the product ID
  //   Products savedProduct = productRepository.save(product);

  //   // Now that the product is saved and has an ID, set and save the attributes
  //   newAttributes.forEach(attr -> {
  //     attr.setProduct(savedProduct);
  //     attributeRepository.save(attr);
  //   });

  //   savedProduct.setAttributes(new HashSet<>(newAttributes));
  //   return savedProduct;
  // }

  public void deleteProduct(Integer productId) {
    if (productId == null) {
      return;
    }
    attributeRepository.deleteByProductId(productId);
    productRepository.deleteById(productId);
  }

  private ArtisanProfileDTO convertArtisanProfileToDTO(
    ArtisanProfile artisanProfile
  ) {
    ArtisanProfileDTO artisanProfileDTO = new ArtisanProfileDTO();
    artisanProfileDTO.setArtisanId(artisanProfile.getArtisanId());
    artisanProfileDTO.setBio(artisanProfile.getBio());
    artisanProfileDTO.setProfilePicture(artisanProfile.getProfilePicture());
    artisanProfileDTO.setLocation(artisanProfile.getLocation());
    artisanProfileDTO.setStoreName(artisanProfile.getStoreName());
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

  private ProductDTO convertToDTO(Products product) {
    ProductDTO dto = new ProductDTO();

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
    dto.setProductDiscount(product.getProductDiscount());
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
    dto.setDynamicPricing(product.calculatePriceWithCustomizations());

    return dto;
  }

  private Set<Integer> collectCategoryIdsIncludingSubcategories(
    Category category
  ) {
    Set<Integer> categoryIds = new HashSet<>();
    collectCategoryIdsHelper(category, categoryIds);
    return categoryIds;
  }

  private void collectCategoryIdsHelper(
    Category category,
    Set<Integer> categoryIds
  ) {
    categoryIds.add(category.getCategoryId());
    for (Category subCategory : category.getSubCategories()) {
      collectCategoryIdsHelper(subCategory, categoryIds);
    }
  }
}
