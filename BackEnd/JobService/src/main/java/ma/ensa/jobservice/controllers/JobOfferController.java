package ma.ensa.jobservice.controllers;
import lombok.RequiredArgsConstructor;
import ma.ensa.jobservice.dtos.JobOfferRequestDTO;
import ma.ensa.jobservice.dtos.JobOfferResponseDTO;
import ma.ensa.jobservice.services.JobOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-offers")
public class JobOfferController {
    private final JobOfferService jobOfferService;

    @PostMapping
    public ResponseEntity<String> createJobOffer(@RequestBody JobOfferRequestDTO jobOfferRequest) {
        try {
            jobOfferService.save(jobOfferRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Job offer created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create job offer: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponseDTO> getJobOfferById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(jobOfferService.getJobOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobOfferResponseDTO>> getAllActiveJobOffers() {
        return ResponseEntity.status(HttpStatus.OK).body(jobOfferService.getAllActiveJobOffers());
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<JobOfferResponseDTO>> getJobOffersByRecruiterId(@PathVariable("recruiterId") Long recruiterId) {
        return ResponseEntity.status(HttpStatus.OK).body(jobOfferService.getJobOffersByRecruiterId(recruiterId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateJobOffer(
            @PathVariable("id") Long id,
            @RequestBody JobOfferRequestDTO jobOfferRequest) {
        try {
            jobOfferService.update(id, jobOfferRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Job offer updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update job offer: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobOffer(@PathVariable("id") Long id) {
        try {
            jobOfferService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Job offer deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete job offer: " + e.getMessage());
        }
    }

}
