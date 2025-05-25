package ma.ensa.resumeservice.services.apilayer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.resumeservice.dtos.ApiLayerParseResult;
import ma.ensa.resumeservice.exceptions.ApiLayerParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiLayerResponseParser {
    private final ObjectMapper objectMapper;

    /**
     * Parses API Layer JSON response into structured data
     *
     * @param jsonResponse Raw JSON response from API Layer
     * @return Parsed result with extracted information
     * @throws ApiLayerParseException if parsing fails
     */
    public ApiLayerParseResult parseJsonResponse(String jsonResponse) throws ApiLayerParseException {
        try {
            log.debug("Parsing API Layer response (length: {} chars)", jsonResponse.length());

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            return ApiLayerParseResult.builder()
                    .success(true)
                    .extractedText(extractText(rootNode))
                    .structuredData(jsonResponse)
                    .skills(extractSkills(rootNode))
                    .candidateName(extractName(rootNode))
                    .email(extractEmail(rootNode))
                    .phone(extractPhone(rootNode))
                    .totalYearsExperience(extractTotalExperience(rootNode))
                    .experience(extractExperience(rootNode))
                    .education(extractEducation(rootNode))
                    .personalInfo(extractPersonalInfo(rootNode))
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse API Layer response: {}", e.getMessage());
            throw new ApiLayerParseException("Failed to parse API Layer response", e);
        }
    }

    // ======================================================
    // TEXT EXTRACTION
    // ======================================================

    private String extractText(JsonNode response) {
        return Stream.of("raw_text", "text", "content", "extracted_text", "document_text")
                .map(response::path)
                .filter(node -> !node.isMissingNode() && node.isTextual())
                .map(JsonNode::asText)
                .filter(text -> text != null && !text.trim().isEmpty())
                .findFirst()
                .orElse("No text content extracted");
    }

    // ======================================================
    // SKILLS EXTRACTION
    // ======================================================

    private List<String> extractSkills(JsonNode response) {
        return Stream.of("skills", "technical_skills", "technologies", "competencies")
                .map(response::path)
                .filter(node -> !node.isMissingNode())
                .findFirst()
                .map(this::parseSkillsNode)
                .orElse(new ArrayList<>());
    }

    private List<String> parseSkillsNode(JsonNode skillsNode) {
        List<String> skills = new ArrayList<>();

        if (skillsNode.isArray()) {
            for (JsonNode skillNode : skillsNode) {
                if (skillNode.isTextual()) {
                    skills.add(skillNode.asText().trim());
                } else if (skillNode.isObject() && skillNode.has("name")) {
                    skills.add(skillNode.get("name").asText().trim());
                }
            }
        } else if (skillsNode.isTextual()) {
            String[] skillArray = skillsNode.asText().split("[,;|•\\n\\r]+");
            for (String skill : skillArray) {
                String cleanSkill = skill.trim();
                if (!cleanSkill.isEmpty()) {
                    skills.add(cleanSkill);
                }
            }
        }

        return skills.stream()
                .distinct()
                .filter(skill -> skill.length() > 1)
                .toList();
    }

    // ======================================================
    // PERSONAL INFORMATION EXTRACTION
    // ======================================================

    private String extractName(JsonNode response) {
        return Stream.of("personal_info.name", "name", "candidate_name", "full_name")
                .map(path -> getNestedValue(response, path))
                .filter(node -> !node.isMissingNode() && node.isTextual())
                .map(JsonNode::asText)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .findFirst()
                .orElse(null);
    }

    private String extractEmail(JsonNode response) {
        return Stream.of("personal_info.email", "email", "contact_info.email")
                .map(path -> getNestedValue(response, path))
                .filter(node -> !node.isMissingNode() && node.isTextual())
                .map(JsonNode::asText)
                .map(String::trim)
                .filter(this::isValidEmail)
                .findFirst()
                .orElse(null);
    }

    private String extractPhone(JsonNode response) {
        return Stream.of("personal_info.phone", "phone", "contact_info.phone", "mobile")
                .map(path -> getNestedValue(response, path))
                .filter(node -> !node.isMissingNode() && node.isTextual())
                .map(JsonNode::asText)
                .map(String::trim)
                .filter(phone -> !phone.isEmpty())
                .findFirst()
                .orElse(null);
    }

    private String extractPersonalInfo(JsonNode response) {
        JsonNode personalNode = Stream.of("personal_info", "contact_info", "personal_details")
                .map(response::path)
                .filter(node -> !node.isMissingNode())
                .findFirst()
                .orElse(null);

        return personalNode != null ? personalNode.toString() : null;
    }

    // ======================================================
    // EXPERIENCE EXTRACTION
    // ======================================================

    private String extractExperience(JsonNode response) {
        JsonNode expNode = Stream.of("work_experience", "experience", "employment", "career_history")
                .map(response::path)
                .filter(node -> !node.isMissingNode())
                .findFirst()
                .orElse(null);

        if (expNode != null) {
            if (expNode.isArray() && expNode.size() > 0) {
                return expNode.toString();
            } else if (expNode.isTextual()) {
                return expNode.asText();
            }
        }

        return null;
    }

    private Integer extractTotalExperience(JsonNode response) {
        // Try direct numeric fields first
        Integer totalYears = Stream.of("total_experience_years", "years_of_experience", "experience_years")
                .map(response::path)
                .filter(node -> !node.isMissingNode())
                .map(this::parseExperienceNumber)
                .filter(years -> years != null && years > 0 && years <= 50)
                .findFirst()
                .orElse(null);

        return totalYears;
    }

    private Integer parseExperienceNumber(JsonNode node) {
        if (node.isNumber()) {
            return node.asInt();
        } else if (node.isTextual()) {
            return extractYearsFromText(node.asText());
        }
        return null;
    }

    private Integer extractYearsFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        Pattern yearPattern = Pattern.compile("(\\d+)(?:\\+|\\s*[-–]\\s*\\d+)?\\s*(?:years?|yrs?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = yearPattern.matcher(text);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    // ======================================================
    // EDUCATION EXTRACTION
    // ======================================================

    private String extractEducation(JsonNode response) {
        JsonNode eduNode = Stream.of("education", "academic_background", "qualifications", "degrees")
                .map(response::path)
                .filter(node -> !node.isMissingNode())
                .findFirst()
                .orElse(null);

        if (eduNode != null) {
            if (eduNode.isArray() && eduNode.size() > 0) {
                return eduNode.toString();
            } else if (eduNode.isTextual()) {
                return eduNode.asText();
            }
        }

        return null;
    }

    // ======================================================
    // UTILITY METHODS
    // ======================================================

    private JsonNode getNestedValue(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode current = root;

        for (String part : parts) {
            current = current.path(part);
            if (current.isMissingNode()) {
                break;
            }
        }

        return current;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(email.trim()).matches();
    }
}
