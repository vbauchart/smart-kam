package com.smartkam.project;

import java.math.BigDecimal;
import java.util.List;

/**
 * One row in the monthly timeline section.
 *
 * <p>Month 0 = annual summary (collapsible header); 1-12 = Jan-Dec.</p>
 *
 * @param label       "SOP", "SOP+1", "Jan", "Fév", etc.
 * @param year        calendar year (e.g. 2014)
 * @param month       0 = year summary, 1–12 = Jan–Dec
 * @param event       event description (productivity, tombée, etc.) — empty string if none
 * @param prices      per-ref prices (correct: productivity on net price only)
 * @param naivePrices per-ref prices (naive: productivity on full price) — null if rate == 0
 * @param rowClass    CSS class for styling
 */
public record MonthlyTimelineRow(
        String label,
        int year,
        int month,
        String event,
        List<BigDecimal> prices,
        List<BigDecimal> naivePrices,
        String rowClass
) {
    boolean isSummary() { return month == 0; }
}
