-- Smart KAM — Seed data for PF2–PF7 from Excel
-- Source: "Outil suivi prix & modifications.xlsm"
-- All values verified against the Excel reference file.
-- PF1 data is already in V2__seed_pf1_data.sql.

-- ============================================================
-- PF2 — Product Family n° 2 (Airbags)
-- Productivity: -2% × 5 years
-- Plant: Safe Bag, Ponte de Lima — Incoterm: FCA
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (2, 1, 'PF2', 'Product Family n° 2 — Airbags');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (6, 2, 'PF2-1', '123456789', 'Airbag Active — Standard'),
    (7, 2, 'PF2-2', '123456790', 'Airbag Active — Variante A'),
    (8, 2, 'PF2-3', '123456791', 'Airbag Active — Variante B');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (6, 6, 20.0000, 0.2500, 0.0900, 20.3400),
    (7, 7, 25.0000, 0.2500, 0.0900, 25.3400),
    (8, 8, 30.0000, 0.2500, 0.0900, 30.3400);

-- PF2 modification sheets (IDs 11–17)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (11, 1, 'F006',        'Nouveau style logo Client 1',                       'VALIDATED',  0.8800),
    (12, 1, 'F007-PF2',    'Nouveau style logos PF2',                            'VALIDATED',  0.4200),
    (13, 1, 'F007-PF3',    'Nouveau style logos PF3',                            'VALIDATED',  0.8800),
    (14, 1, 'F014',        'Régularisation dépassement études (demande refusée)','CANCELED',   0.0300),
    (15, 1, 'DCL-1',       'DCL intermédiaire à 7 pièces par bac',              'VALIDATED',  0.0310),
    (16, 1, 'DCL-2',       'Passage de 7 à 8 pièces par bacs',                  'VALIDATED', -0.0080),
    (17, 1, 'MAJ-Mat',     'MAJ Matières',                                       'OPEN',       0.1395);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (11, 11, 0.7600, 0.1200, 65000),    -- F006
    (12, 12, 0.2600, 0.1600, 108000),   -- F007-PF2
    (13, 13, 0.2600, 0.6200, 74000),    -- F007-PF3
    (14, 14, 0.0000, 0.0300, 14910),    -- F014 CANCELED
    (15, 15, 0.0310, 0.0000, NULL),     -- DCL-1 (packaging)
    (16, 16,-0.0080, 0.0000, NULL),     -- DCL-2 (packaging)
    (17, 17, 0.1395, 0.0000, NULL);     -- MAJ Matières OPEN

-- PF2 application matrix (3 refs × 7 sheets)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (11, 6, TRUE),  (11, 7, FALSE), (11, 8, FALSE),  -- F006: PF2-1 only
    (12, 6, FALSE), (12, 7, TRUE),  (12, 8, FALSE),  -- F007-PF2: PF2-2 only
    (13, 6, FALSE), (13, 7, FALSE), (13, 8, TRUE),   -- F007-PF3: PF2-3 only
    (14, 6, TRUE),  (14, 7, TRUE),  (14, 8, TRUE),   -- F014: all (CANCELED)
    (15, 6, TRUE),  (15, 7, TRUE),  (15, 8, TRUE),   -- DCL-1: all
    (16, 6, TRUE),  (16, 7, TRUE),  (16, 8, TRUE),   -- DCL-2: all
    (17, 6, TRUE),  (17, 7, TRUE),  (17, 8, TRUE);   -- MAJ Matières: all

-- ============================================================
-- PF3 — Product Family n° 3
-- Productivity: -2% × 5 years
-- Plant: Safe Bag, Ponte de Lima — Incoterm: FCA
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (3, 1, 'PF3', 'Product Family n° 3 — Sécurité');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (9, 3, 'PF3-1', '9826547821', 'Module sécurité — LH/RH');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (9, 9, 23.5400, 15.2500, 0.0500, 38.8400);

-- PF3 modification sheets (IDs 18–21)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (18, 1, 'F011',      'Modification positionnement générateur + ajout déflecteur', 'VALIDATED',  7.0900),
    (19, 1, 'F012',      'Modification fixation déflecteur',                          'VALIDATED',  0.1500),
    (20, 1, 'DCL',       'Mise à jour DCL',                                          'VALIDATED',  0.0350),
    (21, 1, 'MAJ-Mat',   'MAJ Matières',                                              'OPEN',       0.0681);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (18, 18, 1.9700, 5.1200, 169000),   -- F011
    (19, 19, 0.1500, 0.0000, 16500),    -- F012
    (20, 20, 0.0350, 0.0000, NULL),     -- DCL (packaging)
    (21, 21, 0.0681, 0.0000, NULL);     -- MAJ Matières OPEN

-- PF3 application matrix (1 ref × 4 sheets)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (18, 9, TRUE),   -- F011
    (19, 9, TRUE),   -- F012
    (20, 9, TRUE),   -- DCL
    (21, 9, TRUE);   -- MAJ Matières

-- ============================================================
-- PF4 — Product Family n° 4
-- No productivity
-- Plant: Ma Société Timisoara — Incoterm: CIP
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (4, 1, 'PF4', 'Product Family n° 4 — Composants');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (10, 4, 'PF4-1', 'xxxx', 'Composant standard');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (10, 10, 15.0000, 0.0000, 0.0500, 15.0500);

-- PF4 modification sheets (IDs 22–23)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (22, 1, 'F002', 'Décommunalisation airbag',            'VALIDATED', 0.6500),
    (23, 1, 'F014', 'Modification of traceability label',  'VALIDATED', 0.1000);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (22, 22, 0.2000, 0.4500, 19700),   -- F002
    (23, 23, 0.0000, 0.1000, 5000);    -- F014

-- PF4 application matrix (1 ref × 2 sheets)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (22, 10, TRUE),   -- F002
    (23, 10, TRUE);   -- F014

-- ============================================================
-- PF5 — Product Family n° 5 (Ceintures)
-- Productivity: -1% × 5 years
-- Plants: Ma Société Polska (Czestochowa), Ma Société Carr (Stara Boleslav)
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (5, 1, 'PF5', 'Product Family n° 5 — Ceintures');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (11, 5, 'PF5-1', NULL, 'Ceinture Sous-Groupe 1 — Czestochowa'),
    (12, 5, 'PF5-2', NULL, 'Ceinture Sous-Groupe 2 — Stara Boleslav'),
    (13, 5, 'PF5-3', NULL, 'Ceinture Sous-Groupe 3'),
    (14, 5, 'PF5-4', NULL, 'Ceinture Sous-Groupe 4 — CIP'),
    (15, 5, 'PF5-5', NULL, 'Ceinture Sous-Groupe 5 — Stara Boleslav'),
    (16, 5, 'PF5-6', NULL, 'Ceinture Sous-Groupe 6 — CIP'),
    (17, 5, 'PF5-7', NULL, 'Ceinture Sous-Groupe 7 — Stara Boleslav');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (11, 11, 20.0000, 0.1028, 0.1150, 20.2178),
    (12, 12, 21.0000, 0.1199, 0.0460, 21.1659),
    (13, 13, 22.0000, 0.1669, 0.0460, 22.2129),
    (14, 14, 23.0000, 0.2992, 0.0460, 23.3452),
    (15, 15, 24.0000, 0.3422, 0.0460, 24.3882),
    (16, 16, 25.0000, 0.4670, 0.0460, 25.5130),
    (17, 17, 26.0000, 0.4670, 0.0460, 26.5130);

-- PF5 modification sheets (IDs 24–48)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (24, 1, 'F002',       '(Annulé)',                                                          'CANCELED',   0.0000),
    (25, 1, 'F003',       'Replace anchor bracket',                                            'VALIDATED',  0.1700),
    (26, 1, 'F008',       'Replace anchor bracket (PF5-3)',                                    'VALIDATED',  0.7950),
    (27, 1, 'F011',       'Diversity L1/L2',                                                   'VALIDATED',  0.1240),
    (28, 1, 'F014',       'No need for sensor housing tooling',                                'VALIDATED',  0.0000),
    (29, 1, 'F015',       'Replace bended tongue by a flat tongue',                            'VALIDATED',  0.0950),
    (30, 1, 'F016',       '(Annulé)',                                                          'CANCELED',   0.0000),
    (31, 1, 'F018',       '(Annulé)',                                                          'CANCELED',   0.0000),
    (32, 1, 'F019',       'R&D following PF5-2 cancel',                                        'VALIDATED',  0.0250),
    (33, 1, 'F020',       'Transfert of R&D amort from toto to titi',                          'VALIDATED', -0.0250),
    (34, 1, 'F022',       '(Annulé)',                                                          'CANCELED',   0.0000),
    (35, 1, 'F023',       'Reduction of number of parts per packaging',                        'VALIDATED',  0.0580),
    (36, 1, 'F024',       'Change of screw',                                                   'VALIDATED',  0.1910),
    (37, 1, 'F025',       'Screw assembly modification',                                       'VALIDATED',  0.1960),
    (38, 1, 'F026',       '(Annulé)',                                                          'CANCELED',   0.0000),
    (39, 1, 'F028',       'Add of webbing stop button',                                        'VALIDATED',  0.3560),
    (40, 1, 'F029',       'Change from cardboard to metal washer',                             'VALIDATED',  0.0170),
    (41, 1, 'F035',       'Modification of PF5-5 retractor base',                              'VALIDATED',  0.0300),
    (42, 1, 'F037',       '2D code content modification',                                      'OPEN',       0.0200),
    (43, 1, 'F037bis',    '2D code content modification (bis)',                                'OPEN',       0.0250),
    (44, 1, 'F038',       'MAJ DCL PF5-3/5/7',                                                'VALIDATED',  0.0480),
    (45, 1, 'DCL',        'MAJ DCL PF5-1 — suppression ceinture carton',                      'VALIDATED', -0.0670),
    (46, 1, 'MAJ-Mat-SG1','MAJ Matières Sous Groupe 1',                                       'VALIDATED',  0.1700),
    (47, 1, 'MAJ-Mat-SG2','MAJ Matières Sous Groupe 2',                                       'VALIDATED',  0.0840),
    (48, 1, 'DCL-2',      'MAJ DCL PF5-3/5/7 — ajout sac plastique par bac',                  'VALIDATED',  0.0100);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (24, 24, 0.0000, 0.0000, NULL),       -- F002 CANCELED
    (25, 25, 0.1520, 0.0180, 39000),      -- F003
    (26, 26, 0.6730, 0.1220, 47000),      -- F008
    (27, 27, 0.1240, 0.0000, NULL),       -- F011
    (28, 28, 0.0000, 0.0000, -34500),     -- F014 (tooling reduction)
    (29, 29, 0.0950, 0.0000, NULL),       -- F015
    (30, 30, 0.0000, 0.0000, NULL),       -- F016 CANCELED
    (31, 31, 0.0000, 0.0000, NULL),       -- F018 CANCELED
    (32, 32, 0.0000, 0.0250, 31000),      -- F019
    (33, 33, 0.0000,-0.0250, NULL),       -- F020
    (34, 34, 0.0000, 0.0000, NULL),       -- F022 CANCELED
    (35, 35, 0.0580, 0.0000, NULL),       -- F023 (packaging)
    (36, 36, 0.1910, 0.0000, NULL),       -- F024
    (37, 37, 0.1770, 0.0190, 5850),       -- F025
    (38, 38, 0.0000, 0.0000, NULL),       -- F026 CANCELED
    (39, 39, 0.3560, 0.0000, NULL),       -- F028
    (40, 40, 0.0170, 0.0000, NULL),       -- F029
    (41, 41, 0.0000, 0.0300, 8000),       -- F035
    (42, 42, 0.0000, 0.0200, 15000),      -- F037 OPEN
    (43, 43, 0.0000, 0.0250, 7500),       -- F037bis OPEN
    (44, 44, 0.0480, 0.0000, NULL),       -- F038 (packaging)
    (45, 45,-0.0670, 0.0000, NULL),       -- DCL (packaging)
    (46, 46, 0.1700, 0.0000, NULL),       -- MAJ Mat SG1
    (47, 47, 0.0840, 0.0000, NULL),       -- MAJ Mat SG2
    (48, 48, 0.0100, 0.0000, NULL);       -- DCL-2 (packaging)

-- PF5 application matrix (7 refs × 25 sheets)
-- Refs: 11=PF5-1, 12=PF5-2, 13=PF5-3, 14=PF5-4, 15=PF5-5, 16=PF5-6, 17=PF5-7
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    -- F002 CANCELED: PF5-1, PF5-2
    (24,11,TRUE), (24,12,TRUE), (24,13,FALSE),(24,14,FALSE),(24,15,FALSE),(24,16,FALSE),(24,17,FALSE),
    -- F003: PF5-1, PF5-2
    (25,11,TRUE), (25,12,TRUE), (25,13,FALSE),(25,14,FALSE),(25,15,FALSE),(25,16,FALSE),(25,17,FALSE),
    -- F008: PF5-3
    (26,11,FALSE),(26,12,FALSE),(26,13,TRUE), (26,14,FALSE),(26,15,FALSE),(26,16,FALSE),(26,17,FALSE),
    -- F011: PF5-6, PF5-7
    (27,11,FALSE),(27,12,FALSE),(27,13,FALSE),(27,14,FALSE),(27,15,FALSE),(27,16,TRUE), (27,17,TRUE),
    -- F014: PF5-5
    (28,11,FALSE),(28,12,FALSE),(28,13,FALSE),(28,14,FALSE),(28,15,TRUE), (28,16,FALSE),(28,17,FALSE),
    -- F015: PF5-1, PF5-2, PF5-3
    (29,11,TRUE), (29,12,TRUE), (29,13,TRUE), (29,14,FALSE),(29,15,FALSE),(29,16,FALSE),(29,17,FALSE),
    -- F016 CANCELED: none
    (30,11,FALSE),(30,12,FALSE),(30,13,FALSE),(30,14,FALSE),(30,15,FALSE),(30,16,FALSE),(30,17,FALSE),
    -- F018 CANCELED: PF5-3
    (31,11,FALSE),(31,12,FALSE),(31,13,TRUE), (31,14,FALSE),(31,15,FALSE),(31,16,FALSE),(31,17,FALSE),
    -- F019: PF5-1
    (32,11,TRUE), (32,12,FALSE),(32,13,FALSE),(32,14,FALSE),(32,15,FALSE),(32,16,FALSE),(32,17,FALSE),
    -- F020: PF5-3
    (33,11,FALSE),(33,12,FALSE),(33,13,TRUE), (33,14,FALSE),(33,15,FALSE),(33,16,FALSE),(33,17,FALSE),
    -- F022 CANCELED: PF5-1, PF5-2
    (34,11,TRUE), (34,12,TRUE), (34,13,FALSE),(34,14,FALSE),(34,15,FALSE),(34,16,FALSE),(34,17,FALSE),
    -- F023: PF5-1
    (35,11,TRUE), (35,12,FALSE),(35,13,FALSE),(35,14,FALSE),(35,15,FALSE),(35,16,FALSE),(35,17,FALSE),
    -- F024: PF5-1, PF5-2
    (36,11,TRUE), (36,12,TRUE), (36,13,FALSE),(36,14,FALSE),(36,15,FALSE),(36,16,FALSE),(36,17,FALSE),
    -- F025: PF5-4
    (37,11,FALSE),(37,12,FALSE),(37,13,FALSE),(37,14,TRUE), (37,15,FALSE),(37,16,FALSE),(37,17,FALSE),
    -- F026 CANCELED: PF5-3
    (38,11,FALSE),(38,12,FALSE),(38,13,TRUE), (38,14,FALSE),(38,15,FALSE),(38,16,FALSE),(38,17,FALSE),
    -- F028: PF5-5
    (39,11,FALSE),(39,12,FALSE),(39,13,FALSE),(39,14,FALSE),(39,15,TRUE), (39,16,FALSE),(39,17,FALSE),
    -- F029: PF5-1, PF5-2
    (40,11,TRUE), (40,12,TRUE), (40,13,FALSE),(40,14,FALSE),(40,15,FALSE),(40,16,FALSE),(40,17,FALSE),
    -- F035: PF5-4
    (41,11,FALSE),(41,12,FALSE),(41,13,FALSE),(41,14,TRUE), (41,15,FALSE),(41,16,FALSE),(41,17,FALSE),
    -- F037 OPEN: PF5-1
    (42,11,TRUE), (42,12,FALSE),(42,13,FALSE),(42,14,FALSE),(42,15,FALSE),(42,16,FALSE),(42,17,FALSE),
    -- F037bis OPEN: PF5-2
    (43,11,FALSE),(43,12,TRUE), (43,13,FALSE),(43,14,FALSE),(43,15,FALSE),(43,16,FALSE),(43,17,FALSE),
    -- F038: PF5-2, PF5-4, PF5-6, PF5-7
    (44,11,FALSE),(44,12,TRUE), (44,13,FALSE),(44,14,TRUE), (44,15,FALSE),(44,16,TRUE), (44,17,TRUE),
    -- DCL: PF5-1
    (45,11,TRUE), (45,12,FALSE),(45,13,FALSE),(45,14,FALSE),(45,15,FALSE),(45,16,FALSE),(45,17,FALSE),
    -- MAJ Mat SG1: PF5-1
    (46,11,TRUE), (46,12,FALSE),(46,13,FALSE),(46,14,FALSE),(46,15,FALSE),(46,16,FALSE),(46,17,FALSE),
    -- MAJ Mat SG2: PF5-2..PF5-7
    (47,11,FALSE),(47,12,TRUE), (47,13,TRUE), (47,14,TRUE), (47,15,TRUE), (47,16,TRUE), (47,17,TRUE),
    -- DCL-2: PF5-2, PF5-4, PF5-6, PF5-7
    (48,11,FALSE),(48,12,TRUE), (48,13,FALSE),(48,14,TRUE), (48,15,FALSE),(48,16,TRUE), (48,17,TRUE);

-- ============================================================
-- PF6 — Product Family n° 6 (Boucles)
-- Productivity: -1% × 5 years
-- Plant: Ma Société Polska — Incoterm: CIP
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (6, 1, 'PF6', 'Product Family n° 6 — Boucles');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (18, 6, 'PF6-1', NULL, 'Boucle type 1'),
    (19, 6, 'PF6-2', NULL, 'Boucle type 2'),
    (20, 6, 'PF6-3', NULL, 'Boucle type 3'),
    (21, 6, 'PF6-4', NULL, 'Boucle type 4'),
    (22, 6, 'PF6-5', NULL, 'Boucle type 5'),
    (23, 6, 'PF6-6', NULL, 'Boucle type 6');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (18, 18, 2.6870, 0.0685, 0.0120, 2.7675),
    (19, 19, 2.6870, 0.0685, 0.0120, 2.7675),
    (20, 20, 1.5380, 0.1258, 0.0120, 1.6758),
    (21, 21, 1.6560, 0.0700, 0.0120, 1.7380),
    (22, 22, 2.0800, 0.0000, 0.0120, 2.0920),
    (23, 23, 1.2572, 0.1051, 0.0120, 1.3743);

-- PF6 modification sheets (IDs 49–61)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (49, 1, 'F004',        'Remove Anti-G device',                                  'CANCELED',  -0.2700),
    (50, 1, 'F006',        'Replace anchor bracket',                                'VALIDATED',  0.0140),
    (51, 1, 'F012',        'Change of PF6-3 design',                                'VALIDATED',  0.2220),
    (52, 1, 'F013',        'Reduce buckle length and add screw assy',               'VALIDATED',  0.8450),
    (53, 1, 'F017',        'Buckle length modification',                            'VALIDATED',  0.0120),
    (54, 1, 'F021',        'Design change',                                         'VALIDATED', -0.8310),
    (55, 1, 'F030',        'Embedded screw on PF6-4',                               'CANCELED',   0.0000),
    (56, 1, 'F031',        'Harness length increase and clip modification',         'VALIDATED',  0.0730),
    (57, 1, 'F038',        'PF6-3: Modification of damper rubber',                  'VALIDATED',  0.0200),
    (58, 1, 'MAJ-Mat-A',   'MAJ Matières PF6-1/2/3',                               'VALIDATED',  0.0290),
    (59, 1, 'MAJ-Mat-B',   'MAJ Matières PF6-4',                                   'VALIDATED',  0.0300),
    (60, 1, 'MAJ-Mat-C',   'MAJ Matières PF6-5',                                   'VALIDATED',  0.0210),
    (61, 1, 'MAJ-Mat-D',   'MAJ Matières PF6-6',                                   'VALIDATED',  0.0180);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (49, 49,-0.2700, 0.0000, NULL),       -- F004 CANCELED
    (50, 50, 0.0140, 0.0000, NULL),       -- F006
    (51, 51, 0.0370, 0.1850, 44000),      -- F012
    (52, 52, 0.8210, 0.0240, 35000),      -- F013
    (53, 53, 0.0120, 0.0000, NULL),       -- F017
    (54, 54,-0.8310, 0.0000, NULL),       -- F021
    (55, 55, 0.0000, 0.0000, NULL),       -- F030 CANCELED
    (56, 56, 0.0730, 0.0000, 2900),       -- F031
    (57, 57, 0.0100, 0.0100, 8000),       -- F038
    (58, 58, 0.0290, 0.0000, NULL),       -- MAJ Mat A
    (59, 59, 0.0300, 0.0000, NULL),       -- MAJ Mat B
    (60, 60, 0.0210, 0.0000, NULL),       -- MAJ Mat C
    (61, 61, 0.0180, 0.0000, NULL);       -- MAJ Mat D

-- PF6 application matrix (6 refs × 13 sheets)
-- Refs: 18=PF6-1, 19=PF6-2, 20=PF6-3, 21=PF6-4, 22=PF6-5, 23=PF6-6
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    -- F004 CANCELED: all
    (49,18,TRUE),(49,19,TRUE),(49,20,TRUE),(49,21,TRUE),(49,22,TRUE),(49,23,TRUE),
    -- F006: PF6-3
    (50,18,FALSE),(50,19,FALSE),(50,20,TRUE),(50,21,FALSE),(50,22,FALSE),(50,23,FALSE),
    -- F012: PF6-5
    (51,18,FALSE),(51,19,FALSE),(51,20,FALSE),(51,21,FALSE),(51,22,TRUE),(51,23,FALSE),
    -- F013: PF6-4
    (52,18,FALSE),(52,19,FALSE),(52,20,FALSE),(52,21,TRUE),(52,22,FALSE),(52,23,FALSE),
    -- F017: PF6-1, PF6-2
    (53,18,TRUE),(53,19,TRUE),(53,20,FALSE),(53,21,FALSE),(53,22,FALSE),(53,23,FALSE),
    -- F021: PF6-2
    (54,18,FALSE),(54,19,TRUE),(54,20,FALSE),(54,21,FALSE),(54,22,FALSE),(54,23,FALSE),
    -- F030 CANCELED: PF6-6
    (55,18,FALSE),(55,19,FALSE),(55,20,FALSE),(55,21,FALSE),(55,22,FALSE),(55,23,TRUE),
    -- F031: PF6-1
    (56,18,TRUE),(56,19,FALSE),(56,20,FALSE),(56,21,FALSE),(56,22,FALSE),(56,23,FALSE),
    -- F038: PF6-5
    (57,18,FALSE),(57,19,FALSE),(57,20,FALSE),(57,21,FALSE),(57,22,TRUE),(57,23,FALSE),
    -- MAJ Mat A: PF6-1, PF6-2, PF6-3
    (58,18,TRUE),(58,19,TRUE),(58,20,TRUE),(58,21,FALSE),(58,22,FALSE),(58,23,FALSE),
    -- MAJ Mat B: PF6-4
    (59,18,FALSE),(59,19,FALSE),(59,20,FALSE),(59,21,TRUE),(59,22,FALSE),(59,23,FALSE),
    -- MAJ Mat C: PF6-5
    (60,18,FALSE),(60,19,FALSE),(60,20,FALSE),(60,21,FALSE),(60,22,TRUE),(60,23,FALSE),
    -- MAJ Mat D: PF6-6
    (61,18,FALSE),(61,19,FALSE),(61,20,FALSE),(61,21,FALSE),(61,22,FALSE),(61,23,TRUE);

-- ============================================================
-- PF7 — Product Family n° 7 (Régleurs)
-- Productivity: -1% × 5 years  (applied only on PF7-1)
-- Plant: Ma Société Polska, Czestochowa — Incoterm: FCA
-- ============================================================

INSERT INTO product_family (id, project_id, code, designation)
VALUES (7, 1, 'PF7', 'Product Family n° 7 — Régleurs');

INSERT INTO product_reference (id, family_id, ref_internal, ref_client, description)
VALUES
    (24, 7, 'PF7-1',  '123456789', 'Régleur de hauteur — Standard'),
    (25, 7, 'PF7-1b', '987654321', 'Régleur de hauteur — Bouton');

INSERT INTO price_breakdown (id, reference_id, base_price, rd_amortization, packaging, sop_initial)
VALUES
    (24, 24, 2.0000, 0.0494, 0.0230, 2.0724),
    (25, 25, 0.2000, 0.0000, 0.0010, 0.2010);

-- PF7 modification sheets (IDs 62–70)
INSERT INTO modification_sheet (id, project_id, number, description, status, global_impact)
VALUES
    (62, 1, 'F001',     'Lever replacement',                                        'VALIDATED',  0.0420),
    (63, 1, 'F005',     '(Annulé)',                                                 'CANCELED',   0.0000),
    (64, 1, 'F007',     'HA length increase',                                       'VALIDATED',  0.0500),
    (65, 1, 'F009',     'New button color development',                             'VALIDATED',  0.0220),
    (66, 1, 'F024',     'Increase of gap between pilar loop and HA',                'VALIDATED',  0.2470),
    (67, 1, 'F029',     'Change from cardboard to metal washer',                    'VALIDATED',  0.0340),
    (68, 1, 'F032',     'Reduction of ribs on Height Adjuster + fuse label',        'VALIDATED',  0.0400),
    (69, 1, 'F036',     'Lever edges modification',                                 'VALIDATED',  0.0250),
    (70, 1, 'MAJ-Mat',  'MAJ Matières',                                             'VALIDATED',  0.0510);

INSERT INTO modification_impact (id, sheet_id, part_price, tef_amortization, tooling_amount)
VALUES
    (62, 62, 0.0420, 0.0000, NULL),       -- F001
    (63, 63, 0.0000, 0.0000, NULL),       -- F005 CANCELED
    (64, 64, 0.0500, 0.0000, NULL),       -- F007
    (65, 65, 0.0220, 0.0000, NULL),       -- F009
    (66, 66, 0.2470, 0.0000, NULL),       -- F024
    (67, 67, 0.0340, 0.0000, NULL),       -- F029
    (68, 68, 0.0000, 0.0400, 7500),       -- F032
    (69, 69, 0.0000, 0.0250, 13000),      -- F036
    (70, 70, 0.0510, 0.0000, NULL);       -- MAJ Mat

-- PF7 application matrix (2 refs × 9 sheets)
-- Refs: 24=PF7-1, 25=PF7-1b (PF7-2)
INSERT INTO modification_application (sheet_id, reference_id, applies) VALUES
    (62, 24, TRUE),  (62, 25, FALSE),  -- F001: PF7-1
    (63, 24, TRUE),  (63, 25, FALSE),  -- F005: PF7-1 (CANCELED)
    (64, 24, TRUE),  (64, 25, FALSE),  -- F007: PF7-1
    (65, 24, FALSE), (65, 25, TRUE),   -- F009: PF7-1b
    (66, 24, TRUE),  (66, 25, FALSE),  -- F024: PF7-1
    (67, 24, TRUE),  (67, 25, FALSE),  -- F029: PF7-1
    (68, 24, TRUE),  (68, 25, FALSE),  -- F032: PF7-1
    (69, 24, TRUE),  (69, 25, FALSE),  -- F036: PF7-1
    (70, 24, TRUE),  (70, 25, FALSE);  -- MAJ Mat: PF7-1

-- ============================================================
-- Update sequences to account for all new data
-- ============================================================
SELECT setval('product_family_id_seq', 10);
SELECT setval('product_reference_id_seq', 30);
SELECT setval('price_breakdown_id_seq', 30);
SELECT setval('modification_sheet_id_seq', 80);
SELECT setval('modification_impact_id_seq', 80);
SELECT setval('modification_application_id_seq', 500);
