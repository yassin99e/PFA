package ma.ensa.messagingservice.Service;


import ma.ensa.messagingservice.DTOs.ConversationDTO;
import ma.ensa.messagingservice.Entities.Conversation;
import ma.ensa.messagingservice.Repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepo;
    @Autowired
    private MessageService messageService;

    public ConversationDTO findById(Long id) {
        Conversation convo = conversationRepo.findById(id).orElseThrow();
        ConversationDTO dto = new ConversationDTO();
        dto.setId(convo.getId());
        dto.setParticipantOneId(convo.getParticipantOneId());
        dto.setParticipantTwoId(convo.getParticipantTwoId());
        dto.setStartedAt(convo.getStartedAt());
        dto.setMessages(messageService.getMessagesForConversation(id));
        return dto;
    }
}