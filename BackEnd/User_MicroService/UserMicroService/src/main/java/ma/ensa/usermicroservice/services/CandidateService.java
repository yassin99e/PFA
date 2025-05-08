package ma.ensa.usermicroservice.services;


import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.CandidateRequestDTO;
import ma.ensa.usermicroservice.dtos.CandidateResponseDTO;
import ma.ensa.usermicroservice.entities.Candidate;
import ma.ensa.usermicroservice.entities.Role;
import ma.ensa.usermicroservice.repositories.CandidateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
        modelMapper.map(request, candidate);
        candidateRepository.save(candidate);
    }
}
