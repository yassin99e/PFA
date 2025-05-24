package ma.ensa.resumeservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadDTO {
    private Long candidateId;
    private Long applicationId;
}
