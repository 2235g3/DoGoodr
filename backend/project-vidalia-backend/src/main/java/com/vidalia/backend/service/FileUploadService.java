package com.vidalia.backend.service;

import com.vidalia.backend.config.FileUploadProperties;
import com.vidalia.backend.exceptions.FileStorageException;
import com.vidalia.backend.exceptions.FileUploadValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final Set<String> PROFILE_PICTURE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private static final Set<String> CV_EXTENSIONS = Set.of(".pdf");

    private final FileUploadProperties fileUploadProperties;

    public String uploadProfilePicture(MultipartFile file, UUID userId) {
        validateFile(file, fileUploadProperties.getProfilePictureMaxSize().toBytes(),
                fileUploadProperties.getProfilePicturesAllowedContentTypes(), PROFILE_PICTURE_EXTENSIONS,
                "profile picture");

        String extension = getSafeExtension(file.getOriginalFilename(), PROFILE_PICTURE_EXTENSIONS, "profile picture");
        return storeFile(file, fileUploadProperties.getProfilePicturesDir(), fileUploadProperties.getProfilePicturesUrlPrefix(),
                userId, extension, "profile picture");
    }

    public String uploadCV(MultipartFile file, UUID userId) {
        validateFile(file, fileUploadProperties.getCvMaxSize().toBytes(),
                fileUploadProperties.getCvAllowedContentTypes(), CV_EXTENSIONS, "CV file");

        String extension = getSafeExtension(file.getOriginalFilename(), CV_EXTENSIONS, "CV file");
        return storeFile(file, fileUploadProperties.getCvDir(), fileUploadProperties.getCvUrlPrefix(), userId, extension, "CV file");
    }

    private void validateFile(MultipartFile file,
                              long maxSizeBytes,
                              java.util.List<String> allowedContentTypes,
                              Set<String> allowedExtensions,
                              String filePurposeLabel) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadValidationException("Uploaded " + filePurposeLabel + " is empty");
        }

        if (file.getSize() > maxSizeBytes) {
            throw new FileUploadValidationException("Uploaded " + filePurposeLabel + " exceeds the maximum allowed size");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new FileUploadValidationException("Invalid content type for " + filePurposeLabel);
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (extension == null || !allowedExtensions.contains(extension)) {
            throw new FileUploadValidationException("Invalid file extension for " + filePurposeLabel);
        }
    }

    private String getSafeExtension(String originalFilename, Set<String> allowedExtensions, String filePurposeLabel) {
        String extension = getFileExtension(originalFilename);
        if (extension == null || !allowedExtensions.contains(extension)) {
            throw new FileUploadValidationException("Invalid file extension for " + filePurposeLabel);
        }
        return extension;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return null;
        }

        return filename.substring(lastDot).toLowerCase(Locale.ROOT);
    }

    private String storeFile(MultipartFile file,
                             String directory,
                             String urlPrefix,
                             UUID userId,
                             String extension,
                             String filePurposeLabel) {
        try {
            Path baseDir = Path.of(directory).toAbsolutePath().normalize();
            Files.createDirectories(baseDir);

            String generatedFileName = userId + "_" + UUID.randomUUID() + extension;
            Path destinationPath = baseDir.resolve(generatedFileName).normalize();

            if (!destinationPath.startsWith(baseDir)) {
                throw new FileStorageException("Invalid destination path for " + filePurposeLabel, null);
            }

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return buildPublicUrl(urlPrefix, generatedFileName);
        } catch (IOException exception) {
            throw new FileStorageException("Failed to store " + filePurposeLabel, exception);
        }
    }

    private String buildPublicUrl(String urlPrefix, String fileName) {
        if (urlPrefix.endsWith("/")) {
            return urlPrefix + fileName;
        }
        return urlPrefix + "/" + fileName;
    }
}

