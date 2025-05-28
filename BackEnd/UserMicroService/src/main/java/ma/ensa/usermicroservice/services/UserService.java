package ma.ensa.usermicroservice.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.ensa.usermicroservice.dtos.CandidateResponseDTO;
import ma.ensa.usermicroservice.dtos.RecruiterResponseDTO;
import ma.ensa.usermicroservice.dtos.UserResponseDTO;
import ma.ensa.usermicroservice.entities.Candidate;
import ma.ensa.usermicroservice.entities.Recruiter;
import ma.ensa.usermicroservice.entities.User;
import ma.ensa.usermicroservice.repositories.userRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final userRepository userrepository;

    private final ModelMapper modelMapper;


    public UserResponseDTO getUserById(Long id) {
        User user = userrepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO login(String email, String password) {
        return userrepository.findByEmailAndPassword(email, password)
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

}
