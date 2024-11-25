package com.example.dissertation_backend.solution.ShoppingCart.Service;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.CartItemDTO;
import com.example.dissertation_backend.solution.DTO.CategoryDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.DTO.ShoppingCartDTO;
import com.example.dissertation_backend.solution.Products.Model.ProductAttributes;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.Products.Repository.ProductAttributeRepository;
import com.example.dissertation_backend.solution.Products.Repository.ProductRepository;
import com.example.dissertation_backend.solution.ShoppingCart.Model.CartItem;
import com.example.dissertation_backend.solution.ShoppingCart.Model.ShoppingCart;
import com.example.dissertation_backend.solution.ShoppingCart.Repository.CartItemRepository;
import com.example.dissertation_backend.solution.ShoppingCart.Repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

  @Autowired
  private CartItemRepository cartItemRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ShoppingCartRepository shoppingCartRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductAttributeRepository productAttributeRepository;

  @Transactional
  public ShoppingCart getOrCreateCart(ApplicationUser user) {
    Optional<ShoppingCart> existingCart = shoppingCartRepository.findByUser(
      user
    );
    if (!existingCart.isPresent()) {
      try {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);
        newCart.setDateCreated(LocalDateTime.now());
        return shoppingCartRepository.save(newCart);
      } catch (DataIntegrityViolationException e) {
        // Handling the case where user cannot create a new cart
        return shoppingCartRepository
          .findByUser(user)
          .orElseThrow(() ->
            new RuntimeException("Unable to retrieve or create cart")
          );
      }
    }
    return existingCart.get();
  }

  public CartItemDTO addProductToCart(
    ApplicationUser user,
    Integer productId,
    Integer quantity,
    List<Integer> attributeIds
  ) {
    ShoppingCart cart = getOrCreateCart(user);

    // Fetch the product
    Products product = productRepository
      .findById(productId)
      .orElseThrow(() -> new RuntimeException("Product not found"));

    // Checking if there is enough stock available
    if (product.getProductStockQuantity() < quantity) {
      throw new RuntimeException("Not enough stock available");
    }

    // Determine the price to use (discounted price if available and valid, otherwise the original price)
    double pricePerItem = (
        product.getProductDiscount() != null && product.getProductDiscount() > 0
      )
      ? product.getProductDiscount()
      : product.getProductPrice();

    Optional<CartItem> existingCartItem = cart
      .getCartItems()
      .stream()
      .filter(item -> item.getProduct().getProductId().equals(productId))
      .findFirst();

    // Now handle the product attributes

    Set<ProductAttributes> selectedAttributes = new HashSet<>();
    for (Integer attributeId : attributeIds) {
      ProductAttributes attribute = productAttributeRepository
        .findById(attributeId)
        .orElseThrow(() -> new RuntimeException("Attribute not found"));
      selectedAttributes.add(attribute);
    }

    CartItemDTO cartItemDTO;
    if (existingCartItem.isPresent()) {
      CartItem cartItem = existingCartItem.get();
      int newQuantity = cartItem.getQuantity() + quantity;

      if (product.getProductStockQuantity() >= newQuantity) {
        BigDecimal totalProductPrice = BigDecimal
          .valueOf(pricePerItem)
          .multiply(BigDecimal.valueOf(newQuantity))
          .setScale(2, RoundingMode.HALF_UP);

        cartItem.setQuantity(newQuantity);
        cartItem.setTotalProductPrice(totalProductPrice.doubleValue());
        cartItemDTO = convertCartItemToDTO(cartItemRepository.save(cartItem));
      } else {
        throw new RuntimeException(
          "Not enough stock available to add the requested quantity"
        );
      }
    } else {
      CartItem cartItem = new CartItem();
      cartItem.setProduct(product);
      cartItem.setShoppingCart(cart);
      cartItem.setQuantity(quantity);

      BigDecimal totalProductPrice = BigDecimal
        .valueOf(pricePerItem)
        .multiply(BigDecimal.valueOf(quantity))
        .setScale(2, RoundingMode.HALF_UP);

      cartItem.setTotalProductPrice(totalProductPrice.doubleValue());
      cartItem.setSelectedAttributes(selectedAttributes);
      cartItemDTO = convertCartItemToDTO(cartItemRepository.save(cartItem));
    }

    return cartItemDTO;
  }

  public CartItemDTO updateCartItem(Integer cartItemId, Integer newQuantity) {
    if (cartItemId == null) {
      throw new RuntimeException("Cart item id must be provided");
    }

    CartItem cartItem = cartItemRepository
      .findById(cartItemId)
      .orElseThrow(() -> new RuntimeException("Cart item not found"));

    if (newQuantity <= 0) {
      throw new RuntimeException("Quantity must be greater than zero");
    }

    Products product = cartItem.getProduct();
    if (newQuantity > product.getProductStockQuantity()) {
      throw new RuntimeException("Insufficient stock available");
    }

    cartItem.setQuantity(newQuantity);

    // Determine the price per item based on discount
    double pricePerItem = (
        product.getProductDiscount() != null && product.getProductDiscount() > 0
      )
      ? product.getProductDiscount()
      : product.getProductPrice();

    // Use BigDecimal for precise price calculation
    BigDecimal totalProductPrice = BigDecimal
      .valueOf(pricePerItem)
      .multiply(BigDecimal.valueOf(newQuantity))
      .setScale(2, RoundingMode.HALF_UP);

    cartItem.setTotalProductPrice(totalProductPrice.doubleValue());
    CartItem updatedCartItem = cartItemRepository.save(cartItem);

    return convertCartItemToDTO(updatedCartItem);
  }

  public void removeCartItem(ApplicationUser user, Integer cartItemId) {
    if (cartItemId == null) {
      throw new IllegalArgumentException("Cart item ID must not be null");
    }

    // Fetch the cart item
    CartItem cartItem = cartItemRepository
      .findById(cartItemId)
      .orElseThrow(() -> new RuntimeException("Cart item not found"));

    // Check if the cart item belongs to the user's cart
    if (!cartItem.getShoppingCart().getUser().equals(user)) {
      throw new RuntimeException(
        "Access denied: You do not own this cart item"
      );
    }

    // Remove the cart item
    cartItemRepository.delete(cartItem);
  }

  public ShoppingCartDTO getCartByUsername(String username) {
    ApplicationUser user = userRepository
      .findByUsername(username)
      .orElseThrow(() -> new RuntimeException("User not found"));

    ShoppingCart cart = getOrCreateCart(user);
    return convertToDTO(cart);
  }

  private CartItemDTO convertCartItemToDTO(CartItem cartItem) {
    CartItemDTO cartItemDTO = new CartItemDTO();
    cartItemDTO.setCartItemId(cartItem.getCartItemId());
    cartItemDTO.setProduct(convertProductToDTO(cartItem.getProduct()));
    cartItemDTO.setQuantity(cartItem.getQuantity());
    cartItemDTO.setTotalProductPrice(cartItem.getTotalProductPrice());
    return cartItemDTO;
  }

  private ShoppingCartDTO convertToDTO(ShoppingCart cart) {
    ShoppingCartDTO cartDTO = new ShoppingCartDTO();
    cartDTO.setCartId(cart.getCartId());

    List<CartItemDTO> cartItemDTOs = cart
      .getCartItems()
      .stream()
      .map(this::convertCartItemToDTO)
      .collect(Collectors.toList());

    cartDTO.setCartItems(cartItemDTOs);

    return cartDTO;
  }

  private ProductDTO convertProductToDTO(Products product) {
    Set<String> imageUrls = product
      .getImages()
      .stream()
      .map(ProductImages::getImageUrl)
      .collect(Collectors.toSet());

    ProductDTO productDTO = new ProductDTO();
    productDTO.setProductId(product.getProductId());
    productDTO.setProductName(product.getProductName());
    productDTO.setProductPrice(product.getProductPrice());
    productDTO.setProductStockQuantity(product.getProductStockQuantity());
    productDTO.setProductDescription(product.getProductDescription());
    productDTO.setProductDiscount(product.getProductDiscount());
    productDTO.setImageUrls(imageUrls);
    productDTO.setDateTimeListed(product.getDateListed());
    productDTO.setDateTimeUpdated(product.getDateTimeUpdated());

    ArtisanProfile artisanProfile = product.getArtisan();
    ArtisanProfileDTO artisanProfileDTO = convertArtisanProfileToDTO(
      artisanProfile
    );
    productDTO.setArtisanProfile(artisanProfileDTO);

    Category category = product.getCategory();
    CategoryDTO categoryDTO = convertCategoryToDTO(category);
    productDTO.setCategory(categoryDTO);

    return productDTO;
  }

  private ArtisanProfileDTO convertArtisanProfileToDTO(
    ArtisanProfile artisanProfile
  ) {
    ArtisanProfileDTO artisanProfileDTO = new ArtisanProfileDTO();
    artisanProfileDTO.setArtisanId(artisanProfile.getArtisanId());
    artisanProfileDTO.setBio(artisanProfile.getBio());
    artisanProfileDTO.setProfilePicture(artisanProfile.getProfilePicture());
    artisanProfileDTO.setLocation(artisanProfile.getLocation());

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
