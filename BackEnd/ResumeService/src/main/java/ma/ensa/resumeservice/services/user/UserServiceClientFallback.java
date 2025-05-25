package ma.ensa.resumeservice.services.user;
import ma.ensa.resumeservice.dtos.CandidateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public ResponseEntity<CandidateDTO> getCandidateById(Long id) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<Boolean> checkCandidateExists(Long id) {
        return ResponseEntity.ok(false);
    }
}
