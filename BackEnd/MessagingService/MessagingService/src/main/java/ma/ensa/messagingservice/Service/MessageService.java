package ma.ensa.messagingservice.Service;


import ma.ensa.messagingservice.DTOs.MessageCreateDTO;
import ma.ensa.messagingservice.DTOs.MessageDTO;
import ma.ensa.messagingservice.Entities.Conversation;
import ma.ensa.messagingservice.Entities.Message;
import ma.ensa.messagingservice.Exception.ResourceNotFoundException;
import ma.ensa.messagingservice.Repositories.ConversationRepository;
import ma.ensa.messagingservice.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private ConversationRepository conversationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public MessageDTO findById(Long id) {
        return messageRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    public List<MessageDTO> getMessagesForConversation(Long conversationId) {
        return messageRepo.findByConversationIdOrderByTimestamp(conversationId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MessageDTO sendMessage(MessageCreateDTO messageDto) {
        Conversation conversation = conversationRepo.findById(messageDto.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(messageDto.getSenderId());

        // Set the receiver based on sender
        if (messageDto.getSenderId().equals(conversation.getParticipantOneId())) {
            message.setReceiverId(conversation.getParticipantTwoId());
        } else {
            message.setReceiverId(conversation.getParticipantOneId());
        }

        message.setContent(messageDto.getContent());
        message.setSenderRole(messageDto.getSenderRole());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        Message savedMessage = messageRepo.save(message);
        MessageDTO savedDto = toDTO(savedMessage);

        // Notify the receiver using WebSocket
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                savedDto
        );

        return savedDto;
    }

    public MessageDTO updateMessageContent(Long id, String newContent) {
        Message msg = messageRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Message not found"));
        msg.setContent(newContent);
        return toDTO(messageRepo.save(msg));
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        message.setRead(true);
        messageRepo.save(message);
    }

    public void markConversationAsRead(Long conversationId, Long userId) {
        List<Message> unreadMessages = messageRepo.findUnreadMessagesForUser(conversationId, userId);
        unreadMessages.forEach(message -> {
            message.setRead(true);
            messageRepo.save(message);
        });
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
    public Integer countUnreadMessagesForUser(Long userId) {
        return messageRepo.countUnreadMessagesForUser(userId);
    }
}