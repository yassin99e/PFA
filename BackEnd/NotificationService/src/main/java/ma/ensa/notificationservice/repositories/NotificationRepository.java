package ma.ensa.notificationservice.repositories;

import ma.ensa.notificationservice.entities.Notification;
import ma.ensa.notificationservice.entities.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    // Find unread notifications by user ID
    List<Notification> findByUserIdAndIsReadFalseOrderByTimestampDesc(Long userId);

    // Count unread notifications for a user
    long countByUserIdAndIsReadFalse(Long userId);

    // Count total notifications for a user
    long countByUserId(Long userId);

    // Find notifications by user ID and type
    List<Notification> findByUserIdAndTypeOrderByTimestampDesc(Long userId, NotificationType type);

    // Find notifications by user ID and read status
    List<Notification> findByUserIdAndIsReadOrderByTimestampDesc(Long userId, Boolean isRead);
}
