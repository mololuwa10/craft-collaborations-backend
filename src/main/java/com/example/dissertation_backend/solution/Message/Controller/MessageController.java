package com.example.dissertation_backend.solution.Message.Controller;

import com.example.dissertation_backend.solution.Category.Model.Category;
import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Customers.Model.ArtisanProfile;
import com.example.dissertation_backend.solution.Customers.Repository.ArtisanProfileRepository;
import com.example.dissertation_backend.solution.Customers.Repository.UserRepository;
import com.example.dissertation_backend.solution.DTO.ArtisanProfileDTO;
import com.example.dissertation_backend.solution.DTO.CategoryDTO;
import com.example.dissertation_backend.solution.DTO.MessageDTO;
import com.example.dissertation_backend.solution.DTO.ProductDTO;
import com.example.dissertation_backend.solution.DTO.UserDetailsDTO;
import com.example.dissertation_backend.solution.Message.Model.Message;
import com.example.dissertation_backend.solution.Message.Service.MessageService;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.WebSocket.Chat.ChatMessage;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/messages")
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
public class MessageController {

  @Autowired
  private MessageService messageService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ArtisanProfileRepository artisanProfileRepository;

  @GetMapping("/unread-count")
  public ResponseEntity<?> getUnreadMessageCount(Principal principal) {
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("You must be logged in to view messages.");
    }

    ApplicationUser currentUser = userRepository
      .findByUsername(principal.getName())
      .orElseThrow(() -> new RuntimeException("User not found"));

    Long unreadCount = messageService.countUnreadMessages(currentUser);

    return ResponseEntity.ok(
      Collections.singletonMap("unreadCount", unreadCount)
    );
  }

  // @SuppressWarnings("null")
  @GetMapping("/mark-read/{senderId}/{receiverId}")
  public ResponseEntity<?> markMessagesAsRead(
    @PathVariable Integer senderId,
    @PathVariable Integer receiverId,
    Principal principal
  ) {
    if (principal == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    ApplicationUser currentUser = userRepository
      .findByUsername(principal.getName())
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
      );

    List<ChatMessage> updatedChatMessages = messageService.markMessagesAsRead(
      senderId,
      receiverId,
      currentUser
    );

    return ResponseEntity.ok(updatedChatMessages);
  }

  @GetMapping("/history/{userId}")
  public ResponseEntity<List<ChatMessage>> getMessageHistory(
    @PathVariable Integer userId,
    @RequestParam Integer otherUserId,
    @RequestParam(required = false) Integer productId
  ) {
    ApplicationUser currentUser = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
      );

    ApplicationUser otherUser = userRepository
      .findById(otherUserId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Other user not found"
        )
      );

    // Fetch additional details, like ArtisanProfile, for the other user.
    ArtisanProfile otherUserProfile = fetchArtisanProfile(otherUserId);
    List<Message> messages;

    if (productId != null) {
      messages =
        messageService.getMessageHistoryByProduct(
          currentUser,
          otherUser,
          productId
        );
    } else {
      messages = messageService.getMessageHistory(currentUser, otherUser);
    }
    // Convert messages to ChatMessage DTOs
    List<ChatMessage> chatMessages = messages
      .stream()
      .map(message -> {
        boolean isCurrentUserSender = message.getSender().equals(currentUser);
        ChatMessage.MessageType type = isCurrentUserSender
          ? ChatMessage.MessageType.SENT
          : ChatMessage.MessageType.RECEIVED;
        String senderUsername = isCurrentUserSender
          ? currentUser.getUsername()
          : otherUser.getUsername();

        Integer recipientId = isCurrentUserSender
          ? otherUser.getUserId()
          : currentUser.getUserId();

        UserDetailsDTO userDetails = new UserDetailsDTO(
          isCurrentUserSender ? currentUser : otherUser,
          isCurrentUserSender ? null : otherUserProfile
        );

        ProductDTO productDTO = null;
        if (message.getProducts() != null) {
          productDTO = convertProductToDTO(message.getProducts());
        }

        return new ChatMessage(
          type,
          message.getMessageText(),
          senderUsername,
          message.getDateSent(),
          recipientId,
          productId,
          userDetails,
          productDTO
        );
      })
      .collect(Collectors.toList());

    return ResponseEntity.ok(chatMessages);
  }

  private ArtisanProfile fetchArtisanProfile(Integer userid) {
    return artisanProfileRepository.findByArtisan_UserId(userid).orElse(null);
  }

  @GetMapping("/conversations")
  public ResponseEntity<?> getAllConversations(Principal principal) {
    if (principal == null) {
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("You must be logged in to view messages.");
    }

    ApplicationUser currentUser = userRepository
      .findByUsername(principal.getName())
      .orElseThrow(() -> new RuntimeException("User not found"));

    List<MessageDTO> conversations = messageService.getAllConversations(
      currentUser.getUserId()
    );

    return ResponseEntity.ok(conversations);
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
