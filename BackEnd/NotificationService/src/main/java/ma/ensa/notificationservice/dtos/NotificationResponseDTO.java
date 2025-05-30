package ma.ensa.notificationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.ensa.notificationservice.entities.NotificationType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String content;
    private String data;
    private Boolean isRead;
    private LocalDateTime timestamp;
    private LocalDateTime readAt;
}
