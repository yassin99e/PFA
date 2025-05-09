package ma.ensa.ai_matching.RequestDTOS;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class jobOffer {
    private String title;
    private String description;
    private String searchedProfile;
    private List<String> RequiredTechnologies;
    private int minYearsExperience;

}