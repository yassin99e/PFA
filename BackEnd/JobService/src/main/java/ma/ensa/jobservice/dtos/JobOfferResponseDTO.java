package ma.ensa.jobservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String searchProfile;
    private List<String> requiredTechnologies;
    private Integer minYearsExperience;
    private Long recruiterId;
    private LocalDateTime postedAt;
}