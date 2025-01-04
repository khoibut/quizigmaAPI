package com.wysi.quizigma.configs;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // Matches all endpoints
                .allowedOrigins("*") // Allows requests from any origin
                .allowedMethods("*") // Allows all HTTP methods
                .allowedHeaders("*") // Allows all headers
                .allowCredentials(false); // Disable credentials for full disablement
    }
}

