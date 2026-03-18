package com.smartkam.project;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "product_family")
class ProductFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String code;

    private String designation;

    @Column(name = "productivity_rate")
    private java.math.BigDecimal productivityRate;

    @Column(name = "productivity_years")
    private int productivityYears;

    @Column(name = "rd_drop_year")
    private int rdDropYear;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY)
    @OrderBy("refInternal ASC")
    private List<ProductReference> references;

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public String getCode() { return code; }
    public String getDesignation() { return designation; }
    public java.math.BigDecimal getProductivityRate() { return productivityRate; }
    public int getProductivityYears() { return productivityYears; }
    public int getRdDropYear() { return rdDropYear; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<ProductReference> getReferences() { return references; }
}
