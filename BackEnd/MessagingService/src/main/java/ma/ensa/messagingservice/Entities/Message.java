package ma.ensa.messagingservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.ensa.messagingservice.DTOs.Role;

import jakarta.persistence.*;


import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private Long senderId;
    private Long receiverId;
    private String content;

    @Enumerated(EnumType.STRING)
    private Role senderRole;

    private LocalDateTime timestamp;
    private boolean isRead;

    // Getters and setters
}