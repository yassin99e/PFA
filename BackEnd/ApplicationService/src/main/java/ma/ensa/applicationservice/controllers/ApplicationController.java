package ma.ensa.applicationservice.controllers;
import lombok.RequiredArgsConstructor;
import ma.ensa.applicationservice.dtos.ApplicationRequestDTO;
import ma.ensa.applicationservice.dtos.ApplicationResponseDTO;
import ma.ensa.applicationservice.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<String> createApplication(@RequestBody ApplicationRequestDTO applicationRequest) {
        try {
            applicationService.save(applicationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Application created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create application: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> getApplicationById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByCandidateId(@PathVariable("candidateId") Long candidateId) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationsByCandidateId(candidateId));
    }

    @GetMapping("/job-offer/{jobOfferId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByJobOfferId(@PathVariable("jobOfferId") Long jobOfferId) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationsByJobOfferId(jobOfferId));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByRecruiterId(@PathVariable("recruiterId") Long recruiterId) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationsByRecruiterId(recruiterId));
    }

    @GetMapping("/candidate/{candidateId}/status/{status}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByCandidateIdAndStatus(
            @PathVariable("candidateId") Long candidateId,
            @PathVariable("status") String status) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationsByCandidateIdAndStatus(candidateId, status));
    }

    @GetMapping("/job-offer/{jobOfferId}/status/{status}")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsByJobOfferIdAndStatus(
            @PathVariable("jobOfferId") Long jobOfferId,
            @PathVariable("status") String status) {
        return ResponseEntity.status(HttpStatus.OK).body(applicationService.getApplicationsByJobOfferIdAndStatus(jobOfferId, status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        try {
            applicationService.updateStatus(id, status);
            return ResponseEntity.status(HttpStatus.OK).body("Application status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update application status: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateApplication(
            @PathVariable("id") Long id,
            @RequestBody ApplicationRequestDTO applicationRequest) {
        try {
            applicationService.update(id, applicationRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Application updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update application: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable("id") Long id) {
        try {
            applicationService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Application deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete application: " + e.getMessage());
        }
    }
}
