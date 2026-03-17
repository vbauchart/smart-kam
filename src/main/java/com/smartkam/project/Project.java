package com.smartkam.project;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "project")
class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(name = "sop_date")
    private LocalDate sopDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProductFamily> families;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProjectStatus getStatus() { return status; }
    public LocalDate getSopDate() { return sopDate; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<ProductFamily> getFamilies() { return families; }
}
