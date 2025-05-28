package ma.ensa.messagingservice.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResponseDTO {
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
