package com.smartkam.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for ProjectController — JPA and service are mocked.
 *
 * Verifies HTTP status codes, Thymeleaf rendering, and the HTMX fragment response.
 */
@WebMvcTest(ProjectController.class)
@DisplayName("ProjectController — tests MockMvc")
class ProjectControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ProjectService               projectService;
    @MockBean ProductReferenceRepository   referenceRepository;

    // Shared fixtures, rebuilt before each test
    private Project          myProject;
    private PriceBreakdown   pb1;
    private ProductReference ref1;
    private ProductFamily    family1;

    @BeforeEach
    void setUp() {
        myProject = ProjectServiceTest.project(1L, "MyProject", ProjectStatus.PRODUCTION);
        ReflectionTestUtils.setField(myProject, "sopDate", LocalDate.of(2014, 1, 1));

        pb1   = ProjectServiceTest.priceBreakdown("10.00", "4.50", "0.50", "15.00");
        ref1  = reference(1L, "PF1-1", "Volant PU — Standard", pb1);
        family1 = family(1L, myProject, "PF1", "Product Family 1", List.of(ref1));
        // close the bidirectional reference
        ReflectionTestUtils.setField(ref1, "family", family1);
    }

    // -------------------------------------------------------------------------
    // GET /projects
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /projects — liste des projets")
    class ListProjects {

        @Test
        @DisplayName("Retourne 200 et affiche le nom du projet")
        void returns_200_with_project_name() throws Exception {
            when(projectService.findAll()).thenReturn(List.of(myProject));

            mockMvc.perform(get("/projects"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("MyProject")));
        }

        @Test
        @DisplayName("Affiche le statut du projet sous forme de badge")
        void shows_project_status_badge() throws Exception {
            when(projectService.findAll()).thenReturn(List.of(myProject));

            mockMvc.perform(get("/projects"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("PRODUCTION")));
        }

        @Test
        @DisplayName("Affiche le message vide si aucun projet")
        void empty_list_shows_empty_message() throws Exception {
            when(projectService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/projects"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Aucun projet")));
        }

        @Test
        @DisplayName("Contient un lien vers le détail du projet")
        void contains_link_to_project_detail() throws Exception {
            when(projectService.findAll()).thenReturn(List.of(myProject));

            mockMvc.perform(get("/projects"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/projects/1")));
        }
    }

    // -------------------------------------------------------------------------
    // GET /projects/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /projects/{id} — tableau de prix")
    class DetailProject {

        @Test
        @DisplayName("Retourne 200 et affiche le nom du projet")
        void returns_200_with_project_name() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("MyProject")));
        }

        @Test
        @DisplayName("Affiche le code famille PF1 et la référence PF1-1")
        void shows_family_code_and_reference() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("PF1")))
                    .andExpect(content().string(containsString("PF1-1")));
        }

        @Test
        @DisplayName("Affiche le SOP initial formaté (15.00)")
        void shows_formatted_sop_initial() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("15.00")));
        }

        @Test
        @DisplayName("Affiche les composantes R&D et packaging")
        void shows_rd_and_packaging() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("4.50")))
                    .andExpect(content().string(containsString("0.50")));
        }

        @Test
        @DisplayName("Contient un input HTMX pour la saisie inline du prix de base")
        void contains_htmx_input_for_inline_edit() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("hx-post")))
                    .andExpect(content().string(containsString("hx-target=\"closest tr\"")));
        }

        @Test
        @DisplayName("Contient le breadcrumb de navigation")
        void contains_breadcrumb() throws Exception {
            when(projectService.findById(1L)).thenReturn(myProject);
            when(projectService.findFamiliesWithDetails(1L)).thenReturn(List.of(family1));

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Projets")))
                    .andExpect(content().string(containsString("/projects")));
        }
    }

    // -------------------------------------------------------------------------
    // POST /projects/{id}/references/{refId}/base-price  (HTMX endpoint)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("POST base-price — fragment HTMX")
    class UpdateBasePrice {

        @Test
        @DisplayName("Retourne 200 avec le fragment <tr> contenant le SOP recalculé")
        void returns_fragment_with_updated_sop() throws Exception {
            PriceBreakdown updated = ProjectServiceTest.priceBreakdown(
                    "11.00", "4.50", "0.50", "16.00");
            when(projectService.updateBasePrice(eq(1L), any())).thenReturn(updated);
            when(projectService.findById(1L)).thenReturn(myProject);
            when(referenceRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(ref1));

            mockMvc.perform(post("/projects/1/references/1/base-price")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("base_price", "11.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("16.00")))
                    .andExpect(content().string(containsString("PF1-1")));
        }

        @Test
        @DisplayName("Le SOP retourné est bien base + R&D + packaging (Règle 1)")
        void returned_sop_satisfies_rule1() throws Exception {
            // base=25, rd=4.5, pkg=0.5 → SOP=30
            PriceBreakdown updated = ProjectServiceTest.priceBreakdown(
                    "25.00", "4.50", "0.50", "30.00");
            when(projectService.updateBasePrice(eq(1L), any())).thenReturn(updated);
            when(projectService.findById(1L)).thenReturn(myProject);
            when(referenceRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(ref1));

            mockMvc.perform(post("/projects/1/references/1/base-price")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("base_price", "25.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("30.00")));
        }

        @Test
        @DisplayName("Le fragment retourné ne contient pas l'ancien SOP")
        void old_sop_is_replaced_in_fragment() throws Exception {
            PriceBreakdown updated = ProjectServiceTest.priceBreakdown(
                    "11.00", "4.50", "0.50", "16.00");
            when(projectService.updateBasePrice(eq(1L), any())).thenReturn(updated);
            when(projectService.findById(1L)).thenReturn(myProject);
            when(referenceRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(ref1));

            mockMvc.perform(post("/projects/1/references/1/base-price")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("base_price", "11.00"))
                    .andExpect(status().isOk())
                    // new SOP present
                    .andExpect(content().string(containsString("16.00")))
                    // old SOP not present (was 15.00, now 16.00 — "15.00" ≠ "16.00")
                    .andExpect(content().string(not(containsString("15.00"))));
        }

        @Test
        @DisplayName("Le fragment contient l'URL hx-post pour une future mise à jour")
        void fragment_contains_htmx_post_url() throws Exception {
            PriceBreakdown updated = ProjectServiceTest.priceBreakdown(
                    "11.00", "4.50", "0.50", "16.00");
            when(projectService.updateBasePrice(eq(1L), any())).thenReturn(updated);
            when(projectService.findById(1L)).thenReturn(myProject);
            when(referenceRepository.findByIdWithFamily(1L)).thenReturn(Optional.of(ref1));

            mockMvc.perform(post("/projects/1/references/1/base-price")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("base_price", "11.00"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("hx-post")));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static ProductFamily family(Long id, Project project, String code,
                                        String designation, List<ProductReference> refs) {
        ProductFamily f = new ProductFamily();
        ReflectionTestUtils.setField(f, "id", id);
        ReflectionTestUtils.setField(f, "project", project);
        ReflectionTestUtils.setField(f, "code", code);
        ReflectionTestUtils.setField(f, "designation", designation);
        ReflectionTestUtils.setField(f, "references", refs);
        return f;
    }

    private static ProductReference reference(Long id, String refInternal,
                                               String description, PriceBreakdown pb) {
        ProductReference r = new ProductReference();
        ReflectionTestUtils.setField(r, "id", id);
        ReflectionTestUtils.setField(r, "refInternal", refInternal);
        ReflectionTestUtils.setField(r, "description", description);
        ReflectionTestUtils.setField(r, "priceBreakdown", pb);
        return r;
    }
}
