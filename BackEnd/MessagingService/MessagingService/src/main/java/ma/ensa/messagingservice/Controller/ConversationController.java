package ma.ensa.messagingservice.Controller;

import ma.ensa.messagingservice.DTOs.ConversationDTO;
import ma.ensa.messagingservice.Service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDTO> getConversationById(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getConversationsForUser(
            @PathVariable Long userId,
            @RequestParam String role) {
        return ResponseEntity.ok(conversationService.getConversationsForUser(userId, role));
    }

    @PostMapping("/start")
    public ResponseEntity<ConversationDTO> startConversation(
            @RequestParam Long recruiterId,
            @RequestParam Long candidateId) {
        return ResponseEntity.ok(conversationService.startConversation(recruiterId, candidateId));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveConversation(@PathVariable Long id) {
        conversationService.archiveConversation(id);
        return ResponseEntity.noContent().build();
    }
}