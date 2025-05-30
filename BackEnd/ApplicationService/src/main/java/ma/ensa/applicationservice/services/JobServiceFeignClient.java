package ma.ensa.applicationservice.services;

import ma.ensa.applicationservice.dtos.JobOfferResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
@FeignClient(name = "job-service")
public interface JobServiceFeignClient {
    @GetMapping("/api/job-offers/{id}")
    ResponseEntity<JobOfferResponseDTO> getJobOfferById(@PathVariable("id") Long id);

    @GetMapping("/api/job-offers/recruiter/{recruiterId}")
    ResponseEntity<List<JobOfferResponseDTO>> getJobOffersByRecruiterId(@PathVariable("recruiterId") Long recruiterId);
}
