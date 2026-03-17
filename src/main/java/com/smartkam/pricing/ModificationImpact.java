package com.smartkam.pricing;

import java.math.BigDecimal;

/**
 * Immutable snapshot of a modification sheet's impact on one product reference.
 *
 * @param partPrice       direct part price delta (EUR/piece)
 * @param tefAmortization tooling amortization delta (EUR/piece)
 * @param packagingImpact packaging cost delta (EUR/piece)
 * @param applies         Y in the application matrix for this reference
 * @param validated       the modification sheet is in VALIDATED status
 */
public record ModificationImpact(
        BigDecimal partPrice,
        BigDecimal tefAmortization,
        BigDecimal packagingImpact,
        boolean applies,
        boolean validated
) {
    public static ModificationImpact of(BigDecimal partPrice, BigDecimal tef,
                                        BigDecimal packaging,
                                        boolean applies, boolean validated) {
        return new ModificationImpact(
                partPrice != null ? partPrice : BigDecimal.ZERO,
                tef != null ? tef : BigDecimal.ZERO,
                packaging != null ? packaging : BigDecimal.ZERO,
                applies,
                validated
        );
    }
}
