package ma.ensa.resumeservice.services.apilayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.resumeservice.dtos.ApiLayerParseResult;
import ma.ensa.resumeservice.exceptions.ApiLayerParseException;
import ma.ensa.resumeservice.exceptions.ApiLayerServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * Main orchestrator service for API Layer resume parsing
 * Coordinates between HTTP client and response parser
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiLayerResumeParser {
    @Value("${apilayer.resume-parser.enabled:true}")
    private boolean enabled;

    private final ApiLayerHttpClient httpClient;
    private final ApiLayerResponseParser responseParser;

    /**
     * Parses a resume file using API Layer service
     *
     * @param file The resume file to parse
     * @return Parsed result with extracted information
     */
    public ApiLayerParseResult parseResume(MultipartFile file) {
        if (!enabled) {
            log.info("API Layer parsing is disabled, using fallback");
            return createFallbackResult(file, "API Layer parsing disabled");
        }

        if (!isValidFile(file)) {
            log.warn("Invalid file provided for parsing");
            return createFallbackResult(file, "Invalid file");
        }

        try {
            log.info("Starting resume parsing with API Layer: {}", file.getOriginalFilename());

            // Step 1: Call API Layer
            String jsonResponse = httpClient.parseResumeFile(file);

            // Step 2: Parse response
            ApiLayerParseResult result = responseParser.parseJsonResponse(jsonResponse);

            log.info("Successfully parsed resume: {} skills extracted",
                    result.getSkills().size());

            return result;

        } catch (ApiLayerServiceException e) {
            log.error("API Layer service call failed: {}", e.getMessage());
            return createFallbackResult(file, "API service call failed");

        } catch (ApiLayerParseException e) {
            log.error("API Layer response parsing failed: {}", e.getMessage());
            return createFallbackResult(file, "Response parsing failed");

        } catch (Exception e) {
            log.error("Unexpected error during resume parsing: ", e);
            return createFallbackResult(file, "Unexpected error occurred");
        }
    }

    private boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty() && file.getSize() > 0;
    }

    private ApiLayerParseResult createFallbackResult(MultipartFile file, String reason) {
        String fileName = file != null ? file.getOriginalFilename() : "unknown";

        log.warn("Creating fallback result for file '{}': {}", fileName, reason);

        return ApiLayerParseResult.builder()
                .success(false)
                .extractedText("Extracted content from " + fileName)
                .structuredData(String.format("{\"error\": \"%s\", \"fallback\": true}", reason))
                .skills(new ArrayList<>())
                .build();
    }
}
