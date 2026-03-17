package com.smartkam.project;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "modification_sheet")
class ModificationSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String number;

    private String description;

    @Enumerated(EnumType.STRING)
    private ModificationStatus status;

    @Column(name = "global_impact")
    private BigDecimal globalImpact;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "sheet", fetch = FetchType.LAZY)
    private SheetImpact impact;

    @OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY)
    private List<SheetApplication> applications;

    public Long getId()                          { return id; }
    public Project getProject()                  { return project; }
    public String getNumber()                    { return number; }
    public String getDescription()               { return description; }
    public ModificationStatus getStatus()        { return status; }
    public BigDecimal getGlobalImpact()          { return globalImpact; }
    public SheetImpact getImpact()               { return impact; }
    public List<SheetApplication> getApplications() { return applications; }

    public void setStatus(ModificationStatus status) { this.status = status; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
