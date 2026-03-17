package com.smartkam.project;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for the product-family detail page.
 * Carries all data for the 4 sections (Initial Contract, Modification Sheets,
 * Updated Prices, Annual Projection).
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
        List<ProjectionRow> projection
) {}
