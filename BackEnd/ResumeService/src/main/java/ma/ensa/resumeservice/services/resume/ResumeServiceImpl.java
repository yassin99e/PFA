package ma.ensa.resumeservice.services.resume;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.resumeservice.dtos.ApiLayerParseResult;
import ma.ensa.resumeservice.dtos.ResumeDTO;
import ma.ensa.resumeservice.dtos.ResumeUploadDTO;
import ma.ensa.resumeservice.entities.Resume;
import ma.ensa.resumeservice.repositories.ResumeRepository;
import ma.ensa.resumeservice.services.apilayer.ApiLayerResumeParser;
import ma.ensa.resumeservice.services.user.UserServiceClient;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
    private final ApiLayerResumeParser apiLayerResumeParser;

    @Override
    public ResumeDTO uploadResume(ResumeUploadDTO resumeUploadDTO, MultipartFile file) throws IOException {
        log.info("Starting resume upload for candidate ID: {}", resumeUploadDTO.getCandidateId());

        // Validate candidate exists
        ResponseEntity<Boolean> candidateResponse = userServiceClient.checkCandidateExists(resumeUploadDTO.getCandidateId());
        if (!Boolean.TRUE.equals(candidateResponse.getBody())) {
            throw new EntityNotFoundException("Candidate not found with ID: " + resumeUploadDTO.getCandidateId());
        }

        // Create resume entity
        Resume resume = new Resume();
        resume.setCandidateId(resumeUploadDTO.getCandidateId());
        resume.setApplicationId(resumeUploadDTO.getApplicationId());

        // Set file metadata
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        resume.setFileSize(file.getSize());
        resume.setFileData(file.getBytes());
        resume.setFileHash(generateFileHash(file));

        // Parse resume using API Layer
        ApiLayerParseResult parseResult = apiLayerResumeParser.parseResume(file);

        if (parseResult.isSuccess()) {
            log.info("Using API Layer parsed data for resume: {}", file.getOriginalFilename());

            // Use API Layer extracted data
            resume.setContent(parseResult.getExtractedText());
            resume.setExtractedSkills(parseResult.getSkills());
            resume.setParsedData(parseResult.getStructuredData());

        } else {
            log.info("Using fallback parsing for resume: {}", file.getOriginalFilename());

            // Fallback to manual extraction
            String content = extractTextFromFile(file);
            resume.setContent(content);

            List<String> extractedSkills = extractSkills(content);
            resume.setExtractedSkills(extractedSkills);

            // Create fallback parsed data
            Map<String, Object> parsedData = createFallbackParsedData(file, extractedSkills);
            resume.setParsedData(objectMapper.writeValueAsString(parsedData));
        }

        // Set upload timestamp
        resume.setUploadedAt(LocalDateTime.now());

        // Save to database
        Resume savedResume = resumeRepository.save(resume);
        log.info("Resume saved successfully with ID: {}", savedResume.getId());

        return modelMapper.map(savedResume, ResumeDTO.class);
    }

    @Override
    public ResumeDTO getResumeById(Long id) {
        log.debug("Fetching resume with ID: {}", id);
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with ID: " + id));
        return modelMapper.map(resume, ResumeDTO.class);
    }

    @Override
    public List<ResumeDTO> getResumesByCandidateId(Long candidateId) {
        log.debug("Fetching resumes for candidate ID: {}", candidateId);

        // Verify candidate exists
        ResponseEntity<Boolean> candidateResponse = userServiceClient.checkCandidateExists(candidateId);
        if (!Boolean.TRUE.equals(candidateResponse.getBody())) {
            throw new EntityNotFoundException("Candidate not found with ID: " + candidateId);
        }

        List<Resume> resumes = resumeRepository.findByCandidateId(candidateId);
        return resumes.stream()
                .map(resume -> modelMapper.map(resume, ResumeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ResumeDTO> getResumesByApplicationId(Long applicationId) {
        log.debug("Fetching resumes for application ID: {}", applicationId);
        List<Resume> resumes = resumeRepository.findByApplicationId(applicationId);
        return resumes.stream()
                .map(resume -> modelMapper.map(resume, ResumeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteResume(Long id) {
        log.info("Deleting resume with ID: {}", id);
        if (!resumeRepository.existsById(id)) {
            throw new EntityNotFoundException("Resume not found with ID: " + id);
        }
        resumeRepository.deleteById(id);
    }

    @Override
    public ResumeDTO updateResume(Long id, ResumeUploadDTO resumeUploadDTO, MultipartFile file) throws IOException {
        log.info("Updating resume with ID: {}", id);

        Resume existingResume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with ID: " + id));

        // Verify candidate exists
        ResponseEntity<Boolean> candidateResponse = userServiceClient.checkCandidateExists(resumeUploadDTO.getCandidateId());
        if (!Boolean.TRUE.equals(candidateResponse.getBody())) {
            throw new EntityNotFoundException("Candidate not found with ID: " + resumeUploadDTO.getCandidateId());
        }

        // Update basic fields
        existingResume.setCandidateId(resumeUploadDTO.getCandidateId());
        existingResume.setApplicationId(resumeUploadDTO.getApplicationId());
        existingResume.setFileName(file.getOriginalFilename());
        existingResume.setFileType(file.getContentType());
        existingResume.setFileSize(file.getSize());
        existingResume.setFileData(file.getBytes());
        existingResume.setFileHash(generateFileHash(file));

        // Parse updated file
        ApiLayerParseResult parseResult = apiLayerResumeParser.parseResume(file);

        if (parseResult.isSuccess()) {
            existingResume.setContent(parseResult.getExtractedText());
            existingResume.setExtractedSkills(parseResult.getSkills());
            existingResume.setParsedData(parseResult.getStructuredData());
        } else {
            String content = extractTextFromFile(file);
            existingResume.setContent(content);
            List<String> extractedSkills = extractSkills(content);
            existingResume.setExtractedSkills(extractedSkills);
            Map<String, Object> parsedData = createFallbackParsedData(file, extractedSkills);
            existingResume.setParsedData(objectMapper.writeValueAsString(parsedData));
        }

        existingResume.setUploadedAt(LocalDateTime.now());

        Resume updatedResume = resumeRepository.save(existingResume);
        return modelMapper.map(updatedResume, ResumeDTO.class);
    }

    @Override
    public boolean checkResumeExists(Long candidateId, Long applicationId) {
        return resumeRepository.existsByCandidateIdAndApplicationId(candidateId, applicationId);
    }

    // ======================================================
    // PRIVATE HELPER METHODS
    // ======================================================

    /**
     * Fallback text extraction when API Layer fails
     */
    private String extractTextFromFile(MultipartFile file) {
        // Simple fallback - in real implementation, use Apache Tika or PDFBox
        return "Extracted content from " + file.getOriginalFilename();
    }

    /**
     * Generates SHA-256 hash of file content
     */
    private String generateFileHash(MultipartFile file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(file.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Fallback skills extraction using simple pattern matching
     */
    private List<String> extractSkills(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> extractedSkills = new ArrayList<>();

        // Common technical skills to detect
        List<String> commonSkills = List.of(
                "Java", "Spring", "Spring Boot", "Hibernate", "JPA", "Maven", "Gradle",
                "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch",
                "JavaScript", "TypeScript", "React", "Angular", "Vue.js", "Node.js",
                "Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD", "Jenkins",
                "Python", "Django", "Flask", "NumPy", "Pandas", "TensorFlow",
                "C#", ".NET", "ASP.NET", "C++", "Go", "Rust", "Ruby",
                "HTML", "CSS", "SASS", "Bootstrap", "Tailwind CSS",
                "REST API", "GraphQL", "Microservices", "DevOps", "Git", "Linux"
        );

        // Extract skills using case-insensitive pattern matching
        for (String skill : commonSkills) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                extractedSkills.add(skill);
            }
        }

        return extractedSkills;
    }

    /**
     * Creates fallback parsed data when API Layer is not available
     */
    private Map<String, Object> createFallbackParsedData(MultipartFile file, List<String> extractedSkills) {
        Map<String, Object> parsedData = new HashMap<>();
        parsedData.put("fileSize", file.getSize());
        parsedData.put("fileName", file.getOriginalFilename());
        parsedData.put("contentType", file.getContentType());
        parsedData.put("uploadDate", LocalDateTime.now().toString());
        parsedData.put("extractedSkillsCount", extractedSkills.size());
        parsedData.put("apiLayerUsed", false);
        parsedData.put("extractionMethod", "fallback");

        return parsedData;
    }
}