package com.wysi.quizigma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class QuizigmaApplication {

    public static void main(String[] args) {
		if(isRunningLocally()){
			Dotenv dotenv = Dotenv.configure().load();
			System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
			System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
			System.setProperty("SECRET", dotenv.get("SECRET"));
		}
		SpringApplication.run(QuizigmaApplication.class, args);
    }

    private static boolean isRunningLocally() {
        // Detect if running locally (e.g., absence of Render-specific environment variables)
        return System.getenv("RENDER") == null;
    }
}
