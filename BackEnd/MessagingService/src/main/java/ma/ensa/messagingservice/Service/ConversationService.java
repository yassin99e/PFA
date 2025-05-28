package ma.ensa.messagingservice.Service;


import ma.ensa.messagingservice.DTOs.ConversationDTO;
import ma.ensa.messagingservice.DTOs.ConversationStatus;
import ma.ensa.messagingservice.DTOs.UserDTO;
import ma.ensa.messagingservice.Entities.Conversation;
import ma.ensa.messagingservice.Exception.ResourceNotFoundException;
import ma.ensa.messagingservice.Feign.UserInterface;
import ma.ensa.messagingservice.Repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepo;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserInterface userInterface;

    public ConversationDTO findById(Long id) {
        Conversation convo = conversationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + id));

        return mapToDTO(convo);
    }

    public List<ConversationDTO> getConversationsForUser(Long userId, String role) {
        List<Conversation> conversations;

        if ("RECRUITER".equals(role)) {
            conversations = conversationRepo.findByParticipantTwoId(userId);
        } else {
            conversations = conversationRepo.findByParticipantOneId(userId);
        }

        return conversations.stream()
                .sorted(Comparator.comparing(Conversation::getLastActivityTime).reversed())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ConversationDTO startConversation(Long recruiterId, Long candidateId) {
        // Verify that the initiator is a recruiter by calling UserMicroService
        UserDTO initiator = userInterface.getUserById(recruiterId);

        if (!"RECRUITER".equals(initiator.getRole().name())) {
            throw new IllegalArgumentException("Only recruiters can initiate conversations");
        }

        // Check if conversation already exists
        Optional<Conversation> existingConvo = conversationRepo.findByCandidateAndRecruiter(candidateId, recruiterId);
        if (existingConvo.isPresent()) {
            return mapToDTO(existingConvo.get());
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setParticipantOneId(candidateId); // Candidate is always participant one
        conversation.setParticipantTwoId(recruiterId); // Recruiter is always participant two
        conversation.setInitiatorId(recruiterId); // Changed to recruiter as initiator
        conversation.setStartedAt(LocalDateTime.now());
        conversation.setStatus(ConversationStatus.ACTIVE);

        Conversation savedConversation = conversationRepo.save(conversation);
        return mapToDTO(savedConversation);
    }

    public void archiveConversation(Long id) {
        Conversation conversation = conversationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + id));

        conversation.setStatus(ConversationStatus.ARCHIVED);
        conversationRepo.save(conversation);
    }

    private ConversationDTO mapToDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setParticipantOneId(conversation.getParticipantOneId());
        dto.setParticipantTwoId(conversation.getParticipantTwoId());
        dto.setStartedAt(conversation.getStartedAt());
        dto.setMessages(messageService.getMessagesForConversation(conversation.getId()));
        return dto;
    }
}