package com.example.dissertation_backend.solution.Message.Service;

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
import com.example.dissertation_backend.solution.Message.Repository.MessageRepository;
import com.example.dissertation_backend.solution.Products.Model.ProductImages;
import com.example.dissertation_backend.solution.Products.Model.Products;
import com.example.dissertation_backend.solution.WebSocket.Chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ArtisanProfileRepository artisanProfileRepository;

  public Message saveMessage(
    ChatMessage chatMessage,
    ApplicationUser sender,
    ApplicationUser receiver,
    Products products
  ) {
    Message message = new Message();
    message.setSender(sender);
    message.setReceiver(receiver);
    message.setProducts(products);
    message.setMessageText(chatMessage.getContent());
    message.setDateSent(LocalDateTime.now());
    return messageRepository.save(message);
  }

  public List<Message> getMessageHistoryByProduct(
    ApplicationUser user1,
    ApplicationUser user2,
    Integer productId
  ) {
    // Retrieves the conversation between two users
    List<Message> messagesSent = messageRepository.findBySenderAndReceiver(
      user1,
      user2
    );
    List<Message> messagesReceived = messageRepository.findBySenderAndReceiver(
      user2,
      user1
    );

    Stream<Message> combinedStream = Stream.concat(
      messagesSent.stream(),
      messagesReceived.stream()
    );

    if (productId != null) {
      combinedStream =
        combinedStream.filter(m ->
          m.getProducts() != null &&
          m.getProducts().getProductId().equals(productId)
        );
    }

    // Sort the messages by date sent and return the list
    return combinedStream
      .sorted(Comparator.comparing(Message::getDateSent))
      .collect(Collectors.toList());
  }

  public List<ChatMessage> markMessagesAsRead(
    Integer senderId,
    Integer receiverId,
    ApplicationUser currentUser
  ) {
    List<Message> messages = messageRepository.findBySenderAndReceiver(
      userRepository
        .findById(receiverId)
        .orElseThrow(() -> new RuntimeException("User not found")),
      userRepository
        .findById(senderId)
        .orElseThrow(() -> new RuntimeException("User not found"))
    );
    messages.forEach(message -> {
      if (!message.isRead()) {
        message.setRead(true);
        messageRepository.save(message);
      }
    });

    List<Message> updatedMessages = messageRepository.findBySenderAndReceiver(
      userRepository
        .findById(senderId)
        .orElseThrow(() -> new RuntimeException("User not found")),
      userRepository
        .findById(receiverId)
        .orElseThrow(() -> new RuntimeException("User not found"))
    );

    // Convert messages to ChatMessage DTOs
    List<ChatMessage> chatMessages = updatedMessages
      .stream()
      .map(message -> {
        boolean isCurrentUserSender = message.getSender().equals(currentUser);
        ChatMessage.MessageType type = isCurrentUserSender
          ? ChatMessage.MessageType.SENT
          : ChatMessage.MessageType.RECEIVED;
        String senderUsername = isCurrentUserSender
          ? currentUser.getUsername()
          : message.getReceiver().getUsername();

        Integer recipientId = isCurrentUserSender
          ? message.getReceiver().getUserId()
          : currentUser.getUserId();

        UserDetailsDTO userDetails = new UserDetailsDTO(
          isCurrentUserSender ? currentUser : message.getReceiver(),
          isCurrentUserSender
            ? null
            : fetchArtisanProfile(message.getReceiver().getUserId())
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
          message.getProducts() != null
            ? message.getProducts().getProductId()
            : null,
          userDetails,
          productDTO
        );
      })
      .collect(Collectors.toList());

    return chatMessages;
  }

  public Long countUnreadMessages(ApplicationUser user) {
    return messageRepository.countByReceiverAndIsRead(user, false);
  }

  public List<Message> getMessageHistory(
    ApplicationUser user1,
    ApplicationUser user2
  ) {
    // Retrieves the conversation between two users sorted by date
    List<Message> messagesSent = messageRepository.findBySenderAndReceiver(
      user1,
      user2
    );
    List<Message> messagesReceived = messageRepository.findBySenderAndReceiver(
      user2,
      user1
    );

    List<Message> conversation = new ArrayList<>();
    conversation.addAll(messagesSent);
    conversation.addAll(messagesReceived);

    conversation.sort(Comparator.comparing(Message::getDateSent));
    return conversation;
  }

  public List<MessageDTO> getAllConversations(Integer currentUserId) {
    ApplicationUser currentUser = userRepository
      .findById(currentUserId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    List<Message> messages = messageRepository.findBySenderOrReceiver(
      currentUser,
      currentUser
    );

    // Process messages to group them by conversation with each user
    Map<ApplicationUser, List<Message>> conversations = new HashMap<>();
    messages.forEach(message -> {
      ApplicationUser otherParty = message.getSender().equals(currentUser)
        ? message.getReceiver()
        : message.getSender();
      conversations
        .computeIfAbsent(otherParty, k -> new ArrayList<>())
        .add(message);
    });

    // Convert to a list of Conversation DTOs or similar structure
    List<MessageDTO> conversationList = conversations
      .entrySet()
      .stream()
      .map(entry -> new MessageDTO(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());

    return conversationList;
  }

  private ArtisanProfile fetchArtisanProfile(Integer userid) {
    return artisanProfileRepository.findByArtisan_UserId(userid).orElse(null);
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
