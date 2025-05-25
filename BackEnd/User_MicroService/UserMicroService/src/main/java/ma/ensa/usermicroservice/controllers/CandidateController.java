package ma.ensa.usermicroservice.controllers;


import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.CandidateRequestDTO;
import ma.ensa.usermicroservice.dtos.CandidateResponseDTO;
import ma.ensa.usermicroservice.services.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidate")
public class CandidateController {

    private final CandidateService candidateService;


    @GetMapping("/all")
    public ResponseEntity<List<CandidateResponseDTO>> getAllcandidate() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponseDTO> getcandidateById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<String> createCandidate(
            @RequestBody CandidateRequestDTO request
    ) {
        try {
            candidateService.save(request);  // Fixed: Pass the object, not class name
            return ResponseEntity.status(HttpStatus.CREATED).body("Candidate created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create Candidate: " + e.getMessage());
        }
    }


    // Need to adjustments : change based on what the user send
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCandidate(
            @PathVariable("id") Long id,
            @RequestBody CandidateRequestDTO request
    ) {
        try {
            candidateService.update(id,request);  // Fixed: Pass the object, not class name
            return ResponseEntity.ok("Candidate updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create Candidate: " + e.getMessage());
        }
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkCandidateExists(@PathVariable Long id) {
        try {
            candidateService.getCandidateById(id); // This will throw exception if not found
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }
}