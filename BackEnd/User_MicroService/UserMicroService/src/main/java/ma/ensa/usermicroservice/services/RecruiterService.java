package ma.ensa.usermicroservice.services;


import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.*;
import ma.ensa.usermicroservice.entities.Candidate;
import ma.ensa.usermicroservice.entities.Recruiter;
import ma.ensa.usermicroservice.entities.Role;
import ma.ensa.usermicroservice.entities.User;
import ma.ensa.usermicroservice.repositories.RecruiterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    public void save(RecruiterRequestDTO recruiter) {
        Recruiter rec = modelMapper.map(recruiter, Recruiter.class);
        rec.setRole(Role.RECRUITER);
        recruiterRepository.save(rec);
    }
    public void update(Long id, RecruiterRequestDTO request) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruiter not found with id: " + id));
        modelMapper.map(request, recruiter);
        recruiterRepository.save(recruiter);
    }
}
