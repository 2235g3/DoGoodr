package com.vidalia.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class WebResourceConfig implements WebMvcConfigurer {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadRoot = Path.of(fileUploadProperties.getRootDir()).toAbsolutePath().normalize();
        String uploadRootLocation = uploadRoot.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadRootLocation);
    }
}

