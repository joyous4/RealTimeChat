package com.realTimeChat.ChatApp.Configuration;

import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.realTimeChat.ChatApp.Controller.ChatMessage;
import com.realTimeChat.ChatApp.Controller.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor //automatically cunstroctors are created
@Slf4j // used to display messges based on login
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    
    @EventListener
    public void handelWebScoketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("user disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}