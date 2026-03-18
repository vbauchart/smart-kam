-- Smart KAM — V5: Per-family productivity parameters
-- Each family has its own productivity rate, duration, and R&D drop year.
-- Values extracted from the Excel reference file (PF1..PF7 projection sections).

-- ============================================================
-- 1. Add columns to product_family
-- ============================================================
ALTER TABLE product_family
    ADD COLUMN productivity_rate  NUMERIC(6, 4) NOT NULL DEFAULT -0.01,
    ADD COLUMN productivity_years INT           NOT NULL DEFAULT 4,
    ADD COLUMN rd_drop_year       INT           NOT NULL DEFAULT 7;

-- ============================================================
-- 2. Seed per-family values
-- ============================================================
-- PF1: rate=-1%, prod_years=4, tombée=SOP+7  (defaults — no update needed)
-- PF2: rate=-2%, prod_years=5, tombée=SOP+7
UPDATE product_family SET productivity_rate = -0.02, productivity_years = 5 WHERE code = 'PF2';
-- PF3: rate=-2%, prod_years=5, tombée=SOP+3
UPDATE product_family SET productivity_rate = -0.02, productivity_years = 5, rd_drop_year = 3 WHERE code = 'PF3';
-- PF4: rate=0%, prod_years=0, tombée=SOP+5
UPDATE product_family SET productivity_rate = 0, productivity_years = 0, rd_drop_year = 5 WHERE code = 'PF4';
-- PF5: rate=-1%, prod_years=4, tombée=SOP+7  (same as defaults — no update needed)
-- PF6: rate=-2%, prod_years=5, tombée=SOP+7
UPDATE product_family SET productivity_rate = -0.02, productivity_years = 5 WHERE code = 'PF6';
-- PF7: rate=-2%, prod_years=5, tombée=SOP+3
UPDATE product_family SET productivity_rate = -0.02, productivity_years = 5, rd_drop_year = 3 WHERE code = 'PF7';
