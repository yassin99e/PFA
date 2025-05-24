package ma.ensa.resumeservice.services;
import ma.ensa.resumeservice.dtos.CandidateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USERMICROSERVICE", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/candidates/{id}")
    ResponseEntity<CandidateDTO> getCandidateById(@PathVariable("id") Long id);

    @GetMapping("/api/candidates/exists/{id}")
    ResponseEntity<Boolean> checkCandidateExists(@PathVariable("id") Long id);
}
