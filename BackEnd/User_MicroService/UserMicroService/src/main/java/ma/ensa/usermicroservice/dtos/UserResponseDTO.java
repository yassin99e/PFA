package ma.ensa.usermicroservice.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.ensa.usermicroservice.entities.Role;




@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
}
