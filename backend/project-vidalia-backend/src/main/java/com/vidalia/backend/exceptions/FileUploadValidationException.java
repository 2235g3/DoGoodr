package com.vidalia.backend.exceptions;

public class FileUploadValidationException extends RuntimeException {
    public FileUploadValidationException(String message) {
        super(message);
    }
}

