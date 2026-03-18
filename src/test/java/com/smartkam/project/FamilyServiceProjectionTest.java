package com.smartkam.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for FamilyService.buildProjection() — the annual projection engine.
 *
 * Tests all family configurations extracted from the Excel reference file:
 *   - PF1/PF5 : -1%/4 ans, tombée SOP+7 (tombée AFTER productivity)
 *   - PF2/PF6 : -2%/5 ans, tombée SOP+7 (tombée AFTER productivity)
 *   - PF3/PF7 : -2%/5 ans, tombée SOP+3 (tombée DURING productivity)
 *   - PF4     :  0%/0 ans, tombée SOP+5 (no productivity, only tombée)
 */
@DisplayName("FamilyService.buildProjection — projection annuelle")
class FamilyServiceProjectionTest {

    private static final BigDecimal TOLERANCE = new BigDecimal("0.0001");

    // -------------------------------------------------------------------------
    // PF1 style: -1% × 4 years, tombée at SOP+7
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PF1 — productivité -1%/4 ans, tombée SOP+7")
    class PF1Style {

        private final BigDecimal rate = bd("-0.01");
        private final int prodYears = 4;
        private final int rdDropYear = 7;

        // Single reference: SOP=15.004, R&D=4.59, pkg=0.064
        private final List<BigDecimal> rds = List.of(bd("4.59"));
        private final List<BigDecimal> pkgs = List.of(bd("0.064"));
        private final List<BigDecimal> sopUpdated = List.of(bd("15.004"));

        @Test
        @DisplayName("La première ligne est SOP avec les prix inchangés")
        void first_row_is_sop() {
            List<ProjectionRow> rows = buildProjection();
            assertThat(rows.get(0).label()).isEqualTo("SOP");
            assertThat(rows.get(0).year()).isEqualTo(2014);
            assertThat(rows.get(0).prices().get(0)).isEqualByComparingTo(bd("15.004"));
        }

        @Test
        @DisplayName("SOP+1 à SOP+4 appliquent la productivité -1%")
        void sop_plus_1_to_4_apply_productivity() {
            List<ProjectionRow> rows = buildProjection();

            // SOP+1
            ProjectionRow sop1 = findRow(rows, "SOP+1");
            assertThat(sop1.prices().get(0)).isCloseTo(bd("14.9005"), within(TOLERANCE));
            assertThat(sop1.rowClass()).isEqualTo("prod-row");
            assertThat(sop1.event()).contains("Productivité");

            // SOP+4 — 4 iterations of -1%
            ProjectionRow sop4 = findRow(rows, "SOP+4");
            assertThat(sop4.prices().get(0)).isCloseTo(bd("14.5962"), within(TOLERANCE));
        }

        @Test
        @DisplayName("SOP+5 est prix stable (après productivité, avant tombée)")
        void sop_plus_5_is_stable() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop5 = findRow(rows, "SOP+5");
            assertThat(sop5.event()).isEqualTo("Prix stable");
            assertThat(sop5.rowClass()).isEqualTo("stable-row");
            // Price unchanged from SOP+4
            ProjectionRow sop4 = findRow(rows, "SOP+4");
            assertThat(sop5.prices().get(0)).isEqualByComparingTo(sop4.prices().get(0));
        }

        @Test
        @DisplayName("Ellipsis entre SOP+5 et SOP+7")
        void ellipsis_between_stable_and_tombee() {
            List<ProjectionRow> rows = buildProjection();
            boolean hasEllipsis = rows.stream().anyMatch(r -> r.prices() == null);
            assertThat(hasEllipsis).isTrue();
        }

        @Test
        @DisplayName("SOP+7 applique la tombée des rondelles R&D")
        void sop_plus_7_tombee() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop7 = findRow(rows, "SOP+7");
            // 14.5962 - 4.59 = 10.0062
            assertThat(sop7.prices().get(0)).isCloseTo(bd("10.0062"), within(TOLERANCE));
            assertThat(sop7.event()).contains("Tombée");
            assertThat(sop7.rowClass()).isEqualTo("drop-row");
        }

        @Test
        @DisplayName("SOP+8 est prix stable sans rondelles")
        void sop_plus_8_stable_without_rd() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop8 = findRow(rows, "SOP+8");
            assertThat(sop8.event()).contains("sans rondelles");
            // Same price as SOP+7
            ProjectionRow sop7 = findRow(rows, "SOP+7");
            assertThat(sop8.prices().get(0)).isEqualByComparingTo(sop7.prices().get(0));
        }

        @Test
        @DisplayName("La projection produit le bon nombre de lignes visibles")
        void correct_row_count() {
            List<ProjectionRow> rows = buildProjection();
            // SOP, SOP+1..+4, SOP+5(stable), ellipsis, SOP+7(tombée), SOP+8(stable)
            assertThat(rows).hasSize(9);
        }

        private List<ProjectionRow> buildProjection() {
            return FamilyService.buildProjection(rds, pkgs, sopUpdated, 2014, rate, prodYears, rdDropYear);
        }
    }

    // -------------------------------------------------------------------------
    // PF3/PF7 style: -2% × 5 years, tombée at SOP+3 (tombée DURING productivity)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PF3/PF7 — productivité -2%/5 ans, tombée SOP+3 (pendant productivité)")
    class PF3Style {

        private final BigDecimal rate = bd("-0.02");
        private final int prodYears = 5;
        private final int rdDropYear = 3;

        // Single reference: SOP=20, R&D=3, pkg=0.1
        private final List<BigDecimal> rds = List.of(bd("3"));
        private final List<BigDecimal> pkgs = List.of(bd("0.1"));
        private final List<BigDecimal> sopUpdated = List.of(bd("20"));

        @Test
        @DisplayName("SOP+1 et SOP+2 appliquent productivité sur prix nu (avec R&D)")
        void sop_plus_1_and_2_productivity_with_rd() {
            List<ProjectionRow> rows = buildProjection();
            // SOP+1: prixNu = 20 - 3 - 0.1 = 16.9, adjusted = 16.9 * 0.98 = 16.562
            //        result = 16.562 + 3 + 0.1 = 19.662
            ProjectionRow sop1 = findRow(rows, "SOP+1");
            assertThat(sop1.prices().get(0)).isCloseTo(bd("19.662"), within(TOLERANCE));
            assertThat(sop1.rowClass()).isEqualTo("prod-row");
        }

        @Test
        @DisplayName("SOP+3 applique tombée PUIS productivité (R&D tombe à zéro)")
        void sop_plus_3_tombee_then_productivity() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop3 = findRow(rows, "SOP+3");

            // SOP+2: prixNu = 19.662 - 3 - 0.1 = 16.562, adjusted = 16.562 * 0.98 = 16.23076
            //        SOP+2 = 16.23076 + 3 + 0.1 = 19.33076
            // SOP+3 tombée first: 19.33076 - 3 = 16.33076, R&D becomes 0
            // SOP+3 productivity: prixNu = 16.33076 - 0 - 0.1 = 16.23076
            //        adjusted = 16.23076 * 0.98 = 15.906145
            //        result = 15.906145 + 0 + 0.1 = 16.006145
            assertThat(sop3.prices().get(0)).isCloseTo(bd("16.006145"), within(TOLERANCE));
            assertThat(sop3.event()).contains("Tombée").contains("Productivité");
        }

        @Test
        @DisplayName("SOP+4 et SOP+5 appliquent productivité sans R&D (déjà tombée)")
        void sop_plus_4_and_5_productivity_without_rd() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop4 = findRow(rows, "SOP+4");

            // SOP+4: prixNu = 16.006145 - 0 - 0.1 = 15.906145
            //        adjusted = 15.906145 * 0.98 = 15.588022
            //        result = 15.588022 + 0 + 0.1 = 15.688022
            assertThat(sop4.prices().get(0)).isCloseTo(bd("15.688022"), within(TOLERANCE));
            assertThat(sop4.rowClass()).isEqualTo("prod-row");
        }

        @Test
        @DisplayName("SOP+6 est prix stable (après productivité, tombée déjà passée)")
        void sop_plus_6_stable() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop6 = findRow(rows, "SOP+6");
            assertThat(sop6.event()).contains("stable");
        }

        @Test
        @DisplayName("Pas d'ellipsis car tombée est avant la fin de productivité")
        void no_ellipsis() {
            List<ProjectionRow> rows = buildProjection();
            boolean hasEllipsis = rows.stream().anyMatch(r -> r.prices() == null);
            assertThat(hasEllipsis).isFalse();
        }

        private List<ProjectionRow> buildProjection() {
            return FamilyService.buildProjection(rds, pkgs, sopUpdated, 2014, rate, prodYears, rdDropYear);
        }
    }

    // -------------------------------------------------------------------------
    // PF4 style: 0% × 0 years, tombée at SOP+5 (no productivity at all)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PF4 — pas de productivité, tombée SOP+5")
    class PF4Style {

        private final BigDecimal rate = bd("0");
        private final int prodYears = 0;
        private final int rdDropYear = 5;

        // Single reference: SOP=18, R&D=2, pkg=0.5
        private final List<BigDecimal> rds = List.of(bd("2"));
        private final List<BigDecimal> pkgs = List.of(bd("0.5"));
        private final List<BigDecimal> sopUpdated = List.of(bd("18"));

        @Test
        @DisplayName("SOP+1 est prix stable (pas de productivité)")
        void sop_plus_1_stable() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop1 = findRow(rows, "SOP+1");
            assertThat(sop1.event()).isEqualTo("Prix stable");
            assertThat(sop1.prices().get(0)).isEqualByComparingTo(bd("18"));
        }

        @Test
        @DisplayName("SOP+5 applique la tombée des rondelles")
        void sop_plus_5_tombee() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop5 = findRow(rows, "SOP+5");
            // 18 - 2 = 16
            assertThat(sop5.prices().get(0)).isEqualByComparingTo(bd("16"));
            assertThat(sop5.event()).contains("Tombée");
        }

        @Test
        @DisplayName("SOP+6 est prix stable sans rondelles")
        void sop_plus_6_stable_without_rd() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop6 = findRow(rows, "SOP+6");
            assertThat(sop6.event()).contains("sans rondelles");
            assertThat(sop6.prices().get(0)).isEqualByComparingTo(bd("16"));
        }

        @Test
        @DisplayName("Ellipsis entre SOP+1 et SOP+5")
        void ellipsis_between_stable_and_tombee() {
            List<ProjectionRow> rows = buildProjection();
            boolean hasEllipsis = rows.stream().anyMatch(r -> r.prices() == null);
            assertThat(hasEllipsis).isTrue();
        }

        private List<ProjectionRow> buildProjection() {
            return FamilyService.buildProjection(rds, pkgs, sopUpdated, 2014, rate, prodYears, rdDropYear);
        }
    }

    // -------------------------------------------------------------------------
    // PF2/PF6 style: -2% × 5 years, tombée at SOP+7
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PF2/PF6 — productivité -2%/5 ans, tombée SOP+7")
    class PF2Style {

        private final BigDecimal rate = bd("-0.02");
        private final int prodYears = 5;
        private final int rdDropYear = 7;

        // Single reference: SOP=25, R&D=5, pkg=0.2
        private final List<BigDecimal> rds = List.of(bd("5"));
        private final List<BigDecimal> pkgs = List.of(bd("0.2"));
        private final List<BigDecimal> sopUpdated = List.of(bd("25"));

        @Test
        @DisplayName("5 ans de productivité puis tombée à SOP+7")
        void five_years_productivity_then_tombee() {
            List<ProjectionRow> rows = buildProjection();

            // SOP+1: prixNu=25-5-0.2=19.8, adj=19.8*0.98=19.404, result=19.404+5+0.2=24.604
            ProjectionRow sop1 = findRow(rows, "SOP+1");
            assertThat(sop1.prices().get(0)).isCloseTo(bd("24.604"), within(TOLERANCE));

            // SOP+5 still has productivity
            ProjectionRow sop5 = findRow(rows, "SOP+5");
            assertThat(sop5.rowClass()).isEqualTo("prod-row");

            // SOP+7 tombée
            ProjectionRow sop7 = findRow(rows, "SOP+7");
            assertThat(sop7.event()).contains("Tombée");
        }

        @Test
        @DisplayName("SOP+6 est prix stable entre productivité et tombée")
        void sop_plus_6_stable() {
            List<ProjectionRow> rows = buildProjection();
            ProjectionRow sop6 = findRow(rows, "SOP+6");
            assertThat(sop6.event()).isEqualTo("Prix stable");
        }

        private List<ProjectionRow> buildProjection() {
            return FamilyService.buildProjection(rds, pkgs, sopUpdated, 2014, rate, prodYears, rdDropYear);
        }
    }

    // -------------------------------------------------------------------------
    // Multi-reference projection
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Multi-référence — projection avec plusieurs colonnes")
    class MultiRef {

        @Test
        @DisplayName("Chaque référence évolue indépendamment dans la projection")
        void each_ref_evolves_independently() {
            List<BigDecimal> rds = List.of(bd("4.59"), bd("4.59"));
            List<BigDecimal> pkgs = List.of(bd("0.064"), bd("0.064"));
            List<BigDecimal> sops = List.of(bd("15.004"), bd("23.964"));

            List<ProjectionRow> rows = FamilyService.buildProjection(
                    rds, pkgs, sops, 2014, bd("-0.01"), 4, 7);

            ProjectionRow sop1 = findRow(rows, "SOP+1");
            // ref1: prixNu=10.35, adj=10.2465, result=14.9005
            assertThat(sop1.prices().get(0)).isCloseTo(bd("14.9005"), within(TOLERANCE));
            // ref2: prixNu=19.31, adj=19.1169, result=23.7709
            assertThat(sop1.prices().get(1)).isCloseTo(bd("23.7709"), within(TOLERANCE));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static ProjectionRow findRow(List<ProjectionRow> rows, String label) {
        return rows.stream()
                .filter(r -> label.equals(r.label()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Row not found: " + label));
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
