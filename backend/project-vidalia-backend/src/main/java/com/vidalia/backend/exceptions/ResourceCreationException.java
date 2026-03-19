package com.vidalia.backend.exceptions;

public class ResourceCreationException extends RuntimeException {
    public ResourceCreationException(String message) {
        super(message);
    }
}
