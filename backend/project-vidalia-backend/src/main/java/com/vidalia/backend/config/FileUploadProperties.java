package com.vidalia.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class FileUploadProperties {

    private String rootDir;
    private String profilePicturesDir;
    private String profilePicturesUrlPrefix;
    private String defaultProfilePictureUrl;
    private DataSize profilePictureMaxSize = DataSize.ofMegabytes(5);
    private List<String> profilePicturesAllowedContentTypes = new ArrayList<>();

    private String cvDir;
    private String cvUrlPrefix;
    private DataSize cvMaxSize = DataSize.ofMegabytes(10);
    private List<String> cvAllowedContentTypes = new ArrayList<>();
}

