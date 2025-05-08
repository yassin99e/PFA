package ma.ensa.usermicroservice.controllers;


import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.LoginRequestDTO;
import ma.ensa.usermicroservice.dtos.UserRequestDTO;
import ma.ensa.usermicroservice.dtos.UserResponseDTO;
import ma.ensa.usermicroservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;




    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            UserResponseDTO user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }


}
