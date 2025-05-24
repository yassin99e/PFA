package ma.ensa.resumeservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Builder
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
    private List<String> extractedSkills = new ArrayList<>();
    private String parsedData;
    private LocalDateTime uploadedAt;
}
