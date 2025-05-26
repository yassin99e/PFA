package ma.ensa.jobservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferRequestDTO {
    // NO ID FIELD - this is for creation only
    private String title;
    private String description;
    private String searchProfile;
    private List<String> requiredTechnologies;
    private Integer minYearsExperience;
    private Long recruiterId;
}