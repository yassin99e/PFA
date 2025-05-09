
package ma.ensa.ai_matching.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import ma.ensa.ai_matching.RequestDTOS.MatchResponseDTO;
import ma.ensa.ai_matching.RequestDTOS.MatchingRequestDTO;
import ma.ensa.ai_matching.Service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private LLMService llmService;

    @PostMapping("/match")
    public MatchResponseDTO  match(@RequestBody MatchingRequestDTO request) throws JsonProcessingException {
        return llmService.match(request.getJobOffer(), request.getResumeInfo());
    }
}
