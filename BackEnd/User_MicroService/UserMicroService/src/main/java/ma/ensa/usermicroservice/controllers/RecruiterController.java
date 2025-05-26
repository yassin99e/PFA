package ma.ensa.usermicroservice.controllers;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.CandidateRequestDTO;
import ma.ensa.usermicroservice.dtos.RecruiterRequestDTO;
import ma.ensa.usermicroservice.dtos.RecruiterResponseDTO;
import ma.ensa.usermicroservice.services.RecruiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruiter")
public class RecruiterController {

    private final RecruiterService recruiterService;


    @GetMapping("/all")
    public ResponseEntity<List<RecruiterResponseDTO>> getAllRecruiters() {
        return ResponseEntity.status(HttpStatus.OK).body(recruiterService.getAllRecruiters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecruiterResponseDTO> getRecruiterById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(recruiterService.getRecruiterById(id));
    }


    // // Need to adjustments : change based on what the user send

    @PostMapping("/register")
    public ResponseEntity<?> createRecruiter(@RequestBody RecruiterRequestDTO recruiterRequestDTO) {
        try {
            RecruiterResponseDTO createdRecruiter = recruiterService.save(recruiterRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRecruiter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create recruiter: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateRecruiter(
            @PathVariable("id") Long id,
            @RequestBody RecruiterRequestDTO recruiterRequestDTO
    ) {
        try {
            recruiterService.update(id,recruiterRequestDTO);  // Fixed: Pass the object, not class name

            return ResponseEntity.status(HttpStatus.OK).body("Recruiter updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create recruiter: " + e.getMessage());
        }
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkRecruiterExists(@PathVariable("id") Long id) {
        try {
            recruiterService.getRecruiterById(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

}
