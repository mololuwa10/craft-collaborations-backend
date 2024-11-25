package com.example.dissertation_backend.solution.WebSocket.Config;

import com.example.dissertation_backend.solution.Authentication.Services.TokenService;
import com.example.dissertation_backend.solution.Customers.Service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserChannelInterceptor implements ChannelInterceptor {

  @Autowired
  private UserService userService;

  @Autowired
  private TokenService tokenService;

  @Override
  public Message<?> preSend(
    @SuppressWarnings("null") Message<?> message,
    @SuppressWarnings("null") MessageChannel channel
  ) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
      message,
      StompHeaderAccessor.class
    );

    if (accessor != null) {
      // Extract authentication and set it as a user property in the session
      Principal userPrincipal = extractUserPrincipal(accessor);
      if (userPrincipal != null) {
        accessor.setUser(userPrincipal);
      }
    }
    return message;
  }

  // Implement this method based on your authentication mechanism
  private Principal extractUserPrincipal(StompHeaderAccessor accessor) {
    // Extract a JWT token or session ID from the STOMP headers
    String authToken = accessor.getFirstNativeHeader("Authorization");

    if (authToken != null && authToken.startsWith("Bearer ")) {
      authToken = authToken.substring(7);
      String username = tokenService.getUsernameFromToken(authToken);

      UserDetails userDetails = userService.loadUserByUsername(username);

      if (userDetails != null) {
        return new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );
      }
    }

    return null;
  }
}
