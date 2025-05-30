package ma.ensa.applicationservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private Long id;
    private Long candidateId;
    private Long applicationId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String content;
    private List<String> extractedSkills;
    private String parsedData;
    private LocalDateTime uploadedAt;
}
