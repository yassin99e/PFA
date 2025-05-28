package ma.ensa.usermicroservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class RecruiterResponseDTO extends UserResponseDTO {
    private String company;
    private String department;
}
