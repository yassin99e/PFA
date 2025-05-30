package ma.ensa.applicationservice.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.applicationservice.dtos.*;
import ma.ensa.applicationservice.entities.Application;
import ma.ensa.applicationservice.entities.ApplicationStatus;
import ma.ensa.applicationservice.repositories.ApplicationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ma.ensa.applicationservice.services.UserServiceFeignClient;
import ma.ensa.applicationservice.services.JobServiceFeignClient;
import ma.ensa.applicationservice.services.ResumeServiceFeignClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final UserServiceFeignClient userServiceFeignClient;
    private final ResumeServiceFeignClient resumeServiceFeignClient;
    private final JobServiceFeignClient jobServiceFeignClient;
    public void save(ApplicationRequestDTO applicationRequestDTO) {
        log.info("Creating application for candidate ID: {} and job ID: {}",
                applicationRequestDTO.getCandidateId(), applicationRequestDTO.getJobOfferId());

        // Step 1: Validate candidate exists
        validateCandidate(applicationRequestDTO.getCandidateId());

        // Step 2: Validate job offer exists
        JobOfferResponseDTO jobOffer = validateJobOffer(applicationRequestDTO.getJobOfferId());

        // Step 3: Check for duplicate application
        checkForDuplicateApplication(applicationRequestDTO.getCandidateId(), applicationRequestDTO.getJobOfferId());

        // Step 4: Handle resume selection
        Long resumeId = handleResumeSelection(applicationRequestDTO);

        // Step 5: Create application - MANUAL MAPPING instead of ModelMapper
        Application application = new Application();
        application.setId(null); // Ensure new entity
        application.setCandidateId(applicationRequestDTO.getCandidateId());
        application.setJobOfferId(applicationRequestDTO.getJobOfferId());
        application.setResumeId(resumeId);
        application.setStatus(ApplicationStatus.PENDING);
        // appliedAt will be set by @PrePersist

        Application savedApplication = applicationRepository.save(application);

        log.info("Application created successfully with ID: {}", savedApplication.getId());
    }

    public ApplicationResponseDTO getApplicationById(Long id) {
        log.info("Fetching application with ID: {}", id);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        return modelMapper.map(application, ApplicationResponseDTO.class);
    }

    public List<ApplicationResponseDTO> getApplicationsByCandidateId(Long candidateId) {
        log.info("Fetching applications for candidate ID: {}", candidateId);

        // Validate candidate exists
        validateCandidate(candidateId);

        List<Application> applications = applicationRepository.findByCandidateId(candidateId);

        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDTO> getApplicationsByJobOfferId(Long jobOfferId) {
        log.info("Fetching applications for job offer ID: {}", jobOfferId);

        // Validate job offer exists
        validateJobOffer(jobOfferId);

        List<Application> applications = applicationRepository.findByJobOfferId(jobOfferId);

        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDTO> getApplicationsByRecruiterId(Long recruiterId) {
        log.info("Fetching applications for recruiter ID: {}", recruiterId);

        try {
            // Get all job offers by this recruiter
            ResponseEntity<List<JobOfferResponseDTO>> jobOffersResponse =
                    jobServiceFeignClient.getJobOffersByRecruiterId(recruiterId);

            if (jobOffersResponse.getBody() == null || jobOffersResponse.getBody().isEmpty()) {
                log.info("No job offers found for recruiter ID: {}", recruiterId);
                return List.of();
            }

            // Get applications for all these job offers
            List<Long> jobOfferIds = jobOffersResponse.getBody().stream()
                    .map(JobOfferResponseDTO::getId)
                    .toList();

            List<Application> applications = jobOfferIds.stream()
                    .flatMap(jobOfferId -> applicationRepository.findByJobOfferId(jobOfferId).stream())
                    .toList();

            return applications.stream()
                    .map(application -> modelMapper.map(application, ApplicationResponseDTO.class))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to fetch applications for recruiter: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch applications for recruiter: " + e.getMessage());
        }
    }

    public List<ApplicationResponseDTO> getApplicationsByCandidateIdAndStatus(Long candidateId, String status) {
        log.info("Fetching applications for candidate ID: {} with status: {}", candidateId, status);

        validateCandidate(candidateId);

        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        List<Application> applications = applicationRepository.findByCandidateIdAndStatus(candidateId, applicationStatus);

        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDTO> getApplicationsByJobOfferIdAndStatus(Long jobOfferId, String status) {
        log.info("Fetching applications for job offer ID: {} with status: {}", jobOfferId, status);

        validateJobOffer(jobOfferId);

        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        List<Application> applications = applicationRepository.findByJobOfferIdAndStatus(jobOfferId, applicationStatus);

        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void updateStatus(Long id, String status) {
        log.info("Updating application ID: {} to status: {}", id, status);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
        application.setStatus(newStatus);

        applicationRepository.save(application);

        log.info("Application status updated successfully");
    }

    public void update(Long id, ApplicationRequestDTO applicationRequestDTO) {
        log.info("Updating application with ID: {}", id);

        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));

        // Validate new candidate and job offer if they're different
        if (!existingApplication.getCandidateId().equals(applicationRequestDTO.getCandidateId())) {
            validateCandidate(applicationRequestDTO.getCandidateId());
        }

        if (!existingApplication.getJobOfferId().equals(applicationRequestDTO.getJobOfferId())) {
            validateJobOffer(applicationRequestDTO.getJobOfferId());
        }

        // Handle resume selection
        Long resumeId = handleResumeSelection(applicationRequestDTO);

        // Update fields
        existingApplication.setCandidateId(applicationRequestDTO.getCandidateId());
        existingApplication.setJobOfferId(applicationRequestDTO.getJobOfferId());
        existingApplication.setResumeId(resumeId);

        applicationRepository.save(existingApplication);

        log.info("Application updated successfully");
    }

    public void delete(Long id) {
        log.info("Deleting application with ID: {}", id);

        if (!applicationRepository.existsById(id)) {
            throw new RuntimeException("Application not found with ID: " + id);
        }

        applicationRepository.deleteById(id);

        log.info("Application deleted successfully");
    }

    // Private helper methods
    private void validateCandidate(Long candidateId) {
        try {
            ResponseEntity<UserResponseDTO> userResponse = userServiceFeignClient.getUserById(candidateId);
            if (userResponse.getBody() == null) {
                throw new RuntimeException("Candidate not found with ID: " + candidateId);
            }
            log.info("Candidate verified: {}", userResponse.getBody().getFullName());
        } catch (Exception e) {
            log.error("Failed to verify candidate: {}", e.getMessage());
            throw new RuntimeException("Candidate verification failed: " + e.getMessage());
        }
    }

    private JobOfferResponseDTO validateJobOffer(Long jobOfferId) {
        try {
            ResponseEntity<JobOfferResponseDTO> jobResponse = jobServiceFeignClient.getJobOfferById(jobOfferId);
            if (jobResponse.getBody() == null) {
                throw new RuntimeException("Job offer not found with ID: " + jobOfferId);
            }
            log.info("Job offer verified: {}", jobResponse.getBody().getTitle());
            return jobResponse.getBody();
        } catch (Exception e) {
            log.error("Failed to verify job offer: {}", e.getMessage());
            throw new RuntimeException("Job offer verification failed: " + e.getMessage());
        }
    }

    private void checkForDuplicateApplication(Long candidateId, Long jobOfferId) {
        Optional<Application> existingApplication =
                applicationRepository.findByCandidateIdAndJobOfferId(candidateId, jobOfferId);

        if (existingApplication.isPresent()) {
            throw new RuntimeException("Candidate has already applied for this job");
        }
    }

    private Long handleResumeSelection(ApplicationRequestDTO applicationRequestDTO) {
        Long resumeId = applicationRequestDTO.getResumeId();

        if (resumeId != null) {
            // Validate specific resume exists
            try {
                ResponseEntity<ResumeDTO> resumeResponse = resumeServiceFeignClient.getResumeById(resumeId);
                if (resumeResponse.getBody() == null) {
                    throw new RuntimeException("Resume not found with ID: " + resumeId);
                }
                log.info("Resume verified: {}", resumeResponse.getBody().getFileName());
                return resumeId;
            } catch (Exception e) {
                throw new RuntimeException("Resume verification failed: " + e.getMessage());
            }
        } else {
            // Auto-select latest resume for candidate
            try {
                ResponseEntity<List<ResumeDTO>> resumesResponse =
                        resumeServiceFeignClient.getResumesByCandidateId(applicationRequestDTO.getCandidateId());

                if (resumesResponse.getBody() == null || resumesResponse.getBody().isEmpty()) {
                    throw new RuntimeException("No resume found for candidate. Please upload a resume first.");
                }

                // Get the latest resume (assuming list is ordered by upload date)
                ResumeDTO latestResume = resumesResponse.getBody().get(0);
                log.info("Auto-selected latest resume: {}", latestResume.getFileName());
                return latestResume.getId();

            } catch (Exception e) {
                throw new RuntimeException("Failed to auto-select resume: " + e.getMessage());
            }
        }
    }

}
