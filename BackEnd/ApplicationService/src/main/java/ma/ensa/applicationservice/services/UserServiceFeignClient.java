package ma.ensa.applicationservice.services;
import ma.ensa.applicationservice.dtos.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Client for UserService
@FeignClient(name = "USERMICROSERVICE")
public interface UserServiceFeignClient {
    @GetMapping("/api/users/{id}")
    ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id);
}
