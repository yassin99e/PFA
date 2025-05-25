package ma.ensa.resumeservice.services.resume;

import ma.ensa.resumeservice.dtos.ResumeDTO;
import ma.ensa.resumeservice.dtos.ResumeUploadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
public interface ResumeService {
    ResumeDTO uploadResume(ResumeUploadDTO resumeUploadDTO, MultipartFile file) throws IOException;

    ResumeDTO getResumeById(Long id);

    List<ResumeDTO> getResumesByCandidateId(Long candidateId);

    List<ResumeDTO> getResumesByApplicationId(Long applicationId);

    void deleteResume(Long id);

    ResumeDTO updateResume(Long id, ResumeUploadDTO resumeUploadDTO, MultipartFile file) throws IOException;

    boolean checkResumeExists(Long candidateId, Long applicationId);
}
