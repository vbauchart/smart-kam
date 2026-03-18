package com.smartkam.project;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for the product-family detail page.
 * Carries all data for the 4 sections (Initial Contract, Modification Sheets,
 * Updated Prices, Annual Projection).
 *
 * <p>When a What-if simulation is active ({@code simulatedSheetId != null}),
 * the {@code sim*} fields hold the projected values as if the simulated sheet
 * were VALIDATED — without persisting anything.</p>
 */
public record FamilyView(
        ProductFamily family,
        Project project,
        List<ProductReference> refs,     // ordered columns

        // ① Initial Contract — per-ref computed SOP initials
        List<BigDecimal> sopInitials,

        // ② Modification rows
        List<SheetRow> sheets,

        // ③ Updated Prices — per-ref (Rule 2)
        List<BigDecimal> updatedBases,
        List<BigDecimal> updatedRds,
        List<BigDecimal> sopUpdated,
        List<BigDecimal> deltas,

        // ④ Annual Projection — ordered rows (Rule 3 + 4)
        List<ProjectionRow> projection,

        // ⑤ What-if simulation (null when not simulating)
        Long simulatedSheetId,
        List<BigDecimal> simSopUpdated,
        List<BigDecimal> simDeltas,
        List<ProjectionRow> simProjection
) {
    /** Constructor without simulation — backward compatible. */
    public FamilyView(ProductFamily family, Project project, List<ProductReference> refs,
                      List<BigDecimal> sopInitials, List<SheetRow> sheets,
                      List<BigDecimal> updatedBases, List<BigDecimal> updatedRds,
                      List<BigDecimal> sopUpdated, List<BigDecimal> deltas,
                      List<ProjectionRow> projection) {
        this(family, project, refs, sopInitials, sheets,
             updatedBases, updatedRds, sopUpdated, deltas, projection,
             null, null, null, null);
    }

    public boolean isSimulating() {
        return simulatedSheetId != null;
    }
}
