// src/main/java/com/watyouface/dto/MessageDTO.java
package com.watyouface.dto;

import com.watyouface.entity.Message;
import java.time.Instant;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private Instant sentAt;

    public MessageDTO() {}

    public MessageDTO(Message m) {
        this.id = m.getId();
        this.conversationId = m.getConversation().getId();
        this.senderId = m.getSender().getId();
        this.senderUsername = m.getSender().getUsername();
        this.content = m.getContent();
        this.sentAt = m.getSentAt();
    }

    // getters / setters
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
