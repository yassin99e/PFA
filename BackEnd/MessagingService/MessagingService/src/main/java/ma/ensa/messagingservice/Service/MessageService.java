package ma.ensa.messagingservice.Service;


import ma.ensa.messagingservice.DTOs.MessageDTO;
import ma.ensa.messagingservice.Entities.Message;
import ma.ensa.messagingservice.Repositories.ConversationRepository;
import ma.ensa.messagingservice.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepo;
    @Autowired private ConversationRepository conversationRepo;

    public MessageDTO findById(Long id) {
        return messageRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public List<MessageDTO> getMessagesForConversation(Long conversationId) {
        return messageRepo.findByConversationIdOrderByTimestamp(conversationId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MessageDTO updateMessageContent(Long id, String newContent) {
        Message msg = messageRepo.findById(id).orElseThrow();
        msg.setContent(newContent);
        return toDTO(messageRepo.save(msg));
    }

    public void deleteMessage(Long id) {
        messageRepo.deleteById(id);
    }

    private MessageDTO toDTO(Message msg) {
        MessageDTO dto = new MessageDTO();
        dto.setId(msg.getId());
        dto.setConversationId(msg.getConversation().getId());
        dto.setSenderId(msg.getSenderId());
        dto.setReceiverId(msg.getReceiverId());
        dto.setContent(msg.getContent());
        dto.setSenderRole(msg.getSenderRole().name());
        dto.setTimestamp(msg.getTimestamp());
        dto.setRead(msg.isRead());
        return dto;
    }
}