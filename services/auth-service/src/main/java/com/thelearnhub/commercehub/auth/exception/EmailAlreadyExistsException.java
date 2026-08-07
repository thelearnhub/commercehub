package com.thelearnhub.commercehub.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("An account already exists for " + email);
    }
}
