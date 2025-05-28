package ma.ensa.usermicroservice.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate extends User {

    private String profile;
    private String diploma;
    private String phone;
    private int yearsOfExperience;

    @ElementCollection
    private List<String> technologies;

    @ElementCollection
    private List<String> interestedProfiles;
}
