package ma.ensa.jobservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.jobservice.services.UserServiceFeignClient;
import ma.ensa.jobservice.dtos.JobOfferRequestDTO;
import ma.ensa.jobservice.dtos.JobOfferResponseDTO;
import ma.ensa.jobservice.dtos.RecruiterResponseDTO;
import ma.ensa.jobservice.entities.JobOffer;
import ma.ensa.jobservice.repositories.JobOfferRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final ModelMapper modelMapper;
    private final UserServiceFeignClient userServiceFeignClient;

    public void save(JobOfferRequestDTO jobOfferRequestDTO) {
        log.info("Creating job offer for recruiter ID: {}", jobOfferRequestDTO.getRecruiterId());

        // Verify recruiter exists
        try {
            ResponseEntity<RecruiterResponseDTO> recruiterResponse =
                    userServiceFeignClient.getRecruiterById(jobOfferRequestDTO.getRecruiterId());

            if (recruiterResponse.getBody() == null) {
                throw new RuntimeException("Recruiter not found with ID: " + jobOfferRequestDTO.getRecruiterId());
            }

            log.info("Recruiter verified: {}", recruiterResponse.getBody().getCompany());
        } catch (Exception e) {
            log.error("Failed to verify recruiter: {}", e.getMessage());
            throw new RuntimeException("Recruiter verification failed: " + e.getMessage());
        }

        // Map DTO to Entity and ENSURE ID IS NULL for new entity
        JobOffer jobOffer = modelMapper.map(jobOfferRequestDTO, JobOffer.class);
        jobOffer.setId(null); // Force ID to null for new entity

        JobOffer savedJobOffer = jobOfferRepository.save(jobOffer);

        log.info("Job offer created successfully with ID: {}", savedJobOffer.getId());
    }

    public JobOfferResponseDTO getJobOfferById(Long id) {
        log.info("Fetching job offer with ID: {}", id);

        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found with ID: " + id));

        return modelMapper.map(jobOffer, JobOfferResponseDTO.class);
    }

    public List<JobOfferResponseDTO> getAllActiveJobOffers() {
        log.info("Fetching all active job offers");

        List<JobOffer> jobOffers = jobOfferRepository.findAllActiveJobOffers();

        return jobOffers.stream()
                .map(jobOffer -> modelMapper.map(jobOffer, JobOfferResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<JobOfferResponseDTO> getJobOffersByRecruiterId(Long recruiterId) {
        log.info("Fetching job offers for recruiter ID: {}", recruiterId);

        List<JobOffer> jobOffers = jobOfferRepository.findByRecruiterId(recruiterId);

        return jobOffers.stream()
                .map(jobOffer -> modelMapper.map(jobOffer, JobOfferResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void update(Long id, JobOfferRequestDTO jobOfferRequestDTO) {
        log.info("Updating job offer with ID: {}", id);

        JobOffer existingJobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found with ID: " + id));

        // Verify recruiter exists if recruiterId is being changed
        if (!existingJobOffer.getRecruiterId().equals(jobOfferRequestDTO.getRecruiterId())) {
            try {
                ResponseEntity<RecruiterResponseDTO> recruiterResponse =
                        userServiceFeignClient.getRecruiterById(jobOfferRequestDTO.getRecruiterId());

                if (recruiterResponse.getBody() == null) {
                    throw new RuntimeException("Recruiter not found with ID: " + jobOfferRequestDTO.getRecruiterId());
                }
            } catch (Exception e) {
                throw new RuntimeException("Recruiter verification failed: " + e.getMessage());
            }
        }

        // Update fields
        existingJobOffer.setTitle(jobOfferRequestDTO.getTitle());
        existingJobOffer.setDescription(jobOfferRequestDTO.getDescription());
        existingJobOffer.setSearchProfile(jobOfferRequestDTO.getSearchProfile());
        existingJobOffer.setRequiredTechnologies(jobOfferRequestDTO.getRequiredTechnologies());
        existingJobOffer.setMinYearsExperience(jobOfferRequestDTO.getMinYearsExperience());
        existingJobOffer.setRecruiterId(jobOfferRequestDTO.getRecruiterId());

        jobOfferRepository.save(existingJobOffer);

        log.info("Job offer updated successfully");
    }

    public void delete(Long id) {
        log.info("Deleting job offer with ID: {}", id);

        if (!jobOfferRepository.existsById(id)) {
            throw new RuntimeException("Job offer not found with ID: " + id);
        }

        jobOfferRepository.deleteById(id);

        log.info("Job offer deleted successfully");
    }
}