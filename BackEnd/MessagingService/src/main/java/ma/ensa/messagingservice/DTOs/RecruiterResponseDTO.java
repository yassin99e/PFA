package ma.ensa.messagingservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String company;
    private String department;
}