package com.smartkam.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the project pages — real PostgreSQL via Testcontainers,
 * full Spring Boot context, actual HTTP requests via TestRestTemplate.
 *
 * Covers:
 *   - GET /projects       → liste des projets (seed PF1)
 *   - GET /projects/1     → tableau de prix PF1 avec les 5 références
 *   - POST base-price     → recalcul SOP en temps réel + persistance
 *
 * Ordering: read tests run before the write test to avoid state interference.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pages projet — tests d'intégration")
class ProjectPagesIntegrationTest {

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
    TestRestTemplate http;

    // -------------------------------------------------------------------------
    // GET /projects — liste
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("GET /projects — retourne 200 et affiche MyProject")
    void projects_list_returns_200_and_shows_myproject() {
        ResponseEntity<String> response = http.getForEntity("/projects", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("MyProject")
                .contains("PRODUCTION")
                .contains("Voir les familles");
    }

    @Test
    @Order(2)
    @DisplayName("GET /projects — la card contient un lien vers /projects/1")
    void projects_list_card_links_to_detail() {
        ResponseEntity<String> response = http.getForEntity("/projects", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("/projects/1");
    }

    // -------------------------------------------------------------------------
    // GET /projects/1 — détail et tableau de prix
    // -------------------------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("GET /projects/1 — retourne 200 et affiche les 5 références PF1")
    void project_detail_shows_all_five_references() {
        ResponseEntity<String> response = http.getForEntity("/projects/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("PF1-1")
                .contains("PF1-2")
                .contains("PF1-3")
                .contains("PF1-4")
                .contains("PF1-5");
    }

    @Test
    @Order(4)
    @DisplayName("GET /projects/1 — affiche les SOP corrects selon la Règle 1")
    void project_detail_shows_correct_sop_values() {
        ResponseEntity<String> response = http.getForEntity("/projects/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // PF1-2 : 12 + 4.5 + 0.5 = 17.00  (not PF1-1 to avoid interference with write test)
        // PF1-3 : 14 + 4.5 + 0.5 = 19.00
        // PF1-5 : 25 + 4.5 + 0.5 = 30.00
        assertThat(response.getBody())
                .contains("17.00")
                .contains("19.00")
                .contains("30.00");
    }

    @Test
    @Order(5)
    @DisplayName("GET /projects/1 — affiche les composantes R&D et packaging")
    void project_detail_shows_rd_and_packaging() {
        ResponseEntity<String> response = http.getForEntity("/projects/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Common R&D and packaging across all 5 references
        assertThat(response.getBody())
                .contains("4.50")
                .contains("0.50");
    }

    @Test
    @Order(6)
    @DisplayName("GET /projects/1 — contient le mécanisme HTMX de saisie inline")
    void project_detail_contains_htmx_inline_edit() {
        ResponseEntity<String> response = http.getForEntity("/projects/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("hx-post")
                .contains("hx-target=\"closest tr\"")
                .contains("x-data");
    }

    // -------------------------------------------------------------------------
    // POST base-price — recalcul HTMX + persistance
    // -------------------------------------------------------------------------

    @Test
    @Order(10)
    @DisplayName("POST base-price=11 → SOP recalculé à 16.00 dans le fragment")
    void update_base_price_returns_recalculated_sop_in_fragment() {
        ResponseEntity<String> response = postBasePrice(1L, 1L, "11");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Règle 1 : 11 + 4.50 + 0.50 = 16.00
        assertThat(response.getBody())
                .contains("16.00")
                .contains("PF1-1")
                .contains("11.00");   // new base displayed in the input
    }

    @Test
    @Order(11)
    @DisplayName("POST base-price → la modification est persistée (GET confirme 16.00)")
    void update_base_price_is_persisted() {
        // The previous test (@Order 10) already changed PF1-1 base to 11, SOP to 16
        // This test just verifies the page reflects the persisted value
        ResponseEntity<String> response = http.getForEntity("/projects/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("16.00");
    }

    @Test
    @Order(12)
    @DisplayName("POST base-price → retourne uniquement le fragment <tr>, pas une page HTML complète")
    void update_base_price_returns_fragment_not_full_page() {
        ResponseEntity<String> response = postBasePrice(1L, 2L, "13");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Fragment response must NOT contain a full HTML skeleton
        assertThat(response.getBody())
                .doesNotContain("<html")
                .doesNotContain("<!DOCTYPE")
                .contains("<tr");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private ResponseEntity<String> postBasePrice(Long projectId, Long refId, String basePrice) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("base_price", basePrice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return http.postForEntity(
                "/projects/" + projectId + "/references/" + refId + "/base-price",
                new HttpEntity<>(form, headers),
                String.class);
    }
}
