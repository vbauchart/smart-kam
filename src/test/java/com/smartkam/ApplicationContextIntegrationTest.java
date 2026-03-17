package com.smartkam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: validates that the application context starts successfully
 * against a real PostgreSQL database (Testcontainers).
 *
 * Catches: Flyway migration errors, JPA schema validation failures,
 * missing beans, misconfigured properties.
 *
 * Run: mvn test -Dtest=ApplicationContextIntegrationTest
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Démarrage de l'application — intégration")
class ApplicationContextIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("smartkam_test")
            .withUsername("smartkam")
            .withPassword("smartkam");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TestRestTemplate http;

    @Test
    @DisplayName("Le contexte Spring démarre sans erreur")
    void contextLoads() {
        // If we reach this point, the context started successfully:
        // Flyway migrations ran, JPA schema validated, all beans created.
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Les migrations Flyway V1 et V2 sont appliquées")
    void flyway_migrations_applied() {
        Long count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true", Long.class);
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Le seed PF1 est chargé : 1 projet, 5 références, 10 fiches")
    void seed_data_loaded() {
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM project", Long.class))
                .isEqualTo(1L);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM product_reference", Long.class))
                .isEqualTo(5L);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM modification_sheet", Long.class))
                .isEqualTo(10L);
    }

    @Test
    @DisplayName("La page d'accueil répond HTTP 200")
    void home_page_returns_200() {
        ResponseEntity<String> response = http.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Smart KAM");
    }
}
