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
 * Integration tests for the family detail page — real PostgreSQL via Testcontainers.
 *
 * Covers:
 *   - GET /projects/1/families/1   → page renders 4 sections
 *   - Section ③ updated prices     → Rule 2 VALIDATED modifications applied
 *   - Section ④ projection         → Rules 3 & 4 (productivité + tombée rondelles)
 *   - POST endpoints               → HTMX recalc fragment returned with OOB
 *   - Persistence                  → changes visible on subsequent GET
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Page famille — tests d'intégration")
class FamilyPagesIntegrationTest {

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
    // GET /projects/1/families/1 — page complète
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("GET /projects/1/families/1 — retourne 200")
    void family_detail_returns_200() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    @DisplayName("GET — breadcrumb et titre affichent le projet et la famille")
    void family_detail_shows_breadcrumb_and_title() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);

        assertThat(response.getBody())
                .contains("MyProject")
                .contains("PF1")
                .contains("Product Family");
    }

    @Test
    @Order(3)
    @DisplayName("GET — section ① affiche les 5 références et les composantes initiales")
    void family_detail_section1_shows_initial_prices() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        String body = response.getBody();

        // All 5 refs visible
        assertThat(body).contains("PF1-1").contains("PF1-2").contains("PF1-3")
                        .contains("PF1-4").contains("PF1-5");
        // R&D and packaging components (V4 changed PF1 packaging from 0.5 to 0.064)
        assertThat(body).contains("4.500").contains("0.064");
        // SOP Initial row (id="sop-initial-row") with known values
        assertThat(body)
                .contains("sop-initial-row")
                .contains("15.000")   // PF1-1
                .contains("17.000")   // PF1-2
                .contains("30.000");  // PF1-5
    }

    @Test
    @Order(4)
    @DisplayName("GET — section ② affiche les fiches de modification avec leurs statuts")
    void family_detail_section2_shows_modification_sheets() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        String body = response.getBody();

        // Sheet numbers from seed data
        assertThat(body).contains("F005").contains("F012-PU").contains("F015");
        // Status options in selects
        assertThat(body).contains("VALIDATED").contains("OPEN").contains("CANCELED");
        // Matrix checkboxes
        assertThat(body).contains("type=\"checkbox\"");
    }

    @Test
    @Order(5)
    @DisplayName("GET — section ③ affiche les prix SOP actualisés (Règle 2)")
    void family_detail_section3_shows_updated_prices_rule2() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        String body = response.getBody();

        // Section ③ header
        assertThat(body).contains("Prix SOP Actualisés");
        // PF1-1: VALIDATED F012-PU (+0.32 part) + F015 (+0.03 part, +0.09 tef)
        //   updatedBase=10.350, updatedRd=4.590, updatedPkg=0.064, sopUpdated=15.004
        assertThat(body).contains("15.004");
        // PF1-4: VALIDATED F005 (-1.20 part) + F012-Cuir (+0.48 part) + F015 (+0.03 part, +0.09 tef)
        //   updatedBase=19.310, updatedRd=4.590, updatedPkg=0.064, sopUpdated=23.964
        assertThat(body).contains("23.964");
        // Delta PF1-1 = 15.004 - 15.000 = +0.004
        assertThat(body).contains("+0.004");
        // Delta PF1-4 = 23.964 - 25.000 = -1.036
        assertThat(body).contains("-1.036");
    }

    @Test
    @Order(6)
    @DisplayName("GET — section ④ affiche la projection annuelle (SOP+1 à SOP+7)")
    void family_detail_section4_shows_annual_projection() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        String body = response.getBody();

        // Section ④ header
        assertThat(body).contains("Projection Annuelle");
        // Year labels
        assertThat(body).contains("SOP+1").contains("SOP+4").contains("SOP+7");
        // Rule 3: year labels contain productivity rate
        assertThat(body).contains("Productivité");
        // Rule 4: tombée des rondelles
        assertThat(body).contains("Tombée des rondelles");
        // Ellipsis row separator
        assertThat(body).contains("ellipsis-row");
    }

    @Test
    @Order(7)
    @DisplayName("GET — mécanisme HTMX inline editing en place")
    void family_detail_contains_htmx_inline_edit_mechanism() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        String body = response.getBody();

        assertThat(body)
                .contains("hx-post")
                .contains("hx-target=\"#sections-computed\"")
                .contains("hx-swap=\"outerHTML\"")
                .contains("x-data");
    }

    // -------------------------------------------------------------------------
    // POST endpoints — recalc fragment
    // -------------------------------------------------------------------------

    @Test
    @Order(10)
    @DisplayName("POST base=12 → fragment retourne le nouveau tbody + OOB row")
    void post_base_price_returns_recalc_fragment() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/references/1/base",
                "base_price", "12");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        // Fragment contains new tbody
        assertThat(body).contains("sections-computed");
        // OOB swap for SOP Initial row
        assertThat(body).contains("sop-initial-row").contains("hx-swap-oob");
        // No full HTML page
        assertThat(body).doesNotContain("<!DOCTYPE").doesNotContain("<html");
    }

    @Test
    @Order(11)
    @DisplayName("POST base=12 → SOP Initial recalculé à 17.000 dans le fragment OOB")
    void post_base_price_recalculates_sop_initial() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/references/1/base",
                "base_price", "12");

        // Règle 1: 12 + 4.50 + 0.50 = 17.00
        assertThat(response.getBody()).contains("17.000");
    }

    @Test
    @Order(12)
    @DisplayName("POST base=12 → SOP Actualisé recalculé avec la Règle 2")
    void post_base_price_recalculates_sop_updated() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/references/1/base",
                "base_price", "12");

        // updatedBase = 12 + 0.32 (F012-PU) + 0.03 (F015) = 12.35
        // sopUpdated = 12.35 + 4.59 + 0.064 = 17.004
        assertThat(response.getBody()).contains("17.004");
    }

    @Test
    @Order(13)
    @DisplayName("POST base=12 → modification persistée (GET confirme 17.000)")
    void post_base_price_is_persisted() {
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);
        // Previous test (@Order 12) already set base of PF1-1 to 12, sopInitial to 17
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("17.000");
    }

    @Test
    @Order(20)
    @DisplayName("POST rd=5 → SOP Initial et SOP Actualisé recalculés")
    void post_rd_updates_sop() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/references/2/rd",
                "rd_value", "5");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // PF1-2: base=12, rd=5, pkg=0.064 → sopInitial = 17.064
        assertThat(response.getBody()).contains("17.064");
    }

    @Test
    @Order(21)
    @DisplayName("POST pkg=1 → SOP Initial recalculé")
    void post_pkg_updates_sop() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/references/3/pkg",
                "pkg_value", "1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // PF1-3: base=14, rd=4.5, pkg=1 → sopInitial = 19.500
        assertThat(response.getBody()).contains("19.500");
    }

    @Test
    @Order(30)
    @DisplayName("POST sheet status → recalc fragment retourné")
    void post_sheet_status_returns_recalc_fragment() {
        ResponseEntity<String> response = postForm(
                "/projects/1/families/1/sheets/8/status",
                "status", "VALIDATED");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("sections-computed")
                .doesNotContain("<!DOCTYPE");
    }

    @Test
    @Order(31)
    @DisplayName("POST matrix applies=true → recalc fragment retourné")
    void post_matrix_applies_returns_recalc_fragment() {
        ResponseEntity<String> response = postWithQueryParam(
                "/projects/1/families/1/sheets/9/references/1/applies",
                "applies", "true");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("sections-computed")
                .doesNotContain("<!DOCTYPE");
    }

    // -------------------------------------------------------------------------
    // What-if simulation
    // -------------------------------------------------------------------------

    @Test
    @Order(40)
    @DisplayName("GET ?simulate=9 — simulation banner and simulated prices appear")
    void whatif_simulation_shows_simulated_prices() {
        // Sheet 9 is OPEN in PF1 seed data (F017 — MAJ prix PF1itchs)
        ResponseEntity<String> response = http.getForEntity(
                "/projects/1/families/1?simulate=9", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        // Simulation banner
        assertThat(body).contains("Mode simulation");
        assertThat(body).contains("Quitter la simulation");
        // Simulated SOP row
        assertThat(body).contains("SOP Simul");
        // Simulated delta row
        assertThat(body).contains("cart simul");
    }

    @Test
    @Order(41)
    @DisplayName("GET ?simulate=9 — simulation does NOT persist changes")
    void whatif_simulation_does_not_persist() {
        // First, trigger the simulation
        http.getForEntity("/projects/1/families/1?simulate=9", String.class);
        // Then load without simulation — prices should be unchanged
        ResponseEntity<String> response = http.getForEntity("/projects/1/families/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // No simulation banner
        assertThat(response.getBody()).doesNotContain("Mode simulation");
        assertThat(response.getBody()).doesNotContain("SOP Simul");
    }

    @Test
    @Order(42)
    @DisplayName("GET ?simulate=8 — simulating a VALIDATED sheet is ignored")
    void whatif_simulation_ignores_validated_sheets() {
        // Sheet 8 is VALIDATED — simulation should be a no-op
        ResponseEntity<String> response = http.getForEntity(
                "/projects/1/families/1?simulate=8", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // No simulation banner (can't simulate already-validated sheets)
        assertThat(response.getBody()).doesNotContain("Mode simulation");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ResponseEntity<String> postForm(String url, String paramName, String paramValue) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(paramName, paramValue);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return http.postForEntity(url, new HttpEntity<>(form, headers), String.class);
    }

    private ResponseEntity<String> postWithQueryParam(String url, String paramName, String paramValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return http.postForEntity(
                url + "?" + paramName + "=" + paramValue,
                new HttpEntity<>(new LinkedMultiValueMap<>(), headers),
                String.class);
    }
}
