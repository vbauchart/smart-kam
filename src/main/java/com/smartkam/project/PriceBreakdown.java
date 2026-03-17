package com.smartkam.project;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "price_breakdown")
class PriceBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private ProductReference reference;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "rd_amortization")
    private BigDecimal rdAmortization;

    private BigDecimal packaging;

    @Column(name = "sop_initial")
    private BigDecimal sopInitial;

    @Column(name = "sop_updated")
    private BigDecimal sopUpdated;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public ProductReference getReference() { return reference; }
    public BigDecimal getBasePrice() { return basePrice; }
    public BigDecimal getRdAmortization() { return rdAmortization; }
    public BigDecimal getPackaging() { return packaging; }
    public BigDecimal getSopInitial() { return sopInitial; }
    public BigDecimal getSopUpdated() { return sopUpdated; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public void setSopInitial(BigDecimal sopInitial) { this.sopInitial = sopInitial; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
