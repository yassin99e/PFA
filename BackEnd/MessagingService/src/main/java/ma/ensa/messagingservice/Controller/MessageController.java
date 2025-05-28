package ma.ensa.messagingservice.Controller;

import ma.ensa.messagingservice.DTOs.ConversationDTO;
import ma.ensa.messagingservice.DTOs.MessageCreateDTO;
import ma.ensa.messagingservice.DTOs.MessageDTO;
import ma.ensa.messagingservice.Service.ConversationService;
import ma.ensa.messagingservice.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesForConversation(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messageService.getMessagesForConversation(conversationId));
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageCreateDTO messageDto) {
        return ResponseEntity.ok(messageService.sendMessage(messageDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessageContent(@PathVariable Long id, @RequestBody String content) {
        return ResponseEntity.ok(messageService.updateMessageContent(id, content));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        messageService.markConversationAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversation/detail/{id}")
    public ResponseEntity<ConversationDTO> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.findById(id));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Integer> getUnreadMessagesCount(@RequestParam Long userId) {
        System.out.println("Received request for unread count for user: " + userId);

        try {
            Integer count = messageService.countUnreadMessagesForUser(userId);
            System.out.println("Returning unread count: " + count + " for user: " + userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.err.println("Error getting unread count for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(0); // Return 0 as fallback
        }
    }
}