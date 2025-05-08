package ma.ensa.usermicroservice.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "email is required")
    private String email;


    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "fullName is required")
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Role role;
}
