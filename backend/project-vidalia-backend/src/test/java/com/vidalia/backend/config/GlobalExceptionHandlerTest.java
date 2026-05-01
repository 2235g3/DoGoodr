package com.vidalia.backend.config;

import com.vidalia.backend.dto.error.ErrorResponse;
import com.vidalia.backend.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GlobalExceptionHandler
 * Tests that exceptions are properly caught and converted to ErrorResponse DTOs
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestExceptionController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    // ===== Test Controller for Exception Scenarios =====
    @RestController
    public static class TestExceptionController {

        @GetMapping("/test/resource-not-found")
        public void resourceNotFound() {
            throw new ResourceNotFoundException("Test resource not found");
        }

        @GetMapping("/test/resource-already-exists")
        public void resourceAlreadyExists() {
            throw new ResourceAlreadyExistsException("Test resource already exists");
        }

        @GetMapping("/test/incorrect-password")
        public void incorrectPassword() {
            throw new IncorrectOldPasswordException("Test password mismatch");
        }

        @GetMapping("/test/file-upload-validation")
        public void fileUploadValidation() {
            throw new FileUploadValidationException("Test file validation failed");
        }

        @GetMapping("/test/file-storage")
        public void fileStorage() {
            throw new FileStorageException("Test file storage error", new RuntimeException("Disk full"));
        }

        @GetMapping("/test/resource-creation")
        public void resourceCreation() {
            throw new ResourceCreationException("Test resource creation failed");
        }

        @GetMapping("/test/illegal-argument")
        public void illegalArgument() {
            throw new IllegalArgumentException("Test invalid argument");
        }

        @GetMapping("/test/bad-credentials")
        public void badCredentials() {
            throw new BadCredentialsException("Test bad credentials");
        }

        @GetMapping("/test/access-denied")
        public void accessDenied() {
            throw new AccessDeniedException("Test access denied");
        }

        @PostMapping("/test/validation")
        public void validation(@Valid @RequestBody TestValidationRequest request) {
            // This endpoint triggers validation errors via @Valid annotation
        }

        @GetMapping("/test/generic-error")
        public void genericError() {
            throw new RuntimeException("Test unexpected error");
        }

        // Test DTO for validation error testing
        public static class TestValidationRequest {
            @NotBlank(message = "Email is required")
            private String email;

            @NotBlank(message = "Password is required")
            private String password;

            public TestValidationRequest() {}
            public TestValidationRequest(String email, String password) {
                this.email = email;
                this.password = password;
            }

            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }
    }

    // ===== Tests for Custom Exceptions =====

    @Test
    void testResourceNotFoundExceptionReturns404() throws Exception {
        MvcResult result = mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.details").value("Test resource not found"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertEquals(404, response.getStatus());
        assertEquals("Resource not found", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testResourceAlreadyExistsExceptionReturns409() throws Exception {
        mockMvc.perform(get("/test/resource-already-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Resource already exists"))
                .andExpect(jsonPath("$.details").value("Test resource already exists"));
    }

    @Test
    void testIncorrectOldPasswordExceptionReturns401() throws Exception {
        mockMvc.perform(get("/test/incorrect-password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Incorrect password"))
                .andExpect(jsonPath("$.details").value("Test password mismatch"));
    }

    @Test
    void testFileUploadValidationExceptionReturns400() throws Exception {
        mockMvc.perform(get("/test/file-upload-validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("File upload validation failed"))
                .andExpect(jsonPath("$.details").value("Test file validation failed"));
    }

    @Test
    void testFileStorageExceptionReturns500() throws Exception {
        mockMvc.perform(get("/test/file-storage"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Failed to store file"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testResourceCreationExceptionReturns400() throws Exception {
        mockMvc.perform(get("/test/resource-creation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Failed to create resource"));
    }

    @Test
    void testIllegalArgumentExceptionReturns400() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid argument"))
                .andExpect(jsonPath("$.details").value("Test invalid argument"));
    }

    @Test
    void testBadCredentialsExceptionReturns401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication failed"));
    }

    @Test
    void testAccessDeniedExceptionReturns403() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.details").value("You do not have permission to access this resource"));
    }

    // ===== Tests for Spring Framework Exceptions =====

    @Test
    void testValidationExceptionReturns400WithFieldErrors() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new TestExceptionController.TestValidationRequest("", "")
        );

        MvcResult result = mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertNotNull(response.getFieldErrors());
        assertTrue(response.getFieldErrors().size() >= 2, "Should have field errors for email and password");
        assertTrue(response.getFieldErrors().containsKey("email"));
        assertTrue(response.getFieldErrors().containsKey("password"));
    }

    @Test
    void testMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    // ===== Tests for Generic Exception (Catch-all) =====

    @Test
    void testGenericExceptionReturns500() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ===== Tests for Response Structure =====

    @Test
    void testErrorResponseHasRequiredFields() throws Exception {
        MvcResult result = mockMvc.perform(get("/test/resource-not-found"))
                .andReturn();

        ErrorResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertNotNull(response.getStatus(), "Status should not be null");
        assertNotNull(response.getMessage(), "Message should not be null");
        assertNotNull(response.getTimestamp(), "Timestamp should not be null");
        assertNotNull(response.getPath(), "Path should not be null");
    }

    @Test
    void testErrorResponsePathIsCorrect() throws Exception {
        MvcResult result = mockMvc.perform(get("/test/resource-not-found"))
                .andReturn();

        ErrorResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertEquals("/test/resource-not-found", response.getPath());
    }

    @Test
    void testErrorResponseContentTypeIsJson() throws Exception {
        mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
