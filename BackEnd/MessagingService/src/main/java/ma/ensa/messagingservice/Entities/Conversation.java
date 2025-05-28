package ma.ensa.messagingservice.Entities;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.ensa.messagingservice.DTOs.ConversationStatus;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long participantOneId;
    private Long participantTwoId;
    private LocalDateTime startedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private Long initiatorId;

    // Adding conversation status
    @Enumerated(EnumType.STRING)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    // Get last message timestamp for sorting
    @Transient
    public LocalDateTime getLastActivityTime() {
        if (messages == null || messages.isEmpty()) {
            return startedAt;
        }
        return messages.stream()
                .map(Message::getTimestamp)
                .max(LocalDateTime::compareTo)
                .orElse(startedAt);
    }

}