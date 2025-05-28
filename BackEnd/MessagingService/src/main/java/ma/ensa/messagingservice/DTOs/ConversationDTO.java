package ma.ensa.messagingservice.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    private Long id;
    private Long participantOneId;
    private Long participantTwoId;
    private LocalDateTime startedAt;
    private List<MessageDTO> messages;
    // Getters and setters
}