package com.smartkam.project;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "modification_impact")
class SheetImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id")
    private ModificationSheet sheet;

    @Column(name = "part_price")
    private BigDecimal partPrice;

    @Column(name = "tef_amortization")
    private BigDecimal tefAmortization;

    @Column(name = "tooling_amount")
    private BigDecimal toolingAmount;

    public Long getId()                    { return id; }
    public BigDecimal getPartPrice()       { return partPrice; }
    public BigDecimal getTefAmortization() { return tefAmortization; }
    public BigDecimal getToolingAmount()   { return toolingAmount; }
}
