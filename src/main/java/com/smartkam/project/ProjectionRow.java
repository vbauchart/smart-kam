package com.smartkam.project;

import java.math.BigDecimal;
import java.util.List;

/**
 * One row in the annual projection section (Rule 3 + Rule 4).
 *
 * @param prices  null signals an ellipsis row (· · ·)
 */
public record ProjectionRow(String label, int year, String event, List<BigDecimal> prices, String rowClass) {

    boolean isEllipsis() { return prices == null; }
}
