-- Smart KAM — V4: Add packaging_impact column and fix data
-- The Excel uses 3 impact dimensions: Part Price, TEF (R&D amortization), and Packaging.
-- Our schema only had 2 (part_price, tef_amortization). This migration adds the 3rd.

-- ============================================================
-- 1. Add packaging_impact column to modification_impact
-- ============================================================
ALTER TABLE modification_impact
    ADD COLUMN packaging_impact NUMERIC(12, 4) NOT NULL DEFAULT 0;

-- ============================================================
-- 2. Fix PF2 DCL entries: move from part_price to packaging_impact
-- ============================================================
-- Sheet 15 (DCL-1): packaging impact = 0.031 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = 0.0310 WHERE id = 15;
-- Sheet 16 (DCL-2): packaging impact = -0.008 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = -0.0080 WHERE id = 16;

-- ============================================================
-- 3. Fix PF3 DCL entry: move from part_price to packaging_impact
-- ============================================================
-- Sheet 20 (DCL): packaging = 0.085 - 0.05 = 0.035 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = 0.0350 WHERE id = 20;

-- ============================================================
-- 4. Fix PF5 packaging entries: move from part_price to packaging_impact
-- ============================================================
-- Sheet 35 (F023): packaging = 0.058 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = 0.0580 WHERE id = 35;
-- Sheet 44 (F038): packaging = 0.094 - 0.046 = 0.048 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = 0.0480 WHERE id = 44;
-- Sheet 45 (DCL PF5-1): packaging = 0.106 - (0.115 + 0.058) = -0.067 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = -0.0670 WHERE id = 45;
-- Sheet 48 (DCL-2 PF5): packaging = 0.104 - 0.094 = 0.010 (was in part_price)
UPDATE modification_impact SET part_price = 0, packaging_impact = 0.0100 WHERE id = 48;

-- ============================================================
-- 5. Add 3 missing PF1 "MAJ Matières" sheets (Excel R23-R25)
-- ============================================================
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (71, 1, 'MAJ-Mat-PU',    'MAJ Matières PU',                   'OPEN', 0.3050),
    (72, 1, 'MAJ-Mat-PU-Sw', 'MAJ Matières PU + PF1itches',       'OPEN', 0.2995),
    (73, 1, 'MAJ-Mat-LW',    'MAJ Matières LW',                   'OPEN', 0.3068);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount, packaging_impact)
VALUES
    (71, 71, 0.3050, 0.0000, NULL, 0),   -- MAJ Matières PU
    (72, 72, 0.2995, 0.0000, NULL, 0),   -- MAJ Matières PU + PF1itches
    (73, 73, 0.3068, 0.0000, NULL, 0);   -- MAJ Matières LW

-- Application matrix for missing PF1 sheets
-- MAJ-Mat-PU (R23): PF1-1 only
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (71, 1, TRUE),  (71, 2, FALSE), (71, 3, FALSE), (71, 4, FALSE), (71, 5, FALSE);
-- MAJ-Mat-PU-Sw (R24): PF1-2, PF1-3
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (72, 1, FALSE), (72, 2, TRUE),  (72, 3, TRUE),  (72, 4, FALSE), (72, 5, FALSE);
-- MAJ-Mat-LW (R25): PF1-4, PF1-5
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (73, 1, FALSE), (73, 2, FALSE), (73, 3, FALSE), (73, 4, TRUE),  (73, 5, TRUE);

-- ============================================================
-- 6. Update PF1 packaging from 0.5 to 0.064
--    (Excel R33: hardcoded 0.064 — manual correction not tracked via F4 sheets)
--    sop_initial stays unchanged (historical snapshot)
-- ============================================================
UPDATE price_breakdown SET packaging = 0.0640 WHERE reference_id IN (1, 2, 3, 4, 5);

-- ============================================================
-- 7. Update sequences
-- ============================================================
SELECT setval('modification_sheet_id_seq', 80);
SELECT setval('modification_impact_id_seq', 80);
