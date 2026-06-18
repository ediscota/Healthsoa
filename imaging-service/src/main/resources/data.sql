-- Referti di imaging pre-esistenti (RIS/PACS simulato).
-- examType coincide con il panel code del Diagnostic Aggregator
-- così il filtro ?examType=PANEL_* è diretto senza mapping.
-- Almeno 2 referti per pannello, distribuiti tra pazienti 1, 2 e 3.

-- ── PANEL_RENAL (ecografia renale, urografia) ─────────────────────────────────

INSERT INTO `imaging_report` (`patient_id`, `exam_type`, `status`, `findings`, `conclusion`, `critical_flag`, `report_date`, `callback_url`)
VALUES
('1', 'PANEL_RENAL', 'COMPLETED',
 'Rene destro: dimensioni ai limiti superiori (13 cm), ecostruttura conservata. Rene sinistro nei limiti. Modesta idronefrosi destra di grado I.',
 'Idronefrosi destra di grado I. Controllo ecografico a 3 mesi.', false, '2026-05-10', NULL),

('2', 'PANEL_RENAL', 'COMPLETED',
 'Entrambi i reni di dimensioni e morfologia normali. Assenza di dilatazione delle vie urinarie. Vescica non distesa.',
 'Esame nella norma.', false, '2026-05-15', NULL),

('3', 'PANEL_RENAL', 'COMPLETED',
 'Rene sinistro: presenza di calcolo iperecogeno da 7 mm a livello del bacinetto con cono d''ombra posteriore. Rene destro nella norma.',
 'Nefrolitiasi sinistra. Urologia di riferimento per valutazione terapeutica.', true, '2026-06-01', NULL);

-- ── PANEL_METABOLIC (ecografia addominale, TAC addome) ───────────────────────

INSERT INTO `imaging_report` (`patient_id`, `exam_type`, `status`, `findings`, `conclusion`, `critical_flag`, `report_date`, `callback_url`)
VALUES
('1', 'PANEL_METABOLIC', 'COMPLETED',
 'Fegato di dimensioni aumentate (diametro longitudinale 18 cm), ecostruttura diffusamente iperecogena compatibile con steatosi epatica moderata. Colecisti distesa, pareti regolari. Pancreas non visualizzabile per interposizione di gas.',
 'Steatosi epatica moderata. Follow-up ecografico a 6 mesi.', false, '2026-04-22', NULL),

('2', 'PANEL_METABOLIC', 'COMPLETED',
 'Fegato di dimensioni nei limiti, struttura omogenea. Colecisti: multipli calcoli di piccole dimensioni (3-5 mm). Pancreas nella norma. Milza nei limiti.',
 'Colelitiasi multipla. Valutazione chirurgica consigliata.', false, '2026-05-20', NULL),

('3', 'PANEL_METABOLIC', 'COMPLETED',
 'Fegato nei limiti per dimensioni ed ecostruttura. Pancreas di aspetto regolare. Nessuna linfadenopatia addominale evidente.',
 'Esame nella norma.', false, '2026-06-03', NULL);

-- ── PANEL_CBC (radiografia torace, TAC torace) ───────────────────────────────

INSERT INTO `imaging_report` (`patient_id`, `exam_type`, `status`, `findings`, `conclusion`, `critical_flag`, `report_date`, `callback_url`)
VALUES
('1', 'PANEL_CBC', 'COMPLETED',
 'Opacità parailare destra compatibile con addensamento polmonare. Silhouette cardiaca nei limiti. Seni costofrenici liberi.',
 'Quadro radiologico suggestivo di polmonite lobare destra. Controllo dopo terapia.', true, '2026-05-10', NULL),

('2', 'PANEL_CBC', 'COMPLETED',
 'Parenchima polmonare nei limiti. Silhouette cardiaca nei limiti. Seni costofrenici liberi bilateralmente.',
 'Esame nella norma.', false, '2026-05-18', NULL),

('3', 'PANEL_CBC', 'COMPLETED',
 'Moderato versamento pleurico sinistro. Parenchima polmonare dx nei limiti. Ombra cardiaca ai limiti superiori della norma.',
 'Versamento pleurico sinistro di media entità. Rivalutazione clinica urgente.', true, '2026-06-02', NULL);
