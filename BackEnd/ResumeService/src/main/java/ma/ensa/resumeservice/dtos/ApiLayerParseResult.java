package ma.ensa.resumeservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLayerParseResult {
    private boolean success;
    private String extractedText;
    private String structuredData;
    private List<String> skills = new ArrayList<>();
    private String experience;
    private String education;
    private String personalInfo;
    private String candidateName;
    private String email;
    private String phone;
    private Integer totalYearsExperience;
}
