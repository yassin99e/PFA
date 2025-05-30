package ma.ensa.applicationservice.services;


import ma.ensa.applicationservice.dtos.ResumeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name = "resume-service")
public interface ResumeServiceFeignClient {
    @GetMapping("/api/resumes/candidate/{candidateId}")
    ResponseEntity<List<ResumeDTO>> getResumesByCandidateId(@PathVariable("candidateId") Long candidateId);

    @GetMapping("/api/resumes/{id}")
    ResponseEntity<ResumeDTO> getResumeById(@PathVariable("id") Long id);

    @GetMapping("/api/resumes/exists")
    ResponseEntity<Boolean> checkResumeExists(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("applicationId") Long applicationId
    );

}
