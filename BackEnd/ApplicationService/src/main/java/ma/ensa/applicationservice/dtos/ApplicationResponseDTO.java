package ma.ensa.applicationservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.ensa.applicationservice.entities.ApplicationStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponseDTO {
    private Long id;
    private Long candidateId;
    private Long jobOfferId;
    private Long resumeId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Double matchScore;
}
