package com.thelearnhub.commercehub.shipping;

import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import com.thelearnhub.commercehub.shipping.dto.CreateShipmentRequest;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.UpdateStatusRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.File;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShippingFlowIntegrationTest {

    private static final String TEST_SECRET = "dev-only-secret-change-me-before-any-real-deployment-32chars+";

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("commercehub_shipping")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
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

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private String generateJwt(String email, String role) {
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

    private HttpHeaders createHeaders(String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(generateJwt("testuser@example.com", role));
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @Test
    void endToEndShippingFlow() {
        HttpHeaders userHeaders = createHeaders("CUSTOMER");
        HttpHeaders carrierHeaders = createHeaders("CARRIER");

        // 1. Create shipping label
        CreateShipmentRequest createRequest = new CreateShipmentRequest(
                "ORD-99901",
                "FEDEX",
                "Alice Smith",
                "742 Evergreen Terrace, Springfield",
                Instant.now().plusSeconds(172800)
        );

        ResponseEntity<ShipmentResponse> createResponse = restTemplate.exchange(
                baseUrl() + "/shipping/shipments",
                HttpMethod.POST,
                new HttpEntity<>(createRequest, userHeaders),
                ShipmentResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ShipmentResponse createdShipment = createResponse.getBody();
        assertThat(createdShipment).isNotNull();
        assertThat(createdShipment.orderId()).isEqualTo("ORD-99901");
        assertThat(createdShipment.status()).isEqualTo(ShipmentState.LABEL_CREATED);
        assertThat(createdShipment.trackingNumber()).startsWith("TRK-");
        assertThat(createdShipment.statusHistory()).hasSize(1);

        String trackingNumber = createdShipment.trackingNumber();
        Long shipmentId = createdShipment.id();

        // 2. Get shipment by order ID
        ResponseEntity<ShipmentResponse> getByOrderResponse = restTemplate.exchange(
                baseUrl() + "/shipping/shipments/order/ORD-99901",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                ShipmentResponse.class
        );
        assertThat(getByOrderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getByOrderResponse.getBody().trackingNumber()).isEqualTo(trackingNumber);

        // 3. Track shipment publicly (no auth header needed)
        ResponseEntity<ShipmentResponse> trackResponse = restTemplate.exchange(
                baseUrl() + "/shipping/track/" + trackingNumber,
                HttpMethod.GET,
                null,
                ShipmentResponse.class
        );
        assertThat(trackResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(trackResponse.getBody().status()).isEqualTo(ShipmentState.LABEL_CREATED);

        // 4. Update status with CARRIER role: LABEL_CREATED -> IN_TRANSIT
        UpdateStatusRequest statusRequest1 = new UpdateStatusRequest(ShipmentState.IN_TRANSIT, "Picked up by FedEx courier");
        ResponseEntity<ShipmentResponse> updateResponse1 = restTemplate.exchange(
                baseUrl() + "/shipping/shipments/" + shipmentId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest1, carrierHeaders),
                ShipmentResponse.class
        );
        assertThat(updateResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse1.getBody().status()).isEqualTo(ShipmentState.IN_TRANSIT);
        assertThat(updateResponse1.getBody().statusHistory()).hasSize(2);

        // 5. Attempt invalid transition: IN_TRANSIT -> DELIVERED (Must go through OUT_FOR_DELIVERY)
        UpdateStatusRequest invalidStatusRequest = new UpdateStatusRequest(ShipmentState.DELIVERED, "Direct delivery attempt");
        ResponseEntity<String> invalidUpdateResponse = restTemplate.exchange(
                baseUrl() + "/shipping/shipments/" + shipmentId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(invalidStatusRequest, carrierHeaders),
                String.class
        );
        assertThat(invalidUpdateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // 6. Attempt status update with CUSTOMER role (should be forbidden)
        UpdateStatusRequest statusRequest2 = new UpdateStatusRequest(ShipmentState.OUT_FOR_DELIVERY, "Customer update");
        ResponseEntity<String> forbiddenResponse = restTemplate.exchange(
                baseUrl() + "/shipping/shipments/" + shipmentId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest2, userHeaders),
                String.class
        );
        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
