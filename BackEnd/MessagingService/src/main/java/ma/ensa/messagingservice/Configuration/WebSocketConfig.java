// BackEnd/MessagingService/MessagingService/src/main/java/ma/ensa/messagingservice/Configuration/WebSocketConfig.java
package ma.ensa.messagingservice.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for sending messages to clients
        config.enableSimpleBroker("/topic", "/queue");

        // Set prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");

        // Set prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Add CORS back since we're connecting directly now
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }
}