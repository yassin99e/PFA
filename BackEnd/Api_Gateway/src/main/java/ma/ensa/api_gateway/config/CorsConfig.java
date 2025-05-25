// BackEnd/Api_Gateway/src/main/java/ma/ensa/api_gateway/config/CorsConfig.java
package ma.ensa.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow credentials
        corsConfig.setAllowCredentials(true);

        // Add allowed origin (ONLY ONCE!)
        corsConfig.addAllowedOrigin("http://localhost:4200");

        // Allow all headers
        corsConfig.addAllowedHeader("*");

        // Allow all methods
        corsConfig.addAllowedMethod("*");

        // Expose headers for WebSocket
        corsConfig.addExposedHeader("*");

        // Set max age to avoid preflight requests
        corsConfig.setMaxAge(3600L);

        // Apply configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}