package dev.tanaka.portal_backend.exception;

public class ExistingEmailFoundException extends RuntimeException {

    public ExistingEmailFoundException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
