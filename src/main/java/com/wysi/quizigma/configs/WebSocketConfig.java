package com.wysi.quizigma.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.wysi.quizigma.service.GameService;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private GameService gameService;

    @Autowired
    public void setGameService(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(taskScheduler());
        config.setApplicationDestinationPrefixes("/quizz");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/player")
                .addInterceptors(new PlayerHandshakeInterceptor(gameService))
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(10000);
        registry.addEndpoint("/creator")
                .addInterceptors(new CreatorHandshakeInterceptor(gameService))
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(10000);
        registry.addEndpoint("/assignment")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(10000);

    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
