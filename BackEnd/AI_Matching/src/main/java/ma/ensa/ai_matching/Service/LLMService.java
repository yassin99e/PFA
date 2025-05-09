
package ma.ensa.ai_matching.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensa.ai_matching.RequestDTOS.MatchResponseDTO;
import ma.ensa.ai_matching.RequestDTOS.ResumeInfo;
import ma.ensa.ai_matching.RequestDTOS.jobOffer;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class LLMService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:/prompts/prompt.st")
    private Resource prompt;

    public LLMService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    public MatchResponseDTO match(jobOffer jobOffer, ResumeInfo resumeInfo)  {
        Map<String, Object> vars = new HashMap<>();


        vars.put("title", jobOffer.getTitle());
        vars.put("description", jobOffer.getDescription());
        vars.put("searchedProfile", jobOffer.getSearchedProfile());
        vars.put("requiredTechnologies", String.join(", ", jobOffer.getRequiredTechnologies()));
        vars.put("minYearsExperience", jobOffer.getMinYearsExperience());

        vars.put("fullName", resumeInfo.getFullName());
        vars.put("profileTitle", resumeInfo.getProfileTitle());
        vars.put("summary", resumeInfo.getSummary());
        vars.put("technologies", String.join(", ", resumeInfo.getTechnologies()));
        vars.put("totalYearsExperience", resumeInfo.getTotalYearsExperience());

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Prompt finalPrompt = promptTemplate.create(vars);

        System.out.println("Final Prompt:\n" + finalPrompt.getContents());

        String jsonResponse = chatClient.prompt(finalPrompt).call().content();


        return safeParseResponse(jsonResponse);
    }
    public MatchResponseDTO safeParseResponse(String rawResponse) {
        try {
            // Try direct parse
            return objectMapper.readValue(rawResponse, MatchResponseDTO.class);
        } catch (JsonProcessingException e) {
            // Try extracting JSON object from within text
            int start = rawResponse.indexOf("{");
            int end = rawResponse.lastIndexOf("}") + 1;

            if (start != -1 && end != -1 && start < end) {
                String possibleJson = rawResponse.substring(start, end);
                try {
                    return objectMapper.readValue(possibleJson, MatchResponseDTO.class);
                } catch (JsonProcessingException innerEx) {
                    throw new RuntimeException("LLM response could not be parsed into MatchResponseDTO", innerEx);
                }
            }

            throw new RuntimeException("No JSON object found in LLM response", e);
        }
    }


}
