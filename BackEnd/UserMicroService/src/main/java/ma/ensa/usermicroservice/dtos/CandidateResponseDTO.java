package ma.ensa.usermicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class CandidateResponseDTO extends UserResponseDTO {
    private String profile;
    private String diploma;
    private String phone;
    private int yearsOfExperience;
    private List<String> technologies;
    private List<String> interestedProfiles;
}
