package com.smartkam.project;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "product_reference")
class ProductReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private ProductFamily family;

    @Column(name = "ref_internal")
    private String refInternal;

    @Column(name = "ref_client")
    private String refClient;

    private String description;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "reference", fetch = FetchType.LAZY)
    private PriceBreakdown priceBreakdown;

    public Long getId() { return id; }
    public ProductFamily getFamily() { return family; }
    public String getRefInternal() { return refInternal; }
    public String getRefClient() { return refClient; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public PriceBreakdown getPriceBreakdown() { return priceBreakdown; }
}
