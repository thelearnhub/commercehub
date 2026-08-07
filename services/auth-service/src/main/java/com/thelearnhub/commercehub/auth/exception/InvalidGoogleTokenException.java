package com.thelearnhub.commercehub.auth.exception;

public class InvalidGoogleTokenException extends RuntimeException {
    public InvalidGoogleTokenException() {
        super("Google ID token is missing, expired, or was not issued for this app");
    }
}
