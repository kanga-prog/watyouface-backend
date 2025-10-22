package com.watyouface.service;

import com.watyouface.entity.Message;
import com.watyouface.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() { return messageRepository.findAll(); }

    public Message createMessage(Message message) { return messageRepository.save(message); }

    public void deleteMessage(Long id) { messageRepository.deleteById(id); }
}
