package com.smartkam.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PriceComponents — value object")
class PriceComponentsTest {

    @Test
    @DisplayName("sop() délègue à PricingEngine.computeInitialSop")
    void sop_delegates_to_pricing_engine() {
        var pc = new PriceComponents(bd("10"), bd("4.5"), bd("0.5"));
        assertThat(pc.sop()).isEqualByComparingTo(bd("15.00"));
    }

    @Test
    @DisplayName("sop() avec R&D et packaging à zéro retourne la base")
    void sop_with_zero_rd_and_packaging() {
        var pc = new PriceComponents(bd("42.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        assertThat(pc.sop()).isEqualByComparingTo(bd("42.00"));
    }

    @Test
    @DisplayName("Les composantes sont accessibles via les accesseurs du record")
    void record_accessors() {
        var pc = new PriceComponents(bd("10"), bd("4.5"), bd("0.5"));
        assertThat(pc.base()).isEqualByComparingTo(bd("10"));
        assertThat(pc.rdAmortization()).isEqualByComparingTo(bd("4.5"));
        assertThat(pc.packaging()).isEqualByComparingTo(bd("0.5"));
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
