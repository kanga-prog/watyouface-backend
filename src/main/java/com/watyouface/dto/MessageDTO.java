package com.watyouface.dto;

import com.watyouface.entity.Message;
import java.time.Instant;

public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private Instant sentAt;

    public MessageDTO(Message m) {
        this.id = m.getId();
        this.content = m.getContent();
        this.senderId = m.getSender().getId();
        this.sentAt = m.getSentAt();
    }

    // getters/setters
    public Long getId() { return id; }
    public String getContent() { return content; }
    public Long getSenderId() { return senderId; }
    public Instant getSentAt() { return sentAt; }
}
