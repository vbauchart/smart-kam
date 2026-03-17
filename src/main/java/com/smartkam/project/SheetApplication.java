package com.smartkam.project;

import jakarta.persistence.*;

@Entity
@Table(name = "modification_application")
class SheetApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id")
    private ModificationSheet sheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private ProductReference reference;

    private boolean applies;

    public Long getId()                   { return id; }
    public ModificationSheet getSheet()   { return sheet; }
    public ProductReference getReference(){ return reference; }
    public boolean isApplies()            { return applies; }

    public void setApplies(boolean applies) { this.applies = applies; }
}
