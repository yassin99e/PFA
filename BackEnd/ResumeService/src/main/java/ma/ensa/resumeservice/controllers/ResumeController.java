package ma.ensa.resumeservice.controllers;
import lombok.RequiredArgsConstructor;
import ma.ensa.resumeservice.dtos.ResumeDTO;
import ma.ensa.resumeservice.dtos.ResumeUploadDTO;
import ma.ensa.resumeservice.services.ResumeService;
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
}
