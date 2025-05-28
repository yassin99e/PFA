package ma.ensa.usermicroservice.dtos;



import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class RecruiterRequestDTO extends UserRequestDTO {

    @NotBlank(message = "company is required")
    private String company;

    @NotBlank(message = "department is required")
    private String department;
}