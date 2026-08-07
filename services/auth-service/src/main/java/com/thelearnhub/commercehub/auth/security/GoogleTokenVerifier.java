package com.thelearnhub.commercehub.auth.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thelearnhub.commercehub.auth.exception.InvalidGoogleTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Verifies Google Sign-In ID tokens via Google's tokeninfo endpoint.
 *
 * This is Google's own documented lightweight verification path — Google
 * validates the signature, expiry, and issuer server-side and just hands
 * back the decoded claims. It's simple and correct, but rate-limited, so
 * it's a Phase 1 choice: at real production scale you'd instead verify the
 * JWT locally against Google's published JWKS (cached, no per-request call
 * to Google) — worth revisiting alongside the rest of the OAuth2/OIDC work
 * in Phase 3 of the master plan.
 */
@Service
public class GoogleTokenVerifier {

    private final RestClient restClient;
    private final String expectedAudience;

    public GoogleTokenVerifier(@Value("${security.google.client-id:}") String expectedAudience) {
        this.restClient = RestClient.create("https://oauth2.googleapis.com");
        this.expectedAudience = expectedAudience;
    }

    public GoogleUserInfo verify(String idToken) {
        if (expectedAudience.isBlank()) {
            throw new IllegalStateException(
                    "security.google.client-id is not configured — set the GOOGLE_CLIENT_ID env var");
        }

        TokenInfoResponse response;
        try {
            response = restClient.get()
                    .uri("/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(TokenInfoResponse.class);
        } catch (RestClientException e) {
            throw new InvalidGoogleTokenException();
        }

        if (response == null
                || !expectedAudience.equals(response.aud())
                || !"true".equals(response.emailVerified())
                || response.email() == null) {
            throw new InvalidGoogleTokenException();
        }

        return new GoogleUserInfo(response.email());
    }

    public record GoogleUserInfo(String email) {
    }

    private record TokenInfoResponse(
            String aud,
            String email,
            @JsonProperty("email_verified") String emailVerified
    ) {
    }
}
