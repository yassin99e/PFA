package ma.ensa.resumeservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private Long id;
    private String email;
    private String fullName;
    private String profile;
    private String diploma;
    private String phone;
    private int yearsOfExperience;
    private List<String> technologies;
    private List<String> interestedProfiles;
}
