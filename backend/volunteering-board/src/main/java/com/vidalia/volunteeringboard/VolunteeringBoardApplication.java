package com.vidalia.volunteeringboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VolunteeringBoardApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VolunteeringBoardApplication.class);

        // UPDATE_THIS_BEFORE_USE
        // prod - for production use
        // dev - for development use
        // test - only for running tests
        app.setAdditionalProfiles("dev");
        app.run(args);
	}

}
