package com.thelearnhub.commercehub.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is missing, expired, or invalid");
    }
}
