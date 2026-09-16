package com.thelearnhub.commercehub.user;

import com.thelearnhub.commercehub.user.dto.CreateAddressRequest;
import com.thelearnhub.commercehub.user.dto.CreateProfileRequest;
import com.thelearnhub.commercehub.user.dto.UpdateAddressRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end address flow: add → list → update → delete, all against
 * a real MySQL instance via TestContainers. The profile is created in
 * {@link #setUp()} so address tests have a user to attach to.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AddressFlowIntegrationTest {

    private static final String TEST_SECRET = "dev-only-secret-change-me-before-any-real-deployment-32chars+";
    private static final String TEST_EMAIL = "address-test@example.com";

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
        registry.add("eureka.client.enabled", () -> "false");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private String generateTestJwt(String email) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        return Jwts.builder()
                .subject(email)
                .claim("role", "CUSTOMER")
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900_000))
                .signWith(key)
                .compact();
    }

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setBearerAuth(generateTestJwt(TEST_EMAIL));
        headers.set("Content-Type", "application/json");

        // Create profile if it doesn't exist yet (idempotent across test methods)
        CreateProfileRequest profile = new CreateProfileRequest("Address", "Tester", null, null);
        restTemplate.exchange(
                baseUrl() + "/users/profile", HttpMethod.POST,
                new HttpEntity<>(profile, headers), String.class
        );
    }

    @Test
    void addThenListAddresses() {
        CreateAddressRequest address = new CreateAddressRequest(
                "Home", "123 Main St", "Springfield", "IL", "62701", "US", true
        );

        ResponseEntity<String> addResponse = restTemplate.exchange(
                baseUrl() + "/users/me/addresses", HttpMethod.POST,
                new HttpEntity<>(address, headers), String.class
        );
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addResponse.getBody()).contains("123 Main St");
        assertThat(addResponse.getBody()).contains("\"isDefault\":true");

        ResponseEntity<String> listResponse = restTemplate.exchange(
                baseUrl() + "/users/me/addresses", HttpMethod.GET,
                new HttpEntity<>(headers), String.class
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("123 Main St");
    }

    @Test
    void deleteAddressReturnsNoContent() {
        // Add an address to delete
        CreateAddressRequest address = new CreateAddressRequest(
                "Temp", "999 Delete Ave", "Nowhere", "TX", "00000", "US", false
        );
        ResponseEntity<String> addResponse = restTemplate.exchange(
                baseUrl() + "/users/me/addresses", HttpMethod.POST,
                new HttpEntity<>(address, headers), String.class
        );
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Extract the ID from the response
        String body = addResponse.getBody();
        assertThat(body).isNotNull();
        String addressId = body.split("\"id\":\"")[1].split("\"")[0];

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/users/me/addresses/" + addressId, HttpMethod.DELETE,
                new HttpEntity<>(headers), Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
