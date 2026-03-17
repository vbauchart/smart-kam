package com.smartkam.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProjectService — all dependencies mocked.
 *
 * Focus: Règle 1 (SOP = base + R&D + packaging) and basic CRUD delegation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService — tests unitaires")
class ProjectServiceTest {

    @Mock ProjectRepository       projectRepository;
    @Mock ProductFamilyRepository familyRepository;
    @Mock PriceBreakdownRepository priceBreakdownRepository;

    @InjectMocks
    ProjectService service;

    // -------------------------------------------------------------------------
    // findAll / findById
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Lecture des projets")
    class FindProjects {

        @Test
        @DisplayName("findAll délègue au repository et retourne la liste")
        void findAll_delegates_to_repository() {
            Project p = project(1L, "MyProject", ProjectStatus.PRODUCTION);
            when(projectRepository.findAll()).thenReturn(List.of(p));

            assertThat(service.findAll()).containsExactly(p);
        }

        @Test
        @DisplayName("findById retourne le projet quand il existe")
        void findById_returns_project() {
            Project p = project(1L, "MyProject", ProjectStatus.PRODUCTION);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(p));

            assertThat(service.findById(1L)).isSameAs(p);
        }

        @Test
        @DisplayName("findById lève IllegalArgumentException si le projet est absent")
        void findById_throws_when_not_found() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("findFamiliesWithDetails délègue au repository")
        void findFamiliesWithDetails_delegates_to_repository() {
            ProductFamily f = new ProductFamily();
            when(familyRepository.findByProjectIdWithDetails(1L)).thenReturn(List.of(f));

            assertThat(service.findFamiliesWithDetails(1L)).containsExactly(f);
        }
    }

    // -------------------------------------------------------------------------
    // updateBasePrice — Règle 1 : SOP = base + R&D + packaging
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Règle 1 — mise à jour du prix de base et recalcul SOP")
    class UpdateBasePrice {

        @Test
        @DisplayName("PF1-1 : base 10→11, SOP recalculé 15→16 (11 + 4.50 + 0.50)")
        void pf1_1_base_10_to_11_sop_becomes_16() {
            PriceBreakdown pb = priceBreakdown("10.00", "4.50", "0.50", "15.00");
            when(priceBreakdownRepository.findByReferenceId(1L)).thenReturn(Optional.of(pb));
            when(priceBreakdownRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            PriceBreakdown result = service.updateBasePrice(1L, new BigDecimal("11.00"));

            assertThat(result.getBasePrice()).isEqualByComparingTo("11.00");
            assertThat(result.getSopInitial()).isEqualByComparingTo("16.00");
        }

        @Test
        @DisplayName("PF1-5 : base 25→20, SOP recalculé 30→25 (20 + 4.50 + 0.50)")
        void pf1_5_base_25_to_20_sop_becomes_25() {
            PriceBreakdown pb = priceBreakdown("25.00", "4.50", "0.50", "30.00");
            when(priceBreakdownRepository.findByReferenceId(5L)).thenReturn(Optional.of(pb));
            when(priceBreakdownRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            PriceBreakdown result = service.updateBasePrice(5L, new BigDecimal("20.00"));

            assertThat(result.getSopInitial()).isEqualByComparingTo("25.00");
        }

        @Test
        @DisplayName("La formule additive SOP = base + R&D + packaging est respectée")
        void sop_formula_is_additive() {
            // base=100, rd=10, pkg=5 → SOP must be exactly 115
            PriceBreakdown pb = priceBreakdown("50.00", "10.00", "5.00", "65.00");
            when(priceBreakdownRepository.findByReferenceId(1L)).thenReturn(Optional.of(pb));
            when(priceBreakdownRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            PriceBreakdown result = service.updateBasePrice(1L, new BigDecimal("100.00"));

            assertThat(result.getSopInitial()).isEqualByComparingTo("115.00");
        }

        @Test
        @DisplayName("R&D et packaging ne sont pas modifiés lors de la mise à jour")
        void rd_and_packaging_are_unchanged() {
            PriceBreakdown pb = priceBreakdown("10.00", "4.50", "0.50", "15.00");
            when(priceBreakdownRepository.findByReferenceId(1L)).thenReturn(Optional.of(pb));
            when(priceBreakdownRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.updateBasePrice(1L, new BigDecimal("11.00"));

            assertThat(pb.getRdAmortization()).isEqualByComparingTo("4.50");
            assertThat(pb.getPackaging()).isEqualByComparingTo("0.50");
        }

        @Test
        @DisplayName("Le PriceBreakdown mis à jour est sauvegardé en base")
        void price_breakdown_is_saved() {
            PriceBreakdown pb = priceBreakdown("10.00", "4.50", "0.50", "15.00");
            when(priceBreakdownRepository.findByReferenceId(1L)).thenReturn(Optional.of(pb));
            when(priceBreakdownRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.updateBasePrice(1L, new BigDecimal("11.00"));

            verify(priceBreakdownRepository).save(pb);
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si la référence n'a pas de PriceBreakdown")
        void throws_when_price_breakdown_not_found() {
            when(priceBreakdownRepository.findByReferenceId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateBasePrice(99L, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("99");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers — package-accessible so ProjectControllerTest can reuse them
    // -------------------------------------------------------------------------

    static Project project(Long id, String name, ProjectStatus status) {
        Project p = new Project();
        ReflectionTestUtils.setField(p, "id", id);
        ReflectionTestUtils.setField(p, "name", name);
        ReflectionTestUtils.setField(p, "status", status);
        return p;
    }

    static PriceBreakdown priceBreakdown(String base, String rd, String pkg, String sop) {
        PriceBreakdown pb = new PriceBreakdown();
        pb.setBasePrice(new BigDecimal(base));
        ReflectionTestUtils.setField(pb, "rdAmortization", new BigDecimal(rd));
        ReflectionTestUtils.setField(pb, "packaging", new BigDecimal(pkg));
        pb.setSopInitial(new BigDecimal(sop));
        return pb;
    }
}
