package com.smartkam.pricing;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Excel oracle test — verifies the pricing engine against the reference file.
 *
 * <p>The Excel file {@code Outil suivi prix _ modifications.xlsm} is the
 * authoritative source of truth. This test <b>parses the PF1 sheet with
 * Apache POI</b>, extracts input values AND expected results, then feeds
 * the inputs to {@link PricingEngine} and compares the engine's output
 * against the Excel's computed values.</p>
 *
 * <p>If a future Excel update changes a value, this test will catch the
 * discrepancy automatically — no hardcoded constants to maintain.</p>
 *
 * Run with: {@code mvn test -Dexcel.oracle=true}
 * (skipped by default so CI doesn't need the Excel file)
 */
@EnabledIfSystemProperty(named = "excel.oracle", matches = "true")
@DisplayName("Oracle Excel — PF1 validation")
class PricingEngineExcelOracleTest {

    private static final String EXCEL_PATH =
            "_R_V Associates/1. Conception/Outil suivi prix _ modifications.xlsm";
    private static final BigDecimal TOLERANCE = new BigDecimal("0.001");

    // -------------------------------------------------------------------------
    // PF1 sheet layout (1-indexed rows, columns F-J = refs PF1-1..PF1-5)
    // -------------------------------------------------------------------------
    private static final int REF_COUNT = 5;
    private static final int FIRST_REF_COL = 5; // column F (0-indexed)

    // Section ① — Initial contract
    private static final int ROW_BASE      = 6;
    private static final int ROW_RD        = 7;
    private static final int ROW_PKG       = 8;
    private static final int ROW_SOP_INIT  = 9;

    // Section ② — Modifications (rows 14..28)
    private static final int MOD_FIRST_ROW = 14;
    private static final int MOD_LAST_ROW  = 28;
    private static final int COL_STATUS    = 1;  // column B
    private static final int COL_PART      = 11; // column L — Part Price impact
    private static final int COL_TEF       = 12; // column M — TEF amortization impact

    // Section ③ — SOP Updated
    private static final int ROW_UPD_BASE  = 31;
    private static final int ROW_UPD_RD    = 32;
    private static final int ROW_UPD_PKG   = 33;
    private static final int ROW_SOP_UPD   = 34;

    // Section ④ — Projection
    private static final int ROW_SOP_PLUS1 = 56; // first productivity year
    private static final int ROW_PROD_RATE = 56; // column E = rate
    private static final int COL_RATE      = 4;  // column E
    private static final int ROW_SOP4      = 94; // SOP+4 annual summary (MIN row)
    private static final int ROW_SOP7      = 134; // SOP+7 tombée des rondelles

    // -------------------------------------------------------------------------
    // Data extracted from Excel in @BeforeAll
    // -------------------------------------------------------------------------

    // Inputs (Section ①)
    private static BigDecimal[] initialBase;
    private static BigDecimal rdAmortInitial;
    private static BigDecimal packagingInitial;

    // Expected outputs — Section ①
    private static BigDecimal[] expectedSopInitial;

    // Expected outputs — Section ③
    private static BigDecimal[] expectedBaseUpdated;
    private static BigDecimal rdAmortUpdated;
    private static BigDecimal packagingUpdated;
    private static BigDecimal[] expectedSopUpdated;

    // Modification impacts per reference (Section ②)
    private static List<List<ModificationImpact>> impactsPerRef;

    // Expected outputs — Section ④
    private static BigDecimal productivityRate;
    private static BigDecimal[] expectedSopPlus1;
    private static BigDecimal[] expectedSop4;
    private static BigDecimal[] expectedSop7;

    @BeforeAll
    static void parseExcel() throws Exception {
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet pf1 = wb.getSheet("PF1");
            assertThat(pf1).as("PF1 sheet must exist").isNotNull();

            // --- Section ① : Initial contract values ---
            initialBase = readRow(pf1, ROW_BASE);
            BigDecimal[] rdRow = readRow(pf1, ROW_RD);
            BigDecimal[] pkgRow = readRow(pf1, ROW_PKG);
            rdAmortInitial = rdRow[0];     // same for all refs in PF1
            packagingInitial = pkgRow[0];
            expectedSopInitial = readRow(pf1, ROW_SOP_INIT);

            // --- Section ② : Modification matrix + impacts ---
            impactsPerRef = new ArrayList<>();
            for (int r = 0; r < REF_COUNT; r++) {
                impactsPerRef.add(new ArrayList<>());
            }
            for (int rowNum = MOD_FIRST_ROW; rowNum <= MOD_LAST_ROW; rowNum++) {
                Row row = pf1.getRow(rowNum - 1); // POI is 0-indexed
                if (row == null) continue;

                String status = cellString(row, COL_STATUS);
                if (status == null || status.isBlank()) continue;

                boolean validated = "Validated".equalsIgnoreCase(status.trim());
                // Open and Canceled are treated as not-validated
                BigDecimal partPrice = cellBigDecimal(row, COL_PART);
                BigDecimal tef = cellBigDecimal(row, COL_TEF);

                for (int r = 0; r < REF_COUNT; r++) {
                    int col = FIRST_REF_COL + r;
                    boolean applies = "Y".equalsIgnoreCase(cellString(row, col));
                    impactsPerRef.get(r).add(ModificationImpact.of(
                            partPrice, tef, BigDecimal.ZERO, applies, validated));
                }
            }

            // --- Section ③ : Expected SOP Updated ---
            expectedBaseUpdated = readRow(pf1, ROW_UPD_BASE);
            BigDecimal[] updRdRow = readRow(pf1, ROW_UPD_RD);
            BigDecimal[] updPkgRow = readRow(pf1, ROW_UPD_PKG);
            rdAmortUpdated = updRdRow[0];
            packagingUpdated = updPkgRow[0];
            expectedSopUpdated = readRow(pf1, ROW_SOP_UPD);

            // --- Section ④ : Projection ---
            Row rateRow = pf1.getRow(ROW_PROD_RATE - 1);
            productivityRate = cellBigDecimal(rateRow, COL_RATE);

            expectedSopPlus1 = readRow(pf1, ROW_SOP_PLUS1);
            expectedSop4 = readRow(pf1, ROW_SOP4);
            expectedSop7 = readRow(pf1, ROW_SOP7);
        }
    }

    // =========================================================================
    // Rule 1 — SOP Initial = base + R&D + packaging
    // =========================================================================

    @Test
    @DisplayName("Règle 1 — SOP initial des 5 références PF1 (valeurs lues depuis Excel)")
    void rule1_initial_sop_all_references() {
        for (int i = 0; i < REF_COUNT; i++) {
            BigDecimal result = PricingEngine.computeInitialSop(
                    initialBase[i], rdAmortInitial, packagingInitial);
            assertThat(result)
                    .as("SOP initial PF1-%d", i + 1)
                    .isCloseTo(expectedSopInitial[i], within(TOLERANCE));
        }
    }

    // =========================================================================
    // Rule 2 — Updated SOP via SUMIFS of VALIDATED modifications
    // =========================================================================

    @Test
    @DisplayName("Règle 2 — Base, R&D et SOP actualisés pour les 5 références PF1 (matrice lue depuis Excel)")
    void rule2_updated_prices_all_references() {
        for (int i = 0; i < REF_COUNT; i++) {
            List<ModificationImpact> impacts = impactsPerRef.get(i);

            BigDecimal base = PricingEngine.computeUpdatedBase(initialBase[i], impacts);
            BigDecimal rd   = PricingEngine.computeUpdatedRdAmortization(rdAmortInitial, impacts);
            BigDecimal sop  = PricingEngine.computeUpdatedSop(base, rd, packagingUpdated);

            assertThat(base)
                    .as("updated base PF1-%d", i + 1)
                    .isCloseTo(expectedBaseUpdated[i], within(TOLERANCE));
            assertThat(rd)
                    .as("updated R&D PF1-%d", i + 1)
                    .isCloseTo(rdAmortUpdated, within(TOLERANCE));
            assertThat(sop)
                    .as("SOP updated PF1-%d", i + 1)
                    .isCloseTo(expectedSopUpdated[i], within(TOLERANCE));
        }
    }

    // =========================================================================
    // Rule 3 — Contractual productivity (-1%/year on net part price)
    // =========================================================================

    @Test
    @DisplayName("Règle 3 — Projection SOP+1 pour les 5 références PF1 (taux lu depuis Excel)")
    void rule3_productivity_sop_plus_1_all_references() {
        for (int i = 0; i < REF_COUNT; i++) {
            BigDecimal result = PricingEngine.applyProductivity(
                    expectedSopUpdated[i], rdAmortUpdated, packagingUpdated, productivityRate);
            assertThat(result)
                    .as("SOP+1 PF1-%d", i + 1)
                    .isCloseTo(expectedSopPlus1[i], within(TOLERANCE));
        }
    }

    @Test
    @DisplayName("Règle 3 — Projection SOP+4 par application itérative de la productivité")
    void rule3_productivity_sop_plus_4_all_references() {
        for (int i = 0; i < REF_COUNT; i++) {
            BigDecimal current = expectedSopUpdated[i];
            for (int y = 0; y < 4; y++) {
                current = PricingEngine.applyProductivity(
                        current, rdAmortUpdated, packagingUpdated, productivityRate);
            }
            assertThat(current)
                    .as("SOP+4 PF1-%d", i + 1)
                    .isCloseTo(expectedSop4[i], within(TOLERANCE));
        }
    }

    // =========================================================================
    // Rule 4 — Tombée des rondelles (R&D amortization drops to zero at SOP+7)
    // =========================================================================

    @Test
    @DisplayName("Règle 4 — Tombée des rondelles à SOP+7 pour les 5 références PF1")
    void rule4_tombee_des_rondelles_sop_plus_7_all_references() {
        for (int i = 0; i < REF_COUNT; i++) {
            // SOP+4 price stays stable until SOP+7, then R&D drops
            BigDecimal result = PricingEngine.applyTombeeDesRondelles(
                    expectedSop4[i], rdAmortUpdated);
            assertThat(result)
                    .as("SOP+7 tombée PF1-%d", i + 1)
                    .isCloseTo(expectedSop7[i], within(TOLERANCE));
        }
    }

    // =========================================================================
    // Full end-to-end — from initial contract to SOP+7
    // =========================================================================

    @Test
    @DisplayName("Bout en bout — du contrat initial à SOP+7, toutes les règles enchaînées")
    void full_pipeline_from_contract_to_tombee() {
        for (int i = 0; i < REF_COUNT; i++) {
            // Rule 1
            BigDecimal sopInit = PricingEngine.computeInitialSop(
                    initialBase[i], rdAmortInitial, packagingInitial);
            assertThat(sopInit)
                    .as("R1: SOP init PF1-%d", i + 1)
                    .isCloseTo(expectedSopInitial[i], within(TOLERANCE));

            // Rule 2
            List<ModificationImpact> impacts = impactsPerRef.get(i);
            BigDecimal updBase = PricingEngine.computeUpdatedBase(initialBase[i], impacts);
            BigDecimal updRd   = PricingEngine.computeUpdatedRdAmortization(rdAmortInitial, impacts);
            BigDecimal sopUpd  = PricingEngine.computeUpdatedSop(updBase, updRd, packagingUpdated);
            assertThat(sopUpd)
                    .as("R2: SOP updated PF1-%d", i + 1)
                    .isCloseTo(expectedSopUpdated[i], within(TOLERANCE));

            // Rule 3 — 4 years of productivity
            BigDecimal current = sopUpd;
            for (int y = 0; y < 4; y++) {
                current = PricingEngine.applyProductivity(
                        current, updRd, packagingUpdated, productivityRate);
            }
            assertThat(current)
                    .as("R3: SOP+4 PF1-%d", i + 1)
                    .isCloseTo(expectedSop4[i], within(TOLERANCE));

            // Rule 4 — Tombée des rondelles at SOP+7
            BigDecimal tombee = PricingEngine.applyTombeeDesRondelles(current, updRd);
            assertThat(tombee)
                    .as("R4: SOP+7 PF1-%d", i + 1)
                    .isCloseTo(expectedSop7[i], within(TOLERANCE));
        }
    }

    // =========================================================================
    // Helpers — read cells from POI
    // =========================================================================

    /** Reads REF_COUNT numeric values from columns F..J of the given row. */
    private static BigDecimal[] readRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum - 1); // POI is 0-indexed
        assertThat(row).as("Row %d must exist", rowNum).isNotNull();
        BigDecimal[] values = new BigDecimal[REF_COUNT];
        for (int i = 0; i < REF_COUNT; i++) {
            values[i] = cellBigDecimal(row, FIRST_REF_COL + i);
        }
        return values;
    }

    /** Returns the numeric value of a cell as BigDecimal, or ZERO if blank. */
    private static BigDecimal cellBigDecimal(Row row, int col) {
        if (row == null) return BigDecimal.ZERO;
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    /** Returns the string value of a cell, or null if blank. */
    private static String cellString(Row row, int col) {
        if (row == null) return null;
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return null;
    }
}
