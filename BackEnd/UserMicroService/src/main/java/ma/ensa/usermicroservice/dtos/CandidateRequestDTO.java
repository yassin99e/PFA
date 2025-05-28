package ma.ensa.usermicroservice.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class CandidateRequestDTO extends UserRequestDTO {
    @NotBlank(message = "profile is required")
    private String profile;

    @NotBlank(message = "diploma is required")
    private String diploma;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotBlank(message = "yearsOfExperience is required")
    private int yearsOfExperience;

    private List<String> technologies;

    private List<String> interestedProfiles;
}
