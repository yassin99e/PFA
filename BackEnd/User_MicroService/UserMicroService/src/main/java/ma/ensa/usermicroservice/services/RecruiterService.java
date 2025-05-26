package ma.ensa.usermicroservice.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.usermicroservice.dtos.*;
import ma.ensa.usermicroservice.entities.Recruiter;
import ma.ensa.usermicroservice.entities.Role;
import ma.ensa.usermicroservice.repositories.RecruiterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;

    private final ModelMapper modelMapper;

    public List<RecruiterResponseDTO> getAllRecruiters() {
        List<Recruiter> allRecruiters = recruiterRepository.findAll();
        return allRecruiters.stream()
                .map(recruiter -> modelMapper.map(recruiter, RecruiterResponseDTO.class))
                .collect(Collectors.toList());
    }

    public RecruiterResponseDTO getRecruiterById(Long id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruiter not found with id: " + id));
        return modelMapper.map(recruiter, RecruiterResponseDTO.class);
    }

    public RecruiterResponseDTO save(RecruiterRequestDTO recruiterRequest) {
        log.info("Creating recruiter: {}", recruiterRequest.getEmail());

        Recruiter recruiter = modelMapper.map(recruiterRequest, Recruiter.class);
        recruiter.setRole(Role.RECRUITER);

        // Save and get the entity with generated ID
        Recruiter savedRecruiter = recruiterRepository.save(recruiter);

        log.info("Recruiter created with ID: {}", savedRecruiter.getId());

        // Return the DTO with the ID
        return modelMapper.map(savedRecruiter, RecruiterResponseDTO.class);
    }
    public void update(Long id, RecruiterRequestDTO request) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruiter not found with id: " + id));

        // Update only non-null fields
        if (request.getEmail() != null) recruiter.setEmail(request.getEmail());
        if (request.getFullName() != null) recruiter.setFullName(request.getFullName());
        if (request.getPassword() != null) recruiter.setPassword(request.getPassword());
        if (request.getCompany() != null) recruiter.setCompany(request.getCompany());
        if (request.getDepartment() != null) recruiter.setDepartment(request.getDepartment());

        recruiterRepository.save(recruiter);
    }

}
