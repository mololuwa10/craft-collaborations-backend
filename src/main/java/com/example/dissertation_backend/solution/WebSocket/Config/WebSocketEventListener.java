package com.example.dissertation_backend.solution.WebSocket.Config;

import com.example.dissertation_backend.solution.WebSocket.Chat.ChatMessage;
import java.security.Principal;
// import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

  private final SimpMessageSendingOperations messagingTemplate;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(
      event.getMessage()
    );

    // Directly access the user Principal from the headerAccessor
    Principal userPrincipal = headerAccessor.getUser();
    String username = null;

    if (userPrincipal != null) {
      username = userPrincipal.getName();
      log.info("User connected: " + username);
      ChatMessage chatMessage = ChatMessage
        .builder()
        .type(ChatMessage.MessageType.JOIN)
        .sender(username)
        .build();

      messagingTemplate.convertAndSend("/topic/public", chatMessage);
    } else {
      log.warn("Username not found in session attributes");
    }
  }

  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    log.info("Web socket connection disconnected");
  }
}
