package com.example.dissertation_backend.solution.WebSocket.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final UserChannelInterceptor userChannelInterceptor;

  public WebSocketConfig(UserChannelInterceptor userChannelInterceptor) {
    this.userChannelInterceptor = userChannelInterceptor;
  }

  @Override
  public void configureClientInboundChannel(
    @SuppressWarnings("null") ChannelRegistration registration
  ) {
    registration.interceptors(userChannelInterceptor);
  }

  @Override
  public void registerStompEndpoints(
    @SuppressWarnings("null") StompEndpointRegistry registry
  ) {
    registry
      .addEndpoint("/ws")
      .setAllowedOrigins("http://localhost:3000")
      .withSockJS();
  }

  @Override
  public void configureMessageBroker(
    @SuppressWarnings("null") MessageBrokerRegistry registry
  ) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic");
  }
}
