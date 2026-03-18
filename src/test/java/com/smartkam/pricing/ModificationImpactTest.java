package com.smartkam.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModificationImpact — factory et value object")
class ModificationImpactTest {

    @Test
    @DisplayName("of() crée un impact avec les valeurs fournies")
    void of_creates_impact_with_values() {
        var mi = ModificationImpact.of(bd("0.32"), bd("0.09"), bd("0.05"), true, true);
        assertThat(mi.partPrice()).isEqualByComparingTo(bd("0.32"));
        assertThat(mi.tefAmortization()).isEqualByComparingTo(bd("0.09"));
        assertThat(mi.packagingImpact()).isEqualByComparingTo(bd("0.05"));
        assertThat(mi.applies()).isTrue();
        assertThat(mi.validated()).isTrue();
    }

    @Test
    @DisplayName("of() remplace les null par BigDecimal.ZERO")
    void of_replaces_nulls_with_zero() {
        var mi = ModificationImpact.of(null, null, null, false, false);
        assertThat(mi.partPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(mi.tefAmortization()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(mi.packagingImpact()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("of() traite chaque null indépendamment")
    void of_handles_partial_nulls() {
        var mi = ModificationImpact.of(bd("1.5"), null, bd("0.03"), true, true);
        assertThat(mi.partPrice()).isEqualByComparingTo(bd("1.5"));
        assertThat(mi.tefAmortization()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(mi.packagingImpact()).isEqualByComparingTo(bd("0.03"));
    }

    @Test
    @DisplayName("Deux impacts identiques sont equals (record semantics)")
    void record_equality() {
        var a = ModificationImpact.of(bd("0.32"), bd("0.09"), bd("0"), true, true);
        var b = ModificationImpact.of(bd("0.32"), bd("0.09"), bd("0"), true, true);
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("Deux impacts différents ne sont pas equals")
    void record_inequality() {
        var a = ModificationImpact.of(bd("0.32"), bd("0.09"), bd("0"), true, true);
        var b = ModificationImpact.of(bd("0.32"), bd("0.09"), bd("0"), false, true);
        assertThat(a).isNotEqualTo(b);
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
