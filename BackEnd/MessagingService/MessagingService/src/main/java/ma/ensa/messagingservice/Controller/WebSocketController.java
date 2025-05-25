// BackEnd/MessagingService/MessagingService/src/main/java/ma/ensa/messagingservice/Controller/WebSocketController.java
package ma.ensa.messagingservice.Controller;

import ma.ensa.messagingservice.DTOs.MessageDTO;
import ma.ensa.messagingservice.Entities.Conversation;
import ma.ensa.messagingservice.Entities.Message;
import ma.ensa.messagingservice.DTOs.Role;
import ma.ensa.messagingservice.Repositories.ConversationRepository;
import ma.ensa.messagingservice.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        try {
            System.out.println("Received WebSocket message: " + messageDTO.getContent());

            // Convert DTO to Entity
            Message message = new Message();
            message.setSenderId(messageDTO.getSenderId());
            message.setContent(messageDTO.getContent());
            message.setSenderRole(Role.valueOf(messageDTO.getSenderRole()));
            message.setTimestamp(LocalDateTime.now());
            message.setRead(false);

            // Find and set the conversation
            Conversation conversation = conversationRepository.findById(messageDTO.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            message.setConversation(conversation);

            // Determine receiver ID
            Long receiverId = messageDTO.getSenderId().equals(conversation.getParticipantOneId())
                    ? conversation.getParticipantTwoId()
                    : conversation.getParticipantOneId();
            message.setReceiverId(receiverId);

            // Save message to database
            Message savedMessage = messageRepository.save(message);
            System.out.println("Message saved to database with ID: " + savedMessage.getId());

            // Convert back to DTO for broadcasting
            MessageDTO responseDTO = convertToDTO(savedMessage);

            // CRITICAL: Broadcast to BOTH participants
            System.out.println("Broadcasting to participant 1: " + conversation.getParticipantOneId());
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(conversation.getParticipantOneId()),
                    "/queue/messages",
                    responseDTO
            );

            System.out.println("Broadcasting to participant 2: " + conversation.getParticipantTwoId());
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(conversation.getParticipantTwoId()),
                    "/queue/messages",
                    responseDTO
            );

            System.out.println("Message broadcast completed: " + responseDTO.getContent());

        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload Map<String, Object> payload) {
        try {
            if (payload.containsKey("messageId")) {
                Long messageId = Long.valueOf(payload.get("messageId").toString());
                Long userId = Long.valueOf(payload.get("userId").toString());

                Message message = messageRepository.findById(messageId).orElse(null);
                if (message != null && message.getReceiverId().equals(userId)) {
                    message.setRead(true);
                    messageRepository.save(message);

                    // Notify sender about read status
                    messagingTemplate.convertAndSendToUser(
                            String.valueOf(message.getSenderId()),
                            "/queue/read-receipt",
                            Map.of("messageId", messageId, "isRead", true)
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, String> payload) {
        String userId = payload.get("userId");
        System.out.println("User connected to WebSocket: " + userId);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setSenderRole(message.getSenderRole().name());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());
        return dto;
    }
}