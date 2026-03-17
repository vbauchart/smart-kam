-- Smart KAM — Seed data from PF1 Excel sheet
-- Source: "Outil suivi prix & modifications.xlsm" — sheet PF1
-- "MyProject - Product Family n° 1"
-- All values verified against the Excel reference file.

INSERT INTO project (id, name, description, status, sop_date)
VALUES (1, 'MyProject', 'Volants automobile — Projet de référence issu de l''Excel', 'PRODUCTION', '2014-01-01');

INSERT INTO product_family (id, project_id, code, designation)
VALUES (1, 1, 'PF1', 'Product Family n° 1 — Volants');

-- 5 product references (PU = polyuréthane, Cuir = leather)
INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (1, 1, 'PF1-1', '123456789', 'Volant PU — Standard'),
    (2, 1, 'PF1-2', '123456790', 'Volant PU — Avec airbag'),
    (3, 1, 'PF1-3', '123456791', 'Volant PU — Chauffant'),
    (4, 1, 'PF1-4', '123456792', 'Volant Cuir — Standard'),
    (5, 1, 'PF1-5', '123456793', 'Volant Cuir — Premium');

-- Initial contract prices (LOI / F1 sheet)
-- R&D: 1500 K€ sur 5 ans à 1 500 000 pièces = 0.20 €/pce/an × 5 ans ≈ 4.50 €/pce
-- Expected SOP_initial (Rule 1): base + R&D + packaging
--   PF1-1: 10 + 4.5 + 0.5 = 15.00
--   PF1-2: 12 + 4.5 + 0.5 = 17.00
--   PF1-3: 14 + 4.5 + 0.5 = 19.00
--   PF1-4: 20 + 4.5 + 0.5 = 25.00
--   PF1-5: 25 + 4.5 + 0.5 = 30.00
INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (1, 1, 10.0000, 4.5000, 0.5000, 15.0000),
    (2, 2, 12.0000, 4.5000, 0.5000, 17.0000),
    (3, 3, 14.0000, 4.5000, 0.5000, 19.0000),
    (4, 4, 20.0000, 4.5000, 0.5000, 25.0000),
    (5, 5, 25.0000, 4.5000, 0.5000, 30.0000);

-- Modification sheets
-- Productivity: -1% × 4 years (SOP+1 to SOP+4)
-- Tombée des rondelles: SOP+7 (2021)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (1,  1, 'F004',      'Nouvelle définition déco part volant',                   'CANCELED',  1.8900),
    (2,  1, 'F005',      'Nouvelles zones d''arrêt cuir',                           'VALIDATED', -1.2000),
    (3,  1, 'F009',      'Nouveau cache boutons RVLV',                              'CANCELED',  0.0000),
    (4,  1, 'F013',      'Régularisation dépassement études (demande refusée)',      'CANCELED',  0.0500),
    (5,  1, 'F012-PU',   'MAJ poids matières avec num RO (PU)',                     'VALIDATED',  0.3200),
    (6,  1, 'F012-Cuir', 'MAJ poids matières avec num RO (Cuir)',                   'VALIDATED',  0.4800),
    (7,  1, 'F015',      'Modification coque arrière',                              'VALIDATED',  0.1200),
    (8,  1, 'F017',      'Mise à jour du prix des PF1itchs',                        'OPEN',       0.4770),
    (9,  1, 'F017bis',   'Mise à jour du prix des PF1itchs (bis)',                  'OPEN',      -0.2670),
    (10, 1, 'F018',      'Changement couture volants cuir',                         'OPEN',       0.5250);

-- Impact per sheet (part_price + tef_amortization)
INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (1,   1,  1.7900,  0.1000, 32500),   -- F004 (CANCELED — no effect)
    (2,   2, -1.2000,  0.0000,     0),   -- F005 VALIDATED
    (3,   3,  0.0000,  0.0000,  NULL),   -- F009 (CANCELED)
    (4,   4,  0.0000,  0.0500, 21680),   -- F013 (CANCELED)
    (5,   5,  0.3200,  0.0000,  NULL),   -- F012-PU VALIDATED
    (6,   6,  0.4800,  0.0000,  NULL),   -- F012-Cuir VALIDATED
    (7,   7,  0.0300,  0.0900, 53000),   -- F015 VALIDATED
    (8,   8,  0.4770,  0.0000,  NULL),   -- F017 OPEN
    (9,   9, -0.2670,  0.0000,  NULL),   -- F017bis OPEN
    (10, 10,  0.0450,  0.4800,  7700);   -- F018 OPEN

-- Application matrix
-- F004 (CANCELED): PF1-2, PF1-3, PF1-4, PF1-5 — no effect due to CANCELED status
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (1, 1, FALSE), (1, 2, TRUE),  (1, 3, TRUE),  (1, 4, TRUE),  (1, 5, TRUE);

-- F005 (VALIDATED): PF1-4, PF1-5 only
--   => PF1-4 base: 20 + (-1.2) = 18.8 (partial — also has F012-Cuir and F015)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (2, 1, FALSE), (2, 2, FALSE), (2, 3, FALSE), (2, 4, TRUE),  (2, 5, TRUE);

-- F009 (CANCELED): none
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (3, 1, FALSE), (3, 2, FALSE), (3, 3, FALSE), (3, 4, FALSE), (3, 5, FALSE);

-- F013 (CANCELED): all
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (4, 1, TRUE),  (4, 2, TRUE),  (4, 3, TRUE),  (4, 4, TRUE),  (4, 5, TRUE);

-- F012-PU (VALIDATED): PF1-1, PF1-2, PF1-3 (polyuréthane references)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (5, 1, TRUE),  (5, 2, TRUE),  (5, 3, TRUE),  (5, 4, FALSE), (5, 5, FALSE);

-- F012-Cuir (VALIDATED): PF1-4, PF1-5 (leather references)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (6, 1, FALSE), (6, 2, FALSE), (6, 3, FALSE), (6, 4, TRUE),  (6, 5, TRUE);

-- F015 (VALIDATED): all references
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (7, 1, TRUE),  (7, 2, TRUE),  (7, 3, TRUE),  (7, 4, TRUE),  (7, 5, TRUE);

-- F017 (OPEN): PF1-2, PF1-4 — no effect due to OPEN status
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (8, 1, FALSE), (8, 2, TRUE),  (8, 3, FALSE), (8, 4, TRUE),  (8, 5, FALSE);

-- F017bis (OPEN): PF1-3, PF1-5
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (9, 1, FALSE), (9, 2, FALSE), (9, 3, TRUE),  (9, 4, FALSE), (9, 5, TRUE);

-- F018 (OPEN): PF1-4, PF1-5
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (10, 1, FALSE), (10, 2, FALSE), (10, 3, FALSE), (10, 4, TRUE), (10, 5, TRUE);

-- Reset sequences
SELECT setval('project_id_seq', 10);
SELECT setval('product_family_id_seq', 10);
SELECT setval('product_reference_id_seq', 10);
SELECT setval('price_breakdown_id_seq', 10);
SELECT setval('modification_sheet_id_seq', 20);
SELECT setval('modification_impact_id_seq', 20);
SELECT setval('modification_application_id_seq', 100);
