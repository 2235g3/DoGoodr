package com.vidalia.backend.exceptions;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super("You do not have permission to perform this action");
    }

    public ForbiddenRequestException(String message) {
        super(message);
    }
}
