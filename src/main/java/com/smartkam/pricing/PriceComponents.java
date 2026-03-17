package com.smartkam.pricing;

import java.math.BigDecimal;

/**
 * The three immutable components of an initial contract price.
 *
 * @param base           base part price, excluding R&D and packaging
 * @param rdAmortization R&D amortization per piece
 * @param packaging      packaging cost per piece
 */
public record PriceComponents(
        BigDecimal base,
        BigDecimal rdAmortization,
        BigDecimal packaging
) {
    /** Rule 1 convenience method. */
    public BigDecimal sop() {
        return PricingEngine.computeInitialSop(base, rdAmortization, packaging);
    }
}
