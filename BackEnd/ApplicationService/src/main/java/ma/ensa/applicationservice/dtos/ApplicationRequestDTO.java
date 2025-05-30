package ma.ensa.applicationservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDTO {
    private Long candidateId;
    private Long jobOfferId;
    private Long resumeId;
}
