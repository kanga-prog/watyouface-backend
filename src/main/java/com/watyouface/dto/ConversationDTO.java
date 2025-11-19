package com.watyouface.dto;

import com.watyouface.entity.Conversation;
import com.watyouface.entity.ConversationUser;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationDTO {
    private Long id;
    private String title;
    private boolean isGroup;
    private List<UserDTO> participants;

    public ConversationDTO(Conversation conversation) {
        this.id = conversation.getId();
        this.title = conversation.getTitle();
        this.isGroup = conversation.isGroup();

        // ✅ Corrigé ici : map(ConversationUser::getUser)
        this.participants = conversation.getParticipants()
            .stream()
            .map(ConversationUser::getUser)
            .map(UserDTO::new)
            .collect(Collectors.toList());
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }
}
