package ma.ensa.resumeservice.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.resumeservice.dtos.ResumeDTO;
import ma.ensa.resumeservice.dtos.ResumeUploadDTO;
import ma.ensa.resumeservice.entities.Resume;
import ma.ensa.resumeservice.repositories.ResumeRepository;
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
    @Override
    public ResumeDTO uploadResume(ResumeUploadDTO resumeUploadDTO, MultipartFile file) throws IOException {
        // Check if candidate exists
        ResponseEntity<Boolean> candidateResponse = userServiceClient.checkCandidateExists(resumeUploadDTO.getCandidateId());
        if (!Boolean.TRUE.equals(candidateResponse.getBody())) {
            throw new EntityNotFoundException("Candidate not found with ID: " + resumeUploadDTO.getCandidateId());
        }

        // Create resume entity
        Resume resume = new Resume();
        modelMapper.map(resumeUploadDTO, resume);

        // Set file metadata
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        resume.setFileSize(file.getSize());
        resume.setFileData(file.getBytes());

        // Extract text content from file
        String content = extractTextFromFile(file);
        resume.setContent(content);

        // Generate file hash to prevent duplicates
        resume.setFileHash(generateFileHash(file));

        // Extract skills from content
        List<String> extractedSkills = extractSkills(content);
        resume.setExtractedSkills(extractedSkills);

        // Create parsed data JSON
        Map<String, Object> parsedData = new HashMap<>();
        parsedData.put("fileSize", file.getSize());
        parsedData.put("fileName", file.getOriginalFilename());
        parsedData.put("contentType", file.getContentType());
        parsedData.put("uploadDate", LocalDateTime.now().toString());
        parsedData.put("extractedSkillsCount", extractedSkills.size());

        resume.setParsedData(objectMapper.writeValueAsString(parsedData));

        // Set upload timestamp
        resume.setUploadedAt(LocalDateTime.now());

        // Save to database
        Resume savedResume = resumeRepository.save(resume);
        return modelMapper.map(savedResume, ResumeDTO.class);
    }
    private String extractTextFromFile(MultipartFile file) throws IOException {
        // In a real implementation, you would use libraries like Apache Tika or PDFBox to extract text
        // For simplicity, we'll assume the file content is already text or return the filename as a placeholder
        return "Extracted content from " + file.getOriginalFilename();
    }

    private String generateFileHash(MultipartFile file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(file.getBytes());

            // Convert byte array to hex string
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
            throw new RuntimeException("Could not generate hash for file", e);
        }
    }

    private List<String> extractSkills(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> extractedSkills = new ArrayList<>();

        // Sample list of common skills to detect - in a real app you'd have a more comprehensive list
        List<String> commonSkills = List.of(
                "Java", "Spring", "Spring Boot", "Hibernate", "JPA", "Maven", "Gradle",
                "PostgreSQL", "MySQL", "MongoDB", "REST API", "Microservices", "Docker",
                "Kubernetes", "AWS", "Azure", "JavaScript", "React", "Angular", "Node.js"
        );

        // Extract skills from the common list
        for (String skill : commonSkills) {
            // Use word boundary to match whole words
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                extractedSkills.add(skill);
            }
        }

        return extractedSkills;
    }
}
