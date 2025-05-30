package ma.ensa.notificationservice.services.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.notificationservice.dtos.NotificationRequestDTO;
import ma.ensa.notificationservice.entities.NotificationType;
import ma.ensa.notificationservice.services.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "application-events")
    public void handleApplicationEvent(@Payload Map<String, Object> eventData,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       Acknowledgment acknowledgment) {
        try {
            log.info("📨 Received application event: {}", eventData);

            String eventType = (String) eventData.get("eventType");

            if ("APPLICATION_SUBMITTED".equals(eventType)) {
                handleApplicationSubmitted(eventData);
                log.info("✅ A candidate applied for the job - Event processed successfully!");
            } else {
                log.warn("⚠️ Unknown application event type: {}", eventType);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("❌ Error processing application event: {}", e.getMessage(), e);
        }
    }

    private void handleApplicationSubmitted(Map<String, Object> eventData) {
        try {
            Long recruiterId = getLongValue(eventData, "recruiterId");
            String candidateName = (String) eventData.get("candidateName");
            String jobTitle = (String) eventData.get("jobTitle");
            Long applicationId = getLongValue(eventData, "applicationId");

            log.info("🎯 Processing application submission:");
            log.info("   - Candidate: {}", candidateName);
            log.info("   - Job: {}", jobTitle);
            log.info("   - Recruiter ID: {}", recruiterId);
            log.info("   - Application ID: {}", applicationId);

            // Create notification for recruiter
            NotificationRequestDTO recruiterNotification = new NotificationRequestDTO();
            recruiterNotification.setUserId(recruiterId);
            recruiterNotification.setType(NotificationType.APPLICATION_SUBMITTED);
            recruiterNotification.setTitle("New Application Received");
            recruiterNotification.setContent(candidateName + " has applied for " + jobTitle);
            recruiterNotification.setData("{}"); // Simple empty JSON for now

            notificationService.createNotification(recruiterNotification);

            log.info("📧 Notification created and sent to recruiter ID: {}", recruiterId);
            log.info("🎉 SUCCESS: A candidate applied for the job notification flow completed!");

        } catch (Exception e) {
            log.error("❌ Error handling application submission: {}", e.getMessage(), e);
        }
    }

    private Long getLongValue(Map<String, Object> eventData, String key) {
        Object value = eventData.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
