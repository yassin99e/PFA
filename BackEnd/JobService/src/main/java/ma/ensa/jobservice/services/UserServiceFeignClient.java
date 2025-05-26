package ma.ensa.jobservice.services;
import ma.ensa.jobservice.dtos.RecruiterResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USERMICROSERVICE")
public interface UserServiceFeignClient {
    @GetMapping("/api/recruiter/{id}")
    ResponseEntity<RecruiterResponseDTO> getRecruiterById(@PathVariable("id") Long id);

    @GetMapping("/api/recruiter/exists/{id}")
    ResponseEntity<Boolean> checkRecruiterExists(@PathVariable("id") Long id);
}
