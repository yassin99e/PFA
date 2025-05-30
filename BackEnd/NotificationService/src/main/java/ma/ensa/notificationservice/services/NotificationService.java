package ma.ensa.notificationservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notificationservice.dtos.NotificationRequestDTO;
import ma.ensa.notificationservice.dtos.NotificationResponseDTO;
import ma.ensa.notificationservice.dtos.NotificationSummaryDTO;
import ma.ensa.notificationservice.entities.Notification;
import ma.ensa.notificationservice.entities.NotificationType;
import ma.ensa.notificationservice.repositories.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        log.info("Creating notification for user: {}, type: {}", requestDTO.getUserId(), requestDTO.getType());

        Notification notification = modelMapper.map(requestDTO, Notification.class);
        notification = notificationRepository.save(notification);

        NotificationResponseDTO responseDTO = modelMapper.map(notification, NotificationResponseDTO.class);

        log.info("Notification created with ID: {} for user: {}", notification.getId(), requestDTO.getUserId());
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId, int page, int size) {
        log.info("Fetching notifications for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByTimestampDesc(userId, pageable);

        return notificationPage.getContent().stream()
                .map(notification -> modelMapper.map(notification, NotificationResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);

        return unreadNotifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationSummaryDTO getNotificationSummary(Long userId) {
        log.info("Fetching notification summary for user: {}", userId);

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long totalCount = notificationRepository.countByUserId(userId);
        long readCount = totalCount - unreadCount;

        return new NotificationSummaryDTO(totalCount, unreadCount, readCount);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        if (!notification.getIsRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        log.info("Fetching notification by id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        return modelMapper.map(notification, NotificationResponseDTO.class);
    }
}
