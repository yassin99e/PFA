package ma.ensa.messagingservice.Controller;


import ma.ensa.messagingservice.DTOs.ConversationDTO;
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
    @Autowired private ConversationService conversationService;

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessagesForConversation(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messageService.getMessagesForConversation(conversationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessageContent(@PathVariable Long id, @RequestBody String content) {
        return ResponseEntity.ok(messageService.updateMessageContent(id, content));
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
}