package ma.ensa.resumeservice.services.apilayer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ensa.resumeservice.exceptions.ApiLayerServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service responsible for making HTTP calls to API Layer resume parser API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiLayerHttpClient {
    @Value("${apilayer.resume-parser.api-key}")
    private String apiKey;

    @Value("${apilayer.resume-parser.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public String parseResumeFile(MultipartFile file) throws ApiLayerServiceException {
        validateFile(file);

        try {
            log.info("Sending resume to API Layer: {} ({}KB)",
                    file.getOriginalFilename(), file.getSize() / 1024);

            HttpEntity<byte[]> requestEntity = buildRequestEntity(file);
            String endpoint = baseUrl + "/upload";

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully received response from API Layer");
                return response.getBody();
            } else {
                throw new ApiLayerServiceException(
                        "API Layer returned status: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("HTTP error calling API Layer: {}", e.getMessage());
            throw new ApiLayerServiceException("HTTP call to API Layer failed", e);
        } catch (IOException e) {
            log.error("IO error processing file: {}", e.getMessage());
            throw new ApiLayerServiceException("File processing error", e);
        }
    }

    private void validateFile(MultipartFile file) throws ApiLayerServiceException {
        if (file == null || file.isEmpty()) {
            throw new ApiLayerServiceException("File is null or empty");
        }

        if (file.getSize() == 0) {
            throw new ApiLayerServiceException("File size is zero");
        }

        // Validate file format
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ApiLayerServiceException("File name is null");
        }

        String lowerFilename = filename.toLowerCase();
        if (!lowerFilename.endsWith(".pdf") &&
                !lowerFilename.endsWith(".doc") &&
                !lowerFilename.endsWith(".docx")) {
            throw new ApiLayerServiceException(
                    "Unsupported file format. Only PDF, DOC, and DOCX are supported.");
        }
    }

    private HttpEntity<byte[]> buildRequestEntity(MultipartFile file) throws IOException {
        // Prepare headers - API Layer expects application/octet-stream, not multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("apikey", apiKey);

        // Get file bytes directly
        byte[] fileBytes = file.getBytes();

        return new HttpEntity<>(fileBytes, headers);
    }
}