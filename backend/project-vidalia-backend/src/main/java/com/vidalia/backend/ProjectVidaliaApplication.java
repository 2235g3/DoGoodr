package com.vidalia.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectVidaliaApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ProjectVidaliaApplication.class);

        // UPDATE_THIS_BEFORE_USE
        // prod - for production use
        // dev - for development use
        // test - only for running tests
        app.setAdditionalProfiles("dev");
        app.run(args);
	}

}
