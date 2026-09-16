package com.thelearnhub.commercehub.user.exception;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException(String email) {
        super("A profile already exists for " + email);
    }
}
