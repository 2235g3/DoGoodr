package com.vidalia.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.upload")
@Validated
@Getter
@Setter
public class FileUploadProperties {

    @NotBlank
    private String rootDir;

    @NotBlank
    private String profilePicturesDir;

    @NotBlank
    private String profilePicturesUrlPrefix;

    @NotBlank
    private String defaultProfilePictureUrl;

    @NotNull
    private DataSize profilePictureMaxSize;

    private List<String> profilePicturesAllowedContentTypes = new ArrayList<>();

    @NotBlank
    private String cvDir;

    @NotBlank
    private String cvUrlPrefix;

    @NotNull
    private DataSize cvMaxSize;

    private List<String> cvAllowedContentTypes = new ArrayList<>();
}

