package com.smartkam.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit tests for the 4 pricing rules.
 * Values are taken directly from the Excel reference file (PF1 sheet).
 */
class PricingEngineTest {

    private static final BigDecimal TOLERANCE = new BigDecimal("0.0001");

    // -------------------------------------------------------------------------
    // Rule 1 — Initial SOP Price
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Règle 1 — Prix SOP initial")
    class Rule1 {

        @Test
        @DisplayName("PF1-1 : 10 + 4.5 + 0.5 = 15.00")
        void sop_pf1_1() {
            BigDecimal result = PricingEngine.computeInitialSop(
                    bd("10"), bd("4.5"), bd("0.5"));
            assertThat(result).isEqualByComparingTo(bd("15.00"));
        }

        @Test
        @DisplayName("PF1-4 : 20 + 4.5 + 0.5 = 25.00")
        void sop_pf1_4() {
            BigDecimal result = PricingEngine.computeInitialSop(
                    bd("20"), bd("4.5"), bd("0.5"));
            assertThat(result).isEqualByComparingTo(bd("25.00"));
        }

        @Test
        @DisplayName("SOP = base uniquement si R&D et packaging sont zéro")
        void sop_no_rd_no_packaging() {
            BigDecimal result = PricingEngine.computeInitialSop(
                    bd("15"), BigDecimal.ZERO, BigDecimal.ZERO);
            assertThat(result).isEqualByComparingTo(bd("15"));
        }
    }

    // -------------------------------------------------------------------------
    // Rule 2 — Application matrix & recalculation
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Règle 2 — Matrice d'application et recalcul")
    class Rule2 {

        /**
         * PF1-1 validated modifications:
         *   F012-PU  (VALIDATED, applies=Y) : partPrice=+0.32, tef=0
         *   F015     (VALIDATED, applies=Y) : partPrice=+0.03, tef=+0.09
         *   F005     (VALIDATED, applies=N) : excluded by matrix
         *   F012-Cuir(VALIDATED, applies=N) : excluded by matrix
         * Expected base: 10 + 0.32 + 0.03 = 10.35
         * Expected R&D : 4.5 + 0 + 0.09 = 4.59
         */
        private List<ModificationImpact> pf1_1_impacts() {
            return List.of(
                    // F005 VALIDATED but NOT applies for PF1-1
                    ModificationImpact.of(bd("-1.2"), bd("0"),    bd("0"), false, true),
                    // F012-PU VALIDATED, applies
                    ModificationImpact.of(bd("0.32"), bd("0"),    bd("0"), true,  true),
                    // F012-Cuir VALIDATED but NOT applies for PF1-1
                    ModificationImpact.of(bd("0.48"), bd("0"),    bd("0"), false, true),
                    // F015 VALIDATED, applies
                    ModificationImpact.of(bd("0.03"), bd("0.09"), bd("0"), true,  true),
                    // F017 OPEN — must be ignored
                    ModificationImpact.of(bd("0.477"), bd("0"),   bd("0"), true,  false),
                    // F018 OPEN — must be ignored (not applicable anyway)
                    ModificationImpact.of(bd("0.045"), bd("0.48"),bd("0"), false, false)
            );
        }

        @Test
        @DisplayName("PF1-1 : base mise à jour = 10 + 0.32 + 0.03 = 10.35")
        void updatedBase_pf1_1() {
            BigDecimal result = PricingEngine.computeUpdatedBase(bd("10"), pf1_1_impacts());
            assertThat(result).isEqualByComparingTo(bd("10.35"));
        }

        @Test
        @DisplayName("PF1-1 : R&D mise à jour = 4.5 + 0.09 = 4.59")
        void updatedRd_pf1_1() {
            BigDecimal result = PricingEngine.computeUpdatedRdAmortization(bd("4.5"), pf1_1_impacts());
            assertThat(result).isEqualByComparingTo(bd("4.59"));
        }

        @Test
        @DisplayName("PF1-1 : SOP actualisé = 10.35 + 4.59 + 0.064 = 15.004")
        void updatedSop_pf1_1() {
            BigDecimal result = PricingEngine.computeUpdatedSop(bd("10.35"), bd("4.59"), bd("0.064"));
            assertThat(result).isEqualByComparingTo(bd("15.004"));
        }

        /**
         * PF1-4 validated modifications:
         *   F005     (VALIDATED, applies=Y) : partPrice=-1.2,  tef=0
         *   F012-Cuir(VALIDATED, applies=Y) : partPrice=+0.48, tef=0
         *   F015     (VALIDATED, applies=Y) : partPrice=+0.03, tef=+0.09
         *   F012-PU  (VALIDATED, applies=N) : excluded
         * Expected base: 20 + (-1.2) + 0.48 + 0.03 = 19.31
         * Expected R&D : 4.5 + 0 + 0 + 0.09 = 4.59
         */
        @Test
        @DisplayName("PF1-4 : base mise à jour = 20 - 1.2 + 0.48 + 0.03 = 19.31")
        void updatedBase_pf1_4() {
            List<ModificationImpact> impacts = List.of(
                    ModificationImpact.of(bd("-1.2"),  bd("0"),    bd("0"), true,  true),   // F005
                    ModificationImpact.of(bd("0.32"),  bd("0"),    bd("0"), false, true),   // F012-PU N
                    ModificationImpact.of(bd("0.48"),  bd("0"),    bd("0"), true,  true),   // F012-Cuir
                    ModificationImpact.of(bd("0.03"),  bd("0.09"), bd("0"), true,  true),   // F015
                    ModificationImpact.of(bd("0.477"), bd("0"),    bd("0"), true,  false),  // F017 OPEN
                    ModificationImpact.of(bd("0.045"), bd("0.48"), bd("0"), true,  false)   // F018 OPEN
            );
            BigDecimal result = PricingEngine.computeUpdatedBase(bd("20"), impacts);
            assertThat(result).isEqualByComparingTo(bd("19.31"));
        }

        @Test
        @DisplayName("Les fiches CANCELED n'impactent jamais les prix")
        void canceled_sheets_have_no_effect() {
            List<ModificationImpact> impacts = List.of(
                    // F004 CANCELED, would have applied
                    ModificationImpact.of(bd("1.79"), bd("0.1"), bd("0"), true, false),
                    // F013 CANCELED, would have applied
                    ModificationImpact.of(bd("0"),    bd("0.05"), bd("0"), true, false)
            );
            BigDecimal base = PricingEngine.computeUpdatedBase(bd("10"), impacts);
            BigDecimal rd   = PricingEngine.computeUpdatedRdAmortization(bd("4.5"), impacts);
            assertThat(base).isEqualByComparingTo(bd("10"));
            assertThat(rd).isEqualByComparingTo(bd("4.5"));
        }

        @Test
        @DisplayName("Les fiches OPEN n'impactent jamais les prix")
        void open_sheets_have_no_effect() {
            List<ModificationImpact> impacts = List.of(
                    ModificationImpact.of(bd("0.477"), bd("0"),    bd("0"), true, false),  // F017
                    ModificationImpact.of(bd("0.045"), bd("0.48"), bd("0"), true, false)   // F018
            );
            BigDecimal result = PricingEngine.computeUpdatedBase(bd("10"), impacts);
            assertThat(result).isEqualByComparingTo(bd("10"));
        }

        @Test
        @DisplayName("Une fiche VALIDATED avec N dans la matrice n'est pas comptée")
        void validated_with_N_matrix_excluded() {
            List<ModificationImpact> impacts = List.of(
                    ModificationImpact.of(bd("5.00"), bd("0"), bd("0"), false, true)  // VALIDATED but applies=false
            );
            BigDecimal result = PricingEngine.computeUpdatedBase(bd("10"), impacts);
            assertThat(result).isEqualByComparingTo(bd("10"));
        }
    }

    // -------------------------------------------------------------------------
    // Rule 3 — Productivity
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Règle 3 — Productivité contractuelle")
    class Rule3 {

        /**
         * PF1-1 SOP+1 (2015):
         *   SOP = 15.004, R&D = 4.59, packaging = 0.064, rate = -1%
         *   prixNu = 15.004 - 4.59 - 0.064 = 10.35
         *   result = 10.35 × 0.99 + 4.59 + 0.064 = 10.2465 + 4.654 = 14.9005
         */
        @Test
        @DisplayName("PF1-1 SOP+1 : -1% sur prix nu = 14.9005")
        void productivity_pf1_1_sop_plus_1() {
            BigDecimal result = PricingEngine.applyProductivity(
                    bd("15.004"), bd("4.59"), bd("0.064"), bd("-0.01"));
            assertThat(result).isCloseTo(bd("14.9005"), within(TOLERANCE));
        }

        @Test
        @DisplayName("PF1-4 SOP+1 : -1% sur prix nu = 23.7709")
        void productivity_pf1_4_sop_plus_1() {
            // SOP=23.964, R&D=4.59, pkg=0.064
            // prixNu = 23.964 - 4.59 - 0.064 = 19.31
            // result = 19.31 × 0.99 + 4.59 + 0.064 = 19.1169 + 4.654 = 23.7709
            BigDecimal result = PricingEngine.applyProductivity(
                    bd("23.964"), bd("4.59"), bd("0.064"), bd("-0.01"));
            assertThat(result).isCloseTo(bd("23.7709"), within(TOLERANCE));
        }

        @Test
        @DisplayName("La productivité ne s'applique pas sur les rondelles ni le packaging")
        void productivity_excludes_amortization_and_packaging() {
            // With rate=-1%, if applied on total price: result < (sop * 0.99)
            // Correct: result > (sop * 0.99) because amort+pkg are preserved
            BigDecimal sop = bd("15.004");
            BigDecimal rd  = bd("4.59");
            BigDecimal pkg = bd("0.064");

            BigDecimal correct  = PricingEngine.applyProductivity(sop, rd, pkg, bd("-0.01"));
            BigDecimal naive    = sop.multiply(bd("0.99")); // wrong naive approach

            assertThat(correct).isGreaterThan(naive);
        }
    }

    // -------------------------------------------------------------------------
    // Rule 4 — Tombée des Rondelles
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Règle 4 — Tombée des rondelles")
    class Rule4 {

        /**
         * PF1-1 at SOP+7 (2021):
         *   Price SOP+4 = 14.5962 (no more productivity after year 4)
         *   R&D = 4.59
         *   After tombée: 14.5962 - 4.59 = 10.0062
         */
        @Test
        @DisplayName("PF1-1 SOP+7 : tombée des rondelles — 14.5962 - 4.59 = 10.0062")
        void tombee_pf1_1() {
            BigDecimal result = PricingEngine.applyTombeeDesRondelles(
                    bd("14.5962"), bd("4.59"));
            assertThat(result).isCloseTo(bd("10.0062"), within(TOLERANCE));
        }

        @Test
        @DisplayName("PF1-4 SOP+7 : tombée des rondelles — 23.2031 - 4.59 = 18.6131")
        void tombee_pf1_4() {
            BigDecimal result = PricingEngine.applyTombeeDesRondelles(
                    bd("23.2031"), bd("4.59"));
            assertThat(result).isCloseTo(bd("18.6131"), within(TOLERANCE));
        }

        @Test
        @DisplayName("La tombée est brutale (step-down), pas progressive")
        void tombee_is_step_down() {
            BigDecimal before = bd("14.5962");
            BigDecimal after  = PricingEngine.applyTombeeDesRondelles(before, bd("4.59"));
            // The drop is exactly equal to R&D amortization
            assertThat(before.subtract(after)).isEqualByComparingTo(bd("4.59"));
        }
    }

    // -------------------------------------------------------------------------
    // Full projection — PF1-1 from SOP to SOP+7
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Projection complète PF1-1 : SOP → SOP+7")
    class FullProjection {

        @Test
        @DisplayName("SOP → SOP+4 avec productivité puis SOP+7 tombée des rondelles")
        void full_projection_pf1_1() {
            BigDecimal rd  = bd("4.59");
            BigDecimal pkg = bd("0.064");
            BigDecimal rate = bd("-0.01");

            BigDecimal sop    = bd("15.004");
            BigDecimal sop1   = PricingEngine.applyProductivity(sop,  rd, pkg, rate);
            BigDecimal sop2   = PricingEngine.applyProductivity(sop1, rd, pkg, rate);
            BigDecimal sop3   = PricingEngine.applyProductivity(sop2, rd, pkg, rate);
            BigDecimal sop4   = PricingEngine.applyProductivity(sop3, rd, pkg, rate);
            // No productivity after year 4 — price stays stable until SOP+7
            BigDecimal sop7   = PricingEngine.applyTombeeDesRondelles(sop4, rd);

            // Values from Excel (full precision, no intermediate rounding)
            assertThat(sop1).isCloseTo(bd("14.9005"),   within(TOLERANCE));
            assertThat(sop2).isCloseTo(bd("14.798035"), within(TOLERANCE));
            assertThat(sop3).isCloseTo(bd("14.696595"), within(TOLERANCE));
            assertThat(sop4).isCloseTo(bd("14.596169"), within(TOLERANCE));
            assertThat(sop7).isCloseTo(bd("10.006169"), within(TOLERANCE));
        }
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
