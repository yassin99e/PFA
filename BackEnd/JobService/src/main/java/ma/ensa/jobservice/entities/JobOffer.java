package ma.ensa.jobservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String searchProfile;

    @ElementCollection
    @CollectionTable(name = "job_required_technologies", joinColumns = @JoinColumn(name = "job_offer_id"))
    @Column(name = "technology")
    private List<String> requiredTechnologies;

    private Integer minYearsExperience;

    @Column(nullable = false)
    private Long recruiterId;

    @Column(nullable = false)
    private LocalDateTime postedAt;

    @PrePersist
    protected void onCreate() {
        postedAt = LocalDateTime.now();
    }
}