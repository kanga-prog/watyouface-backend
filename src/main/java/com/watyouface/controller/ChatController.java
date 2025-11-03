package com.watyouface.controller;

import com.watyouface.entity.Message;
import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/conversations/{convId}")
    public Message sendMessage(@Payload Message message, Principal principal) {

        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        message.setSender(sender);
        message.setSentAt(Instant.now());
        return message;
    }
}
