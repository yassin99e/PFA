package ma.ensa.notificationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryDTO {
    private Long totalNotifications;
    private Long unreadNotifications;
    private Long readNotifications;
}
