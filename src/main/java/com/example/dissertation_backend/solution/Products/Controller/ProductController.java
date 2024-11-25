package com.example.dissertation_backend.solution.Products.Controller;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Category.Service.CategoryService;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Service.ArtisanProfileService;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.Exception.ImageStorageException;
import com.example.dissertation_backend.solution.Products.Model.ProductAttributes;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Service.ProductImageService;
import com.example.dissertation_backend.solution.Products.Service.ProductServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
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
public class ProductController {

  @Autowired
  private ProductServices productService;

  @Autowired
  private ProductImageService productImageService;

  @Autowired
  private UserService userService;

  @Autowired
  private ArtisanProfileService artisanProfileService;

  @Autowired
  private CategoryService categoryService;

  @GetMapping("/search")
  public ResponseEntity<Page<ProductDTO>> searchProducts(
    @RequestParam(required = false) String searchTerm,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(required = false) Integer minRating,
    @RequestParam(required = false) List<Integer> categoryIds,
    @RequestParam(required = false) List<Integer> artisanIds,
    @PageableDefault(size = 10) Pageable pageable
  ) {
    Page<ProductDTO> productPage = productService.searchProducts(
      searchTerm,
      minPrice,
      maxPrice,
      categoryIds,
      artisanIds,
      minRating,
      pageable
    );
    return ResponseEntity.ok(productPage);
  }

  @GetMapping
  public List<ProductDTO> getAllProducts() {
    return productService.getAllProductDTOs();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
    return productService
      .getProductByIdDTOs(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<ProductDTO>> getProductsByParentCategory(
    @PathVariable Integer categoryId
  ) {
    List<ProductDTO> products = productService.getProductsByParentCategory(
      categoryId
    );
    return ResponseEntity.ok(products);
  }

  @GetMapping("/subcategory/{subCategoryId}")
  public ResponseEntity<List<ProductDTO>> getProductsBySubCategory(
    @PathVariable Integer subCategoryId
  ) {
    List<ProductDTO> products = productService.getProductsBySubCategory(
      subCategoryId
    );
    return ResponseEntity.ok(products);
  }

  @SuppressWarnings("null")
  @GetMapping("/parentWithSubCategory/{categoryId}")
  public ResponseEntity<List<ProductDTO>> getProductsByCategoryIncludingSubcategories(
    @PathVariable Integer categoryId
  ) {
    try {
      List<ProductDTO> products = productService.getProductsByCategoryAndSubcategories(
        categoryId
      );
      if (products.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/artisan/{artisanId}")
  public List<ProductDTO> getProductsByArtisanId(
    @PathVariable Integer artisanId
  ) {
    return productService.getProductsByArtisanId(artisanId);
  }

  @PostMapping
  public ResponseEntity<Object> createProduct(
    Principal principal,
    @RequestParam("product") String productStr,
    @RequestParam("attributes") String attributesStr,
    @RequestParam("images") MultipartFile[] files
  ) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Products product = mapper.readValue(productStr, Products.class);
      List<ProductAttributes> attributes = Arrays.asList(
        mapper.readValue(attributesStr, ProductAttributes[].class)
      );

      ApplicationUser user = userService.findByUsername(principal.getName());
      if (
        !user
          .getAuthorities()
          .stream()
          .anyMatch(role ->
            role.getAuthority().equals("ARTISAN") ||
            role.getAuthority().equals("ADMIN")
          )
      ) {
        return ResponseEntity
          .status(HttpStatus.FORBIDDEN)
          .body("Access denied: User is not authorized to create products");
      }

      ArtisanProfile artisan = artisanProfileService
        .findByArtisan(user)
        .orElseThrow(() ->
          new RuntimeException("Artisan profile not found for user")
        );

      Category category = categoryService
        .getCategoryById(product.getCategory().getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

      product.setCategory(category);
      product.setArtisan(artisan);
      product.setDateListed(LocalDateTime.now());
      Products createdProduct = productService.saveOrUpdateProduct(
        product,
        attributes
      );

      for (MultipartFile file : files) {
        byte[] resizedImage = file.getBytes();
        String directoryName = "uploads";
        Path uploadDirPath = Paths.get(directoryName).toAbsolutePath();
        Files.createDirectories(uploadDirPath);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null &&
          originalFilename.contains(".")
          ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
          : "";
        String fileName =
          System.currentTimeMillis() +
          "_" +
          file.getOriginalFilename() +
          (fileExtension.isEmpty() ? "" : "." + fileExtension);

        Path destinationFilePath = uploadDirPath.resolve(fileName);
        Files.write(destinationFilePath, resizedImage);
        String imageUrl = "/" + directoryName + "/" + fileName;

        ProductImages productImage = new ProductImages(
          createdProduct,
          imageUrl
        );
        productImageService.saveImage(productImage);
      }
      return ResponseEntity.ok(createdProduct);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Failed to create product");
    }
  }

  @PostMapping("/{productId}/images")
  public ResponseEntity<?> uploadProductImages(
    @PathVariable Integer productId,
    @RequestParam("images") MultipartFile[] files,
    Principal principal
  ) {
    Products product = productService
      .getProductById(productId)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
      );

    ApplicationUser user = userService.findByUsername(principal.getName());

    Optional<ArtisanProfile> artisanProfileOpt = artisanProfileService.findByArtisan(
      user
    );
    if (
      !artisanProfileOpt.isPresent() ||
      !product.getArtisan().getArtisan().equals(user)
    ) {
      return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("Access denied: User does not own this product");
    }

    for (MultipartFile file : files) {
      try {
        // Process file - you can resize or convert format if needed
        byte[] resizedImage = file.getBytes();

        // The directory name
        String directoryName = "uploads";

        // Ensure directory exists or create it
        Path uploadDirPath = Paths.get(directoryName).toAbsolutePath();
        Files.createDirectories(uploadDirPath);

        // Extract file extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
          fileExtension =
            originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        // Generate a unique filename using the current time and the original filename
        String fileName =
          System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Resolve the file extension if necessary
        if (!fileExtension.isEmpty()) {
          fileName = fileName + "." + fileExtension;
        }

        Path destinationFilePath = uploadDirPath.resolve(fileName);

        // Write the resized image to the file
        Files.write(destinationFilePath, resizedImage);

        // Generate the retrievable URL or relative path
        String imageUrl = "/" + directoryName + "/" + fileName;

        // Create ProductImages instance and save
        ProductImages productImage = new ProductImages(product, imageUrl);
        productImageService.saveImage(productImage);
      } catch (IOException ex) {
        throw new ImageStorageException("Error processing image", ex);
      }
    }
    return ResponseEntity.ok("Images uploaded successfully");
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateProduct(
    @PathVariable Integer id,
    @RequestParam("product") String productJson,
    @RequestParam("attributes") String attributesStr,
    Principal principal
  ) throws IOException {
    Products updatedProducts = new ObjectMapper()
      .readValue(productJson, Products.class);
    List<ProductAttributes> newAttributes = Arrays.asList(
      new ObjectMapper().readValue(attributesStr, ProductAttributes[].class)
    );
    Optional<Products> existingProduct = productService.getProductById(id);

    if (existingProduct != null) {
      updatedProducts.setProductId(id);

      // updatedProducts.setAttributes(new HashSet<>(newAttributes));
      updatedProducts.setProductPrice(
        updatedProducts.calculatePriceWithCustomizations()
      );

      // Getting the user from user service
      ApplicationUser user = userService.findByUsername(principal.getName());

      boolean isArtisanOrAdmin = user
        .getAuthorities()
        .stream()
        .anyMatch(role ->
          role.getAuthority().equals("ARTISAN") ||
          role.getAuthority().equals("ADMIN")
        );

      // Check if the user is not an admin and not the owner of the product
      ArtisanProfile artisan = existingProduct.get().getArtisan();
      if (
        !isArtisanOrAdmin &&
        (artisan == null || !artisan.getArtisan().equals(user))
      ) {
        return ResponseEntity
          .status(HttpStatus.FORBIDDEN)
          .body(
            "Access denied: User is not an admin or user does not own the product"
          );
      }

      Optional<ArtisanProfile> artisanProfileOpt = artisanProfileService.findByArtisan(
        user
      );

      if (artisanProfileOpt.isPresent()) {
        artisan = artisanProfileOpt.get();
      } else if (isArtisanOrAdmin) {
        // Create a new ArtisanProfile for the admin
        artisan =
          new ArtisanProfile(
            user,
            "Admin's bio",
            null,
            "Northampton",
            "CraftWise Artisans"
          );
        artisanProfileService.saveOrUpdateArtisanProfile(artisan);
      } else {
        throw new RuntimeException("Artisan profile not found for user");
      }

      // Getting the category it is set to
      Category category = categoryService
        .getCategoryById(updatedProducts.getCategory().getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

      updatedProducts.setCategory(category);
      updatedProducts.setArtisan(artisan);
      updatedProducts.setDateListed(existingProduct.get().getDateListed());
      updatedProducts.setDateTimeUpdated(LocalDateTime.now());
      return ResponseEntity.ok(
        productService.saveOrUpdateProduct(updatedProducts, newAttributes)
      );
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(
    @PathVariable Integer id,
    Principal principal
  ) {
    // Assuming 'findByUsername' method returns a user object with roles and other details
    ApplicationUser currentUser = userService.findByUsername(
      principal.getName()
    );

    // Check if the user is an admin
    boolean isAdmin = currentUser
      .getAuthorities()
      .stream()
      .anyMatch(role -> role.getAuthority().equals("ADMIN"));

    // Retrieve the product to check for ownership
    Optional<Products> product = productService.getProductById(id);
    if (product.isPresent()) {
      // Check if the current user is the owner of the product or an admin
      boolean isOwner = product
        .get()
        .getArtisan()
        .getArtisan()
        .getUsername()
        .equals(principal.getName());
      if (isAdmin || isOwner) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
      } else {
        // User is neither admin nor owner
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } else {
      // Product not found
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // Image Resizing
  public byte[] resizeImage(byte[] imageBytes, int width, int height)
    throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails
      .of(new ByteArrayInputStream(imageBytes))
      .size(width, height)
      .toOutputStream(outputStream);
    return outputStream.toByteArray();
  }
}
