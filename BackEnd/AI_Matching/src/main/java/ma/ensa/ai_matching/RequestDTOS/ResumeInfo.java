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
public class ResumeInfo {
    private String fullName;
    private String profileTitle;
    private String summary;
    private List<String> technologies;
    private int totalYearsExperience;
}
