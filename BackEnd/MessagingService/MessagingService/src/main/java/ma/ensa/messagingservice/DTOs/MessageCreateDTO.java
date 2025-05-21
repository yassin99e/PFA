package ma.ensa.messagingservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreateDTO {
    private Long conversationId;
    private Long senderId;
    private String content;
    private Role senderRole;
}