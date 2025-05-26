package ma.ensa.jobservice.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterResponseDTO {
    private Long id;
    private String company;
    private String department;
}