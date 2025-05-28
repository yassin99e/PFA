package ma.ensa.messagingservice.Feign;


import ma.ensa.messagingservice.DTOs.CandidateResponseDTO;
import ma.ensa.messagingservice.DTOs.RecruiterResponseDTO;
import ma.ensa.messagingservice.DTOs.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient("USERMICROSERVICE")
public interface UserInterface {

    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/candidate/all")
    List<CandidateResponseDTO> getAllCandidates();

    @GetMapping("/api/recruiter/all")
    List<RecruiterResponseDTO> getAllRecruiters();
}