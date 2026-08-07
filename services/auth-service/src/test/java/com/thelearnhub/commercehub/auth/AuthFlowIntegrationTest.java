package com.thelearnhub.commercehub.auth;

import com.thelearnhub.commercehub.auth.dto.LoginRequest;
import com.thelearnhub.commercehub.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end register -> login -> reject-duplicate-email flow against a
 * real MySQL instance (via Testcontainers) and Flyway-applied schema.
 * Requires Docker Desktop (or an equivalent daemon) to be running locally.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("commercehub_auth")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void registerThenLoginSucceedsAndReturnsTokens() {
        RegisterRequest register = new RegisterRequest("integration-test@example.com", "supersecret1");

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity(baseUrl() + "/auth/register", register, String.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).contains("accessToken");

        LoginRequest login = new LoginRequest("integration-test@example.com", "supersecret1");
        ResponseEntity<String> loginResponse =
                restTemplate.postForEntity(baseUrl() + "/auth/login", login, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).contains("accessToken");
    }

    @Test
    void registeringTheSameEmailTwiceIsRejected() {
        RegisterRequest register = new RegisterRequest("dupe-test@example.com", "supersecret1");

        restTemplate.postForEntity(baseUrl() + "/auth/register", register, String.class);
        ResponseEntity<String> second =
                restTemplate.postForEntity(baseUrl() + "/auth/register", register, String.class);

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void loginWithWrongPasswordIsRejected() {
        RegisterRequest register = new RegisterRequest("wrongpass-test@example.com", "supersecret1");
        restTemplate.postForEntity(baseUrl() + "/auth/register", register, String.class);

        LoginRequest badLogin = new LoginRequest("wrongpass-test@example.com", "not-the-password");
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl() + "/auth/login", badLogin, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
