package com.thelearnhub.commercehub.user;

import com.thelearnhub.commercehub.user.dto.CreateProfileRequest;
import com.thelearnhub.commercehub.user.dto.UpdateProfileRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end profile flow: create → get → update → verify, all against
 * a real MySQL instance via TestContainers.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProfileFlowIntegrationTest {

    private static final String TEST_SECRET = "dev-only-secret-change-me-before-any-real-deployment-32chars+";

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("commercehub_user")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("security.jwt.secret", () -> TEST_SECRET);
        // Disable Eureka for tests
        registry.add("eureka.client.enabled", () -> "false");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private String generateTestJwt(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900_000))
                .signWith(key)
                .compact();
    }

    private HttpHeaders authHeaders(String email, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(generateTestJwt(email, role));
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @Test
    void createThenGetThenUpdateProfile() {
        String email = "profile-test@example.com";
        HttpHeaders headers = authHeaders(email, "CUSTOMER");

        // Create profile
        CreateProfileRequest create = new CreateProfileRequest("Jane", "Doe", "+1-555-0100", null);
        ResponseEntity<String> createResponse = restTemplate.exchange(
                baseUrl() + "/users/profile",
                HttpMethod.POST,
                new HttpEntity<>(create, headers),
                String.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).contains("Jane");
        assertThat(createResponse.getBody()).contains(email);

        // Get profile
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl() + "/users/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Jane");

        // Update profile
        UpdateProfileRequest update = new UpdateProfileRequest("Janet", "Doe-Smith", "+1-555-0200", "https://example.com/avatar.jpg");
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl() + "/users/me",
                HttpMethod.PUT,
                new HttpEntity<>(update, headers),
                String.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).contains("Janet");
        assertThat(updateResponse.getBody()).contains("Doe-Smith");
    }

    @Test
    void duplicateProfileCreationIsRejected() {
        String email = "dupe-profile@example.com";
        HttpHeaders headers = authHeaders(email, "CUSTOMER");

        CreateProfileRequest create = new CreateProfileRequest("Alice", "Test", null, null);
        restTemplate.exchange(baseUrl() + "/users/profile", HttpMethod.POST, new HttpEntity<>(create, headers), String.class);

        ResponseEntity<String> second = restTemplate.exchange(
                baseUrl() + "/users/profile",
                HttpMethod.POST,
                new HttpEntity<>(create, headers),
                String.class
        );
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void unauthenticatedRequestIsRejected() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/users/me", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
