package com.watyouface.dto;

import com.watyouface.entity.Message;
import java.time.Instant;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderUsername;
    private String senderAvatarUrl; // ✅ nouvel attribut
    private String content;
    private Instant sentAt;

    private Long receiverId;

    public MessageDTO() {}

    public MessageDTO(Message m) {
        this.id = m.getId();
        this.conversationId = m.getConversation().getId();
        this.senderId = m.getSender().getId();
        this.senderUsername = m.getSender().getUsername();
        this.senderAvatarUrl = m.getSender().getAvatarUrl(); // ✅ ajout ici
        this.content = m.getContent();
        this.sentAt = m.getSentAt();

        if (m.getReceiver() != null) {
            this.receiverId = m.getReceiver().getId();
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long senderId) { this.senderId = receiverId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getSenderAvatarUrl() { return senderAvatarUrl; }
    public void setSenderAvatarUrl(String senderAvatarUrl) { this.senderAvatarUrl = senderAvatarUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
