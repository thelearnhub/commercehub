package com.thelearnhub.commercehub.user.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String identifier) {
        super("No profile found for " + identifier);
    }
}
