package com.vidalia.backend.exceptions;


public class IncorrectOldPasswordException extends RuntimeException {
    public IncorrectOldPasswordException() {
        super("Old password does not match our records");
    }

    public IncorrectOldPasswordException(String message) {
        super(message);
    }
}
