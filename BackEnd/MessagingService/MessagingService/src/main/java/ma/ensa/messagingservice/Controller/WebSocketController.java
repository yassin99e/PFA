package ma.ensa.messagingservice.Controller;


import ma.ensa.messagingservice.DTOs.MessageCreateDTO;
import ma.ensa.messagingservice.DTOs.ReadReceiptDTO;
import ma.ensa.messagingservice.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageCreateDTO messageDto) {
        messageService.sendMessage(messageDto);
    }

    @MessageMapping("/chat.markRead")
    public void markRead(@Payload ReadReceiptDTO readReceiptDto) {
        if (readReceiptDto.getMessageId() != null) {
            messageService.markAsRead(readReceiptDto.getMessageId());
        } else if (readReceiptDto.getConversationId() != null) {
            messageService.markConversationAsRead(
                    readReceiptDto.getConversationId(),
                    readReceiptDto.getUserId()
            );
        }
    }
}