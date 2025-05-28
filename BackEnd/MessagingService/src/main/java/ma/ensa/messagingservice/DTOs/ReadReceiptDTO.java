package ma.ensa.messagingservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadReceiptDTO {
    private Long messageId;
    private Long conversationId;
    private Long userId;
}