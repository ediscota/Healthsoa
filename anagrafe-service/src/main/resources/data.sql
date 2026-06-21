-- INSERT IGNORE: idempotent on multiple instance startups (scaling).
-- Rows that already exist (duplicate unique key) are silently skipped.

INSERT IGNORE INTO `patient` (`fiscal_code`, `first_name`, `last_name`, `date_of_birth`, `gender`, `phone`) VALUES
('RSSMRA80A01H501Z', 'Mario',  'Rossi',    '1980-01-01', 'M', '3331234567'),
('BNCALE75B02F205X', 'Alessia','Bianchi',  '1975-02-02', 'F', '3477654321'),
('VRDLCA90C03G702Y', 'Luca',  'Verdi',    '1990-03-03', 'M', '3209876543');

INSERT IGNORE INTO `condition_entry` (`patient_id`, `icd10_code`, `description`, `onset_date`, `status`) VALUES
(1, 'I10',   'Ipertensione arteriosa essenziale',          '2015-06-10', 'ACTIVE'),
(1, 'E11',   'Diabete mellito di tipo 2',                  '2018-03-22', 'ACTIVE'),
(2, 'J45',   'Asma bronchiale allergica',                  '2000-09-15', 'ACTIVE'),
(2, 'K29.5', 'Gastrite cronica',                           '2019-11-01', 'RESOLVED'),
(3, 'M54.5', 'Lombalgia',                                  '2022-01-20', 'ACTIVE'),
(3, 'N20',   'Calcolosi renale',                           '2021-07-05', 'RESOLVED');

INSERT IGNORE INTO `allergy` (`patient_id`, `substance`, `reaction_severity`, `detected_date`) VALUES
(1, 'Penicillina',    'SEVERE',       '2010-04-12'),
(1, 'Aspirina',       'MILD',         '2016-08-30'),
(2, 'Polline',        'MILD',         '1998-04-01'),
(2, 'Amoxicillina',   'ANAPHYLACTIC', '2005-11-20'),
(3, 'Lattice',        'SEVERE',       '2017-02-14');
