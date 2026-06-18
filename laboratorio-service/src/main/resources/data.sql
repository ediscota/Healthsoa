-- Dati iniziali per il Laboratorio Service.
-- Tre pazienti con 2 ordini COMPLETED ciascuno, misurazioni distinte e realistiche.
-- P001: creatinina molto elevata → attiva regole di rischio in UC-1.
-- P002: valori nella norma.
-- P003: anomalie leggere (borderline).
-- INSERT IGNORE: idempotente su più istanze (scaling), non causa errori di duplicate key.

-- ──────────────────────────────────────────────────────────────────────────────
-- PAZIENTE P001 — funzionalità renale compromessa
-- ──────────────────────────────────────────────────────────────────────────────

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (1, '1', 'PANEL_RENAL', 'COMPLETED', NULL, '2026-06-01 08:00:00', '2026-06-01 08:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (1, 'Creatinina',  3.5,   'mg/dL',   '0.6-1.2',       true),
  (1, 'Azotemia',    72.0,  'mg/dL',   '10-50',          true),
  (1, 'Sodio',       138.0, 'mEq/L',   '136-145',        false);

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (2, '1', 'PANEL_CBC', 'COMPLETED', NULL, '2026-06-01 09:00:00', '2026-06-01 09:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (2, 'Emoglobina',  11.2,     'g/dL',   '13.5-17.5',      true),
  (2, 'Leucociti',   6800.0,   'cel/uL', '4000-10000',      false),
  (2, 'Piastrine',   190000.0, 'cel/uL', '150000-400000',   false);

-- ──────────────────────────────────────────────────────────────────────────────
-- PAZIENTE P002 — valori nella norma
-- ──────────────────────────────────────────────────────────────────────────────

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (3, '2', 'PANEL_RENAL', 'COMPLETED', NULL, '2026-06-02 08:00:00', '2026-06-02 08:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (3, 'Creatinina',  0.9,   'mg/dL',   '0.6-1.2',       false),
  (3, 'Azotemia',    28.0,  'mg/dL',   '10-50',          false),
  (3, 'Sodio',       141.0, 'mEq/L',   '136-145',        false);

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (4, '2', 'PANEL_METABOLIC', 'COMPLETED', NULL, '2026-06-02 09:00:00', '2026-06-02 09:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (4, 'Glicemia',      92.0,  'mg/dL',   '70-100',   false),
  (4, 'Colesterolo',  178.0,  'mg/dL',   '0-200',    false),
  (4, 'Trigliceridi', 118.0,  'mg/dL',   '0-150',    false);

-- ──────────────────────────────────────────────────────────────────────────────
-- PAZIENTE P003 — anomalie borderline
-- ──────────────────────────────────────────────────────────────────────────────

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (5, '3', 'PANEL_RENAL', 'COMPLETED', NULL, '2026-06-03 08:00:00', '2026-06-03 08:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (5, 'Creatinina',  1.4,   'mg/dL',   '0.6-1.2',       true),
  (5, 'Azotemia',    54.0,  'mg/dL',   '10-50',          true),
  (5, 'Sodio',       135.0, 'mEq/L',   '136-145',        true);

INSERT IGNORE INTO `test_order` (`id`, `patient_id`, `exam_code`, `status`, `callback_url`, `created_at`, `updated_at`)
VALUES (6, '3', 'PANEL_CBC', 'COMPLETED', NULL, '2026-06-03 09:00:00', '2026-06-03 09:08:00');

INSERT IGNORE INTO `measurement` (`order_id`, `parameter`, `value`, `unit`, `reference_range`, `anomaly_flag`)
VALUES
  (6, 'Emoglobina',  14.8,     'g/dL',   '13.5-17.5',      false),
  (6, 'Leucociti',   11200.0,  'cel/uL', '4000-10000',      true),
  (6, 'Piastrine',   305000.0, 'cel/uL', '150000-400000',   false);
