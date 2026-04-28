package com.vidalia.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectVidaliaApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ProjectVidaliaApplication.class);

		// Allow profile to be set via SPRING_PROFILES_ACTIVE env var
		// Default to 'dev' if not specified (for backwards compatibility)
		String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
		if (activeProfile == null || activeProfile.isBlank()) {
			activeProfile = "dev";
		}
		app.setAdditionalProfiles(activeProfile);
        app.run(args);
	}

}
