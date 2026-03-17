package com.smartkam.pricing;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Excel oracle test — verifies the pricing engine against the reference file.
 *
 * The Excel file is the authoritative source of truth.
 * These tests parse the PF1 sheet and compare our engine's output
 * against the values the Excel computed.
 *
 * Run with: mvn test -Dexcel.oracle=true
 * (skipped by default so CI doesn't need the Excel file)
 */
@EnabledIfSystemProperty(named = "excel.oracle", matches = "true")
@DisplayName("Oracle Excel — PF1 validation")
class PricingEngineExcelOracleTest {

    private static final String EXCEL_PATH =
            "_R_V Associates/1. Conception/Outil suivi prix _ modifications.xlsm";
    private static final BigDecimal TOLERANCE = new BigDecimal("0.001");

    // Values extracted from the Excel PF1 sheet (data_only=True)
    // Initial contract prices
    private static final BigDecimal[] INITIAL_BASE = {
            bd("10"), bd("12"), bd("14"), bd("20"), bd("25")
    };
    private static final BigDecimal RD_AMORT_INITIAL  = bd("4.5");
    private static final BigDecimal PACKAGING_INITIAL = bd("0.5");

    // Expected SOP initial (Rule 1)
    private static final BigDecimal[] EXPECTED_SOP_INITIAL = {
            bd("15"), bd("17"), bd("19"), bd("25"), bd("30")
    };

    // Updated values after validated modifications (Rule 2)
    private static final BigDecimal[] EXPECTED_BASE_UPDATED = {
            bd("10.35"), bd("12.35"), bd("14.35"), bd("19.31"), bd("24.31")
    };
    private static final BigDecimal   RD_AMORT_UPDATED  = bd("4.59");
    private static final BigDecimal   PACKAGING_UPDATED = bd("0.064");

    // Expected SOP updated (Rule 2)
    private static final BigDecimal[] EXPECTED_SOP_UPDATED = {
            bd("15.004"), bd("17.004"), bd("19.004"), bd("23.964"), bd("28.964")
    };

    // Expected SOP+1 (Rule 3 — productivity -1%)
    private static final BigDecimal[] EXPECTED_SOP_PLUS_1 = {
            bd("14.9005"), bd("16.8805"), bd("18.8605"), bd("23.7709"), bd("28.7209")
    };

    // Expected SOP+7 tombée des rondelles (Rule 4)
    private static final BigDecimal[] EXPECTED_SOP_PLUS_7 = {
            bd("10.0062"), bd("11.9274"), bd("13.8486"), bd("18.6131"), bd("23.4161")
    };

    @Test
    @DisplayName("Règle 1 — SOP initial des 5 références PF1")
    void rule1_initial_sop_all_references() {
        for (int i = 0; i < 5; i++) {
            BigDecimal result = PricingEngine.computeInitialSop(
                    INITIAL_BASE[i], RD_AMORT_INITIAL, PACKAGING_INITIAL);
            assertThat(result)
                    .as("SOP initial PF1-%d", i + 1)
                    .isCloseTo(EXPECTED_SOP_INITIAL[i], within(TOLERANCE));
        }
    }

    @Test
    @DisplayName("Règle 2 — Base actualisée PF1-1 après modifications validées")
    void rule2_updated_base_pf1_1() {
        // Only VALIDATED modifications that apply to PF1-1
        List<ModificationImpact> impacts = List.of(
                ModificationImpact.of(bd("0.32"),  bd("0"),    true,  true),  // F012-PU
                ModificationImpact.of(bd("0.03"),  bd("0.09"), true,  true),  // F015
                ModificationImpact.of(bd("-1.2"),  bd("0"),    false, true),  // F005 N
                ModificationImpact.of(bd("0.48"),  bd("0"),    false, true),  // F012-Cuir N
                ModificationImpact.of(bd("0.477"), bd("0"),    false, false), // F017 OPEN
                ModificationImpact.of(bd("1.79"),  bd("0.1"),  false, false)  // F004 CANCELED
        );

        BigDecimal base = PricingEngine.computeUpdatedBase(bd("10"), impacts);
        BigDecimal rd   = PricingEngine.computeUpdatedRdAmortization(bd("4.5"), impacts);
        BigDecimal sop  = PricingEngine.computeUpdatedSop(base, rd, PACKAGING_UPDATED);

        assertThat(base).as("base PF1-1").isCloseTo(EXPECTED_BASE_UPDATED[0], within(TOLERANCE));
        assertThat(rd)  .as("R&D PF1-1") .isCloseTo(RD_AMORT_UPDATED,         within(TOLERANCE));
        assertThat(sop) .as("SOP PF1-1") .isCloseTo(EXPECTED_SOP_UPDATED[0],  within(TOLERANCE));
    }

    @Test
    @DisplayName("Règle 3 — Projection SOP+1 (-1%) sur les 5 références PF1")
    void rule3_productivity_sop_plus_1_all_references() {
        BigDecimal rate = bd("-0.01");
        for (int i = 0; i < 5; i++) {
            BigDecimal result = PricingEngine.applyProductivity(
                    EXPECTED_SOP_UPDATED[i], RD_AMORT_UPDATED, PACKAGING_UPDATED, rate);
            assertThat(result)
                    .as("SOP+1 PF1-%d", i + 1)
                    .isCloseTo(EXPECTED_SOP_PLUS_1[i], within(TOLERANCE));
        }
    }

    @Test
    @DisplayName("Règle 4 — Tombée des rondelles à SOP+7 sur les 5 références PF1")
    void rule4_tombee_des_rondelles_sop_plus_7_all_references() {
        // SOP+4 values (after 4 years of -1% productivity)
        BigDecimal[] sop4 = {
                bd("14.5962"), bd("16.5174"), bd("18.4386"), bd("23.2031"), bd("28.0061")
        };
        for (int i = 0; i < 5; i++) {
            BigDecimal result = PricingEngine.applyTombeeDesRondelles(sop4[i], RD_AMORT_UPDATED);
            assertThat(result)
                    .as("SOP+7 tombée PF1-%d", i + 1)
                    .isCloseTo(EXPECTED_SOP_PLUS_7[i], within(TOLERANCE));
        }
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
