package com.smartkam.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Pure pricing engine implementing the 4 business rules extracted from
 * the Excel reference file (PF1 sheet, "Outil suivi prix & modifications.xlsm").
 *
 * All methods are stateless — no Spring beans, no JPA, no I/O.
 * Results are verified by PricingEngineExcelOracleTest against the Excel file.
 */
public final class PricingEngine {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private PricingEngine() {}

    // -------------------------------------------------------------------------
    // Rule 1 — Initial SOP Price
    // Excel: F8 = F6 + F7_rd + F7_pkg  (base + R&D + packaging)
    // -------------------------------------------------------------------------

    /**
     * Computes the initial SOP price from the three contract components.
     *
     * <pre>Prix SOP initial = base + amortissement R&D + packaging</pre>
     */
    public static BigDecimal computeInitialSop(BigDecimal base,
                                               BigDecimal rdAmortization,
                                               BigDecimal packaging) {
        return base.add(rdAmortization).add(packaging);
    }

    // -------------------------------------------------------------------------
    // Rule 2 — Application Matrix & Recalculation (the core of the product)
    // Excel: F31 = F6 + SUMIFS($L$14:$L$28, $B$14:$B$28,"Validated", F14:F28,"Y")
    // -------------------------------------------------------------------------

    /**
     * Computes the updated base price after applying validated modifications.
     *
     * <p>Only modifications where {@code validated=true} AND {@code applies=true}
     * (i.e. "Y" in the application matrix) contribute to the sum.
     * Open and Canceled sheets are completely ignored.</p>
     */
    public static BigDecimal computeUpdatedBase(BigDecimal initialBase,
                                                List<ModificationImpact> impacts) {
        BigDecimal delta = impacts.stream()
                .filter(i -> i.validated() && i.applies())
                .map(ModificationImpact::partPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return initialBase.add(delta);
    }

    /**
     * Computes the updated R&D amortization after applying validated modifications.
     * TEF (tooling amortization) follows the same SUMIFS logic as part price.
     */
    public static BigDecimal computeUpdatedRdAmortization(BigDecimal initialRd,
                                                           List<ModificationImpact> impacts) {
        BigDecimal delta = impacts.stream()
                .filter(i -> i.validated() && i.applies())
                .map(ModificationImpact::tefAmortization)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return initialRd.add(delta);
    }

    /** Assembles the updated SOP from its three updated components. */
    public static BigDecimal computeUpdatedSop(BigDecimal updatedBase,
                                               BigDecimal updatedRd,
                                               BigDecimal packaging) {
        return updatedBase.add(updatedRd).add(packaging);
    }

    // -------------------------------------------------------------------------
    // Rule 3 — Contractual Productivity
    // Excel: F56 = ((F54 - SUM(F$32:F$33)) * (1 + $E56)) + SUM(F$32:F$33)
    //   where F54 = SOP price, F32 = R&D amort, F33 = packaging, E56 = rate
    //
    // KEY BUSINESS RULE: productivity applies ONLY on the net part price
    // (SOP minus amortization minus packaging). R&D rondelles and packaging
    // are excluded — the client cannot claim productivity on amortization.
    // -------------------------------------------------------------------------

    /**
     * Applies contractual productivity on the net part price only.
     *
     * <pre>
     * prix_nu = SOP - R&D - packaging
     * result  = (prix_nu × (1 + rate)) + R&D + packaging
     * </pre>
     *
     * @param sop            current SOP price (may already include past productivity)
     * @param rdAmortization current R&D amortization component
     * @param packaging      packaging component (unchanged by productivity)
     * @param rate           productivity rate, negative for a decrease (e.g. -0.01 for -1%)
     */
    public static BigDecimal applyProductivity(BigDecimal sop,
                                               BigDecimal rdAmortization,
                                               BigDecimal packaging,
                                               BigDecimal rate) {
        BigDecimal prixNu = sop.subtract(rdAmortization).subtract(packaging);
        BigDecimal adjustedPrixNu = prixNu.multiply(BigDecimal.ONE.add(rate));
        // No rounding here — preserve full precision for multi-year projections
        return adjustedPrixNu.add(rdAmortization).add(packaging);
    }

    // -------------------------------------------------------------------------
    // Rule 4 — Tombée des Rondelles
    // Excel: F134 = F132 - F32  (SOP+7 price - R&D amortization)
    //   Label D134: "Tombée des rondelles études LOI/F4"
    //
    // At the contractual end-of-amortization date, the R&D component drops
    // entirely. This is a one-time step-down, not a gradual decrease.
    // -------------------------------------------------------------------------

    /**
     * Applies the "tombée des rondelles" — R&D amortization drops to zero.
     *
     * <pre>prix_post_tombée = SOP_courant - R&D_amortization</pre>
     *
     * @param currentSop     SOP price just before the amortization end date
     * @param rdAmortization the R&D amortization component to remove
     */
    public static BigDecimal applyTombeeDesRondelles(BigDecimal currentSop,
                                                      BigDecimal rdAmortization) {
        return currentSop.subtract(rdAmortization);
    }
}
