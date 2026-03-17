-- Smart KAM — Initial schema
-- Sprint 0: tables core du moteur de pricing

CREATE TABLE project (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50)  NOT NULL DEFAULT 'RFQ',
    sop_date    DATE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_project_status CHECK (status IN (
        'RFQ', 'OFFER', 'LOI', 'DEVELOPMENT', 'SOP', 'PRODUCTION', 'EOP'
    ))
);

CREATE TABLE product_family (
    id          BIGSERIAL PRIMARY KEY,
    project_id  BIGINT      NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    code        VARCHAR(50) NOT NULL,
    designation VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE product_reference (
    id           BIGSERIAL PRIMARY KEY,
    family_id    BIGINT       NOT NULL REFERENCES product_family(id) ON DELETE CASCADE,
    ref_internal VARCHAR(100) NOT NULL,
    ref_client   VARCHAR(100),
    description  VARCHAR(255),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- One row per reference: the three price components + computed SOP values
CREATE TABLE price_breakdown (
    id              BIGSERIAL PRIMARY KEY,
    reference_id    BIGINT         NOT NULL REFERENCES product_reference(id) ON DELETE CASCADE,
    base_price      NUMERIC(12, 4) NOT NULL,
    rd_amortization NUMERIC(12, 4) NOT NULL DEFAULT 0,
    packaging       NUMERIC(12, 4) NOT NULL DEFAULT 0,
    -- Computed and stored by the pricing engine
    sop_initial     NUMERIC(12, 4),  -- Rule 1 result at contract signing
    sop_updated     NUMERIC(12, 4),  -- Rule 2 result after validated modifications
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_price_breakdown_reference UNIQUE (reference_id)
);

-- F4 / PCICN modification sheets
CREATE TABLE modification_sheet (
    id            BIGSERIAL PRIMARY KEY,
    project_id    BIGINT         NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    number        VARCHAR(50)    NOT NULL,
    description   TEXT,
    status        VARCHAR(20)    NOT NULL DEFAULT 'OPEN',
    global_impact NUMERIC(12, 4),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sheet_status CHECK (status IN ('OPEN', 'VALIDATED', 'CANCELED'))
);

-- Price impact per modification sheet (part price delta + tooling amortization delta)
CREATE TABLE modification_impact (
    id               BIGSERIAL PRIMARY KEY,
    sheet_id         BIGINT         NOT NULL REFERENCES modification_sheet(id) ON DELETE CASCADE,
    part_price       NUMERIC(12, 4) NOT NULL DEFAULT 0,
    tef_amortization NUMERIC(12, 4) NOT NULL DEFAULT 0,
    tooling_amount   NUMERIC(12, 2),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_modification_impact_sheet UNIQUE (sheet_id)
);

-- Application matrix: which modification applies to which reference (Y/N)
CREATE TABLE modification_application (
    id           BIGSERIAL PRIMARY KEY,
    sheet_id     BIGINT  NOT NULL REFERENCES modification_sheet(id) ON DELETE CASCADE,
    reference_id BIGINT  NOT NULL REFERENCES product_reference(id) ON DELETE CASCADE,
    applies      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_modification_application UNIQUE (sheet_id, reference_id)
);

-- Indexes
CREATE INDEX idx_product_family_project    ON product_family(project_id);
CREATE INDEX idx_product_reference_family  ON product_reference(family_id);
CREATE INDEX idx_price_breakdown_reference ON price_breakdown(reference_id);
CREATE INDEX idx_mod_sheet_project         ON modification_sheet(project_id);
CREATE INDEX idx_mod_sheet_status          ON modification_sheet(status);
CREATE INDEX idx_mod_application_sheet     ON modification_application(sheet_id);
CREATE INDEX idx_mod_application_reference ON modification_application(reference_id);
