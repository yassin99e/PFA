package ma.ensa.resumeservice.controllers;
import lombok.RequiredArgsConstructor;
import ma.ensa.resumeservice.dtos.ResumeDTO;
import ma.ensa.resumeservice.dtos.ResumeUploadDTO;
import ma.ensa.resumeservice.services.resume.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDTO> uploadResume(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @RequestParam("file") MultipartFile file) throws IOException {

        ResumeUploadDTO uploadDTO = new ResumeUploadDTO();
        uploadDTO.setCandidateId(candidateId);
        uploadDTO.setApplicationId(applicationId);

        ResumeDTO resumeDTO = resumeService.uploadResume(uploadDTO, file);
        return new ResponseEntity<>(resumeDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResumeDTO> getResumeById(@PathVariable Long id) {
        ResumeDTO resumeDTO = resumeService.getResumeById(id);
        return ResponseEntity.ok(resumeDTO);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ResumeDTO>> getResumesByCandidateId(@PathVariable Long candidateId) {
        List<ResumeDTO> resumes = resumeService.getResumesByCandidateId(candidateId);
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ResumeDTO>> getResumesByApplicationId(@PathVariable Long applicationId) {
        List<ResumeDTO> resumes = resumeService.getResumesByApplicationId(applicationId);
        return ResponseEntity.ok(resumes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDTO> updateResume(
            @PathVariable Long id,
            @RequestParam("candidateId") Long candidateId,
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @RequestParam("file") MultipartFile file) throws IOException {

        ResumeUploadDTO uploadDTO = new ResumeUploadDTO();
        uploadDTO.setCandidateId(candidateId);
        uploadDTO.setApplicationId(applicationId);

        ResumeDTO updatedResume = resumeService.updateResume(id, uploadDTO, file);
        return ResponseEntity.ok(updatedResume);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkResumeExists(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("applicationId") Long applicationId) {
        boolean exists = resumeService.checkResumeExists(candidateId, applicationId);
        return ResponseEntity.ok(exists);
    }
}
