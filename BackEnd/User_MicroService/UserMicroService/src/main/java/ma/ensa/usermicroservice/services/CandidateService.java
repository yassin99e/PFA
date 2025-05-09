package ma.ensa.usermicroservice.services;


import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.CandidateRequestDTO;
import ma.ensa.usermicroservice.dtos.CandidateResponseDTO;
import ma.ensa.usermicroservice.entities.Candidate;
import ma.ensa.usermicroservice.entities.Role;
import ma.ensa.usermicroservice.repositories.CandidateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    private final ModelMapper modelMapper;


    public List<CandidateResponseDTO> getAllCandidates() {
        List<Candidate> allCandidate = candidateRepository.findAll();
        return allCandidate.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateResponseDTO.class))
                .collect(Collectors.toList());
    }

    public CandidateResponseDTO getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
        return modelMapper.map(candidate, CandidateResponseDTO.class);
    }

    public void save(CandidateRequestDTO request) {
        Candidate candidate = modelMapper.map(request, Candidate.class);
        candidate.setRole(Role.CANDIDATE);
        candidateRepository.save(candidate);
    }

    public void update(Long id, CandidateRequestDTO request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("candidate not found with id: " + id));

        if (request.getEmail() != null) candidate.setEmail(request.getEmail());
        if (request.getFullName() != null) candidate.setFullName(request.getFullName());
        if (request.getPassword() != null) candidate.setPassword(request.getPassword());
        if (request.getDiploma() != null) candidate.setDiploma(request.getDiploma());
        if (request.getPhone() != null) candidate.setPhone(request.getPhone());
        if (request.getProfile() != null) candidate.setProfile(request.getProfile());
        if (request.getYearsOfExperience() != 0) candidate.setYearsOfExperience(request.getYearsOfExperience());
        // Append to existing list if non-null
        if (request.getInterestedProfiles() != null) {
            if (candidate.getInterestedProfiles() == null) {
                candidate.setInterestedProfiles(new ArrayList<>());
            }
            candidate.getInterestedProfiles().addAll(request.getInterestedProfiles());
        }
        if (request.getTechnologies() != null) {
            if (candidate.getTechnologies() == null) {
                candidate.setTechnologies(new ArrayList<>());
            }
            candidate.getTechnologies().addAll(request.getTechnologies());
        }

        candidateRepository.save(candidate);
    }

}
