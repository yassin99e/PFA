package ma.ensa.ai_matching.RequestDTOS;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequestDTO {
    private jobOffer jobOffer;
    private ResumeInfo resumeInfo;
}
