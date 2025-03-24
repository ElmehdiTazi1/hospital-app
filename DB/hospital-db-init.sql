-- Script d'initialisation de la base de données pour l'application Hospital Management
-- Créé par Claude AI - Mars 2025

-- Suppression des tables si elles existent
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ligne_prescription;
DROP TABLE IF EXISTS prescription;
DROP TABLE IF EXISTS rendez_vous;
DROP TABLE IF EXISTS medecin;
DROP TABLE IF EXISTS medicament;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS departement;
SET FOREIGN_KEY_CHECKS = 1;

-- Création des tables
-- Table Departement
CREATE TABLE departement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    localisation VARCHAR(255),
    capacite_lits INTEGER,
    actif BOOLEAN DEFAULT TRUE,
    chef_departement_id BIGINT
);

-- Table Patient
CREATE TABLE patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(20) NOT NULL,
    date_naissance DATE,
    malade BOOLEAN DEFAULT FALSE,
    score INTEGER NOT NULL
);

-- Table Medecin
CREATE TABLE medecin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    specialite VARCHAR(255) NOT NULL,
    telephone VARCHAR(15),
    email VARCHAR(255),
    matricule VARCHAR(255) NOT NULL UNIQUE,
    departement_id BIGINT,
    disponible BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (departement_id) REFERENCES departement(id)
);

-- Ajout de la clé étrangère pour chef_departement_id dans departement
ALTER TABLE departement
ADD CONSTRAINT fk_chef_departement
FOREIGN KEY (chef_departement_id) REFERENCES medecin(id);

-- Table Medicament
CREATE TABLE medicament (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    dci VARCHAR(255) NOT NULL,
    laboratoire VARCHAR(255),
    dosage VARCHAR(255),
    forme VARCHAR(255),
    date_expiration DATE,
    quantite_stock INTEGER NOT NULL,
    seuil_alerte INTEGER,
    prix DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    contre_indications TEXT
);

-- Table RendezVous
CREATE TABLE rendez_vous (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_heure DATETIME NOT NULL,
    patient_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,
    motif VARCHAR(255),
    duree INTEGER DEFAULT 30,
    statut VARCHAR(20) NOT NULL,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
);

-- Table Prescription
CREATE TABLE prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_prescription DATE NOT NULL,
    patient_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,
    duree_validite INTEGER DEFAULT 30,
    statut VARCHAR(20) NOT NULL,
    observations TEXT,
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (medecin_id) REFERENCES medecin(id)
);

-- Table LignePrescription
CREATE TABLE ligne_prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicament_id BIGINT NOT NULL,
    posologie VARCHAR(255) NOT NULL,
    duree_traitement INTEGER,
    instructions TEXT,
    moment_prise VARCHAR(20),
    quantite INTEGER,
    substitution_autorisee BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (prescription_id) REFERENCES prescription(id) ON DELETE CASCADE,
    FOREIGN KEY (medicament_id) REFERENCES medicament(id)
);

-- Insertion des données de test
-- Départements
INSERT INTO departement (nom, description, localisation, capacite_lits, actif) VALUES
('Cardiologie', 'Département spécialisé dans les pathologies cardiaques', 'Bâtiment A, 3ème étage', 30, true),
('Neurologie', 'Département spécialisé dans les pathologies du système nerveux', 'Bâtiment B, 2ème étage', 25, true),
('Pédiatrie', 'Département dédié aux soins des enfants', 'Bâtiment C, Rez-de-chaussée', 40, true),
('Orthopédie', 'Département spécialisé dans les pathologies osseuses et articulaires', 'Bâtiment A, 1er étage', 20, true),
('Oncologie', 'Département dédié au traitement du cancer', 'Bâtiment D, 4ème étage', 35, true),
('Pneumologie', 'Département spécialisé dans les pathologies respiratoires', 'Bâtiment B, 3ème étage', 15, true),
('Psychiatrie', 'Département dédié aux soins de santé mentale', 'Bâtiment E, 1er étage', 30, true),
('Dermatologie', 'Département spécialisé dans les affections cutanées', 'Bâtiment C, 2ème étage', 10, true),
('Ophtalmologie', 'Département spécialisé dans les pathologies oculaires', 'Bâtiment D, 1er étage', 15, true),
('Urgences', 'Service d\'accueil des urgences médicales', 'Bâtiment A, Rez-de-chaussée', 50, true);

-- Patients avec des pathologies diverses et âges variés
INSERT INTO patient (nom, date_naissance, malade, score) VALUES
('Jean Dupont', '1975-05-15', true, 150),
('Marie Martin', '1980-12-10', false, 180),
('Pierre Leclerc', '1955-08-22', true, 110),
('Sophie Bernard', '1990-03-30', false, 200),
('Ahmed Alami', '1965-11-05', true, 125),
('Fatima Benani', '1988-07-17', false, 170),
('Robert Garcia', '1950-02-28', true, 105),
('Nadia Roux', '1972-09-14', true, 115),
('Laurent Petit', '1985-06-25', false, 190),
('Samira Haddad', '1978-04-03', true, 130),
('Julie Moreau', '1992-01-20', false, 175),
('Mohammed Idrissi', '1960-10-12', true, 120),
('Isabelle Simon', '1982-08-08', false, 185),
('Thomas Leroy', '1970-03-15', true, 135),
('Laila Amrani', '1995-07-30', false, 195);

-- Médecins avec différentes spécialités
INSERT INTO medecin (nom, prenom, specialite, telephone, email, matricule, departement_id, disponible) VALUES
('Dubois', 'Alexandre', 'Cardiologie', '+33654872145', 'a.dubois@hospital.org', 'MED001', 1, true),
('Benkirane', 'Samia', 'Neurologie', '+33698541236', 's.benkirane@hospital.org', 'MED002', 2, true),
('Martin', 'François', 'Pédiatrie', '+33612457896', 'f.martin@hospital.org', 'MED003', 3, true),
('Delarue', 'Céline', 'Orthopédie', '+33687459632', 'c.delarue@hospital.org', 'MED004', 4, true),
('El Fassi', 'Karim', 'Oncologie', '+33645781236', 'k.elfassi@hospital.org', 'MED005', 5, true),
('Richard', 'Sophie', 'Pneumologie', '+33698754123', 's.richard@hospital.org', 'MED006', 6, true),
('Bouchard', 'Antoine', 'Psychiatrie', '+33654123698', 'a.bouchard@hospital.org', 'MED007', 7, true),
('Ziani', 'Leila', 'Dermatologie', '+33612369874', 'l.ziani@hospital.org', 'MED008', 8, true),
('Dupuis', 'Marc', 'Ophtalmologie', '+33645781239', 'm.dupuis@hospital.org', 'MED009', 9, true),
('Lemaire', 'Hélène', 'Neurologie', '+33654789632', 'h.lemaire@hospital.org', 'MED010', 2, true),
('Berrada', 'Youssef', 'Cardiologie', '+33612587496', 'y.berrada@hospital.org', 'MED011', 1, true),
('Fabre', 'Isabelle', 'Pédiatrie', '+33698523147', 'i.fabre@hospital.org', 'MED012', 3, true),
('Laval', 'Philippe', 'Urgences', '+33654789513', 'p.laval@hospital.org', 'MED013', 10, true),
('Moussaoui', 'Amina', 'Urgences', '+33612453698', 'a.moussaoui@hospital.org', 'MED014', 10, true),
('Girard', 'Mathieu', 'Orthopédie', '+33698514723', 'm.girard@hospital.org', 'MED015', 4, false);

-- Assigner des chefs de département
UPDATE departement SET chef_departement_id = 1 WHERE id = 1; -- Dubois en Cardiologie
UPDATE departement SET chef_departement_id = 2 WHERE id = 2; -- Benkirane en Neurologie
UPDATE departement SET chef_departement_id = 3 WHERE id = 3; -- Martin en Pédiatrie
UPDATE departement SET chef_departement_id = 4 WHERE id = 4; -- Delarue en Orthopédie
UPDATE departement SET chef_departement_id = 5 WHERE id = 5; -- El Fassi en Oncologie
UPDATE departement SET chef_departement_id = 6 WHERE id = 6; -- Richard en Pneumologie
UPDATE departement SET chef_departement_id = 7 WHERE id = 7; -- Bouchard en Psychiatrie
UPDATE departement SET chef_departement_id = 8 WHERE id = 8; -- Ziani en Dermatologie
UPDATE departement SET chef_departement_id = 9 WHERE id = 9; -- Dupuis en Ophtalmologie
UPDATE departement SET chef_departement_id = 13 WHERE id = 10; -- Laval aux Urgences

-- Médicaments variés
INSERT INTO medicament (nom, dci, laboratoire, dosage, forme, date_expiration, quantite_stock, seuil_alerte, prix, disponible, contre_indications) VALUES
('Doliprane', 'Paracétamol', 'Sanofi', '500mg', 'Comprimé', '2026-06-30', 500, 50, 2.50, true, 'Ne pas dépasser la dose prescrite. Ne pas utiliser en cas d\'allergie au paracétamol.'),
('Advil', 'Ibuprofène', 'Pfizer', '200mg', 'Comprimé', '2026-05-15', 300, 30, 3.20, true, 'Ne pas utiliser en cas d\'ulcère gastrique, de grossesse ou d\'allergie aux AINS.'),
('Levothyrox', 'Lévothyroxine', 'Merck', '75μg', 'Comprimé', '2027-03-10', 100, 20, 4.80, true, 'Déconseillé en cas d\'infarctus récent. À prendre à jeun.'),
('Amoxicilline', 'Amoxicilline', 'Biogaran', '1g', 'Comprimé', '2025-11-25', 150, 20, 5.60, true, 'Contre-indiqué en cas d\'allergie aux pénicillines.'),
('Ventoline', 'Salbutamol', 'GlaxoSmithKline', '100μg/dose', 'Aérosol', '2026-08-20', 80, 15, 4.50, true, 'À utiliser avec précaution en cas de troubles cardiaques.'),
('Kardégic', 'Acide acétylsalicylique', 'Sanofi', '75mg', 'Sachet-dose', '2026-02-18', 200, 30, 3.90, true, 'Ne pas utiliser en cas d\'ulcère gastroduodénal évolutif ou d\'hémophilie.'),
('Augmentin', 'Amoxicilline/Acide clavulanique', 'GlaxoSmithKline', '500mg/62.5mg', 'Comprimé', '2025-07-12', 120, 25, 7.20, true, 'Contre-indiqué en cas d\'allergie aux pénicillines ou d\'antécédent d\'hépatite.'),
('Xanax', 'Alprazolam', 'Pfizer', '0.25mg', 'Comprimé', '2026-04-05', 50, 10, 8.30, true, 'Risque de dépendance. Effets sédatifs. Ne pas associer avec de l\'alcool.'),
('Lasilix', 'Furosémide', 'Sanofi', '40mg', 'Comprimé', '2026-09-30', 180, 30, 2.80, true, 'Surveillance de la kaliémie recommandée. Risque de déshydratation.'),
('Tahor', 'Atorvastatine', 'Pfizer', '20mg', 'Comprimé', '2027-01-25', 240, 40, 15.90, true, 'Risque de myalgies. Surveillance du bilan hépatique conseillée.'),
('Lévémir', 'Insuline détémir', 'Novo Nordisk', '100UI/ml', 'Solution injectable', '2025-05-15', 15, 5, 42.60, true, 'Risque d\'hypoglycémie. Nécessite une surveillance glycémique régulière.'),
('Stagid', 'Metformine', 'Merck', '700mg', 'Comprimé', '2026-11-10', 350, 50, 3.10, true, 'Contre-indiqué en cas d\'insuffisance rénale sévère. Arrêt temporaire avant examens radiologiques avec produits de contraste iodés.'),
('Spasfon', 'Phloroglucinol', 'Teva', '80mg', 'Comprimé', '2027-02-28', 400, 50, 2.90, true, 'Généralement bien toléré. Pas de contre-indication majeure connue.'),
('Inexium', 'Esoméprazole', 'AstraZeneca', '20mg', 'Comprimé', '2026-10-15', 120, 20, 7.50, true, 'Déconseillé en cas d\'ostéoporose sur traitement prolongé.'),
('Aerius', 'Desloratadine', 'MSD', '5mg', 'Comprimé', '2025-12-05', 90, 15, 6.20, true, 'Prudence en cas d\'insuffisance rénale sévère.');

-- Rendez-vous avec différents statuts
INSERT INTO rendez_vous (date_heure, patient_id, medecin_id, motif, duree, statut, notes) VALUES
('2025-04-10 09:00:00', 1, 1, 'Consultation de suivi cardiaque', 30, 'PLANIFIE', 'Patient avec antécédents d\'infarctus'),
('2025-04-12 14:30:00', 2, 3, 'Consultation pédiatrique pour son enfant', 45, 'CONFIRME', 'L\'enfant présente une fièvre persistante depuis 3 jours'),
('2025-04-05 11:15:00', 3, 2, 'Troubles neurologiques', 60, 'TERMINE', 'Le patient se plaint de maux de tête fréquents et de vertiges'),
('2025-03-28 16:00:00', 4, 5, 'Suivi traitement oncologique', 30, 'TERMINE', 'Résultats d\'analyses à examiner'),
('2025-04-15 10:45:00', 5, 4, 'Douleurs articulaires', 30, 'PLANIFIE', 'Suspicion d\'arthrose'),
('2025-04-08 13:00:00', 6, 6, 'Difficultés respiratoires', 45, 'ANNULE', 'Patiente hospitalisée en urgence pour détresse respiratoire'),
('2025-04-20 15:30:00', 7, 7, 'Consultation de suivi psychiatrique', 60, 'PLANIFIE', 'Évaluation du traitement antidépresseur'),
('2025-04-18 09:15:00', 8, 8, 'Lésion cutanée', 30, 'CONFIRME', 'Examen d\'une tache suspecte au niveau du bras'),
('2025-04-25 11:30:00', 9, 9, 'Baisse d\'acuité visuelle', 45, 'PLANIFIE', 'Suspicion de cataracte'),
('2025-04-02 14:00:00', 10, 10, 'Consultation neurologique', 60, 'TERMINE', 'Évaluation des troubles de l\'équilibre'),
('2025-04-30 16:45:00', 11, 11, 'Douleurs thoraciques', 30, 'PLANIFIE', 'ECG à réaliser'),
('2025-04-22 10:00:00', 12, 12, 'Consultation pédiatrique', 45, 'CONFIRME', 'Suivi de croissance'),
('2025-04-11 08:30:00', 13, 13, 'Douleurs abdominales', 30, 'TERMINE', 'Bilan sanguin complet à réaliser'),
('2025-04-28 13:15:00', 14, 14, 'Céphalées persistantes', 45, 'PLANIFIE', 'Patient déjà sous traitement antalgique'),
('2025-05-05 11:00:00', 15, 15, 'Douleur au genou suite à un accident', 30, 'PLANIFIE', 'Radiographie à vérifier');

-- Prescriptions
INSERT INTO prescription (date_prescription, patient_id, medecin_id, duree_validite, statut, observations) VALUES
('2025-03-20', 1, 1, 30, 'ACTIVE', 'Traitement préventif post-infarctus. Régime pauvre en sel conseillé.'),
('2025-03-15', 3, 2, 60, 'ACTIVE', 'Traitement pour migraines chroniques. Éviter les facteurs déclenchants (stress, alcool).'),
('2025-03-10', 5, 4, 15, 'TERMINEE', 'Anti-inflammatoires pour arthrose. Prévoir séances de kinésithérapie.'),
('2025-03-25', 7, 7, 90, 'ACTIVE', 'Traitement antidépresseur. Suivi psychothérapeutique recommandé parallèlement.'),
('2025-03-05', 9, 9, 30, 'TERMINEE', 'Collyres pour glaucome. Prévoir contrôle tension oculaire dans 1 mois.'),
('2025-03-18', 10, 10, 45, 'ACTIVE', 'Traitement pour vertiges positionnels. Exercices de rééducation vestibulaire à réaliser quotidiennement.'),
('2025-03-22', 11, 11, 30, 'ACTIVE', 'Traitement préventif de l\'angine de poitrine. Surveiller la tension artérielle.'),
('2025-03-12', 12, 3, 10, 'TERMINEE', 'Antibiotiques pour otite. Consulter si fièvre persiste au-delà de 3 jours.'),
('2025-03-27', 13, 13, 7, 'ACTIVE', 'Antispasmodiques pour syndrome du côlon irritable. Alimentation pauvre en FODMAP recommandée.'),
('2025-03-08', 14, 2, 30, 'ACTIVE', 'Traitement prophylactique des migraines. Tenir un journal des crises.'),
('2025-03-16', 8, 8, 14, 'TERMINEE', 'Traitement topique pour eczéma. Éviter les savons irritants.'),
('2025-03-23', 6, 6, 21, 'ANNULEE', 'Traitement pour rhinite allergique. Suite à hospitalisation, prescription modifiée.'),
('2025-03-19', 2, 3, 5, 'TERMINEE', 'Antipyrétiques pour fièvre. Hydratation importante recommandée.'),
('2025-03-26', 4, 5, 180, 'ACTIVE', 'Traitement de maintenance post-chimiothérapie. Surveillance biologique mensuelle.'),
('2025-03-29', 15, 4, 14, 'ACTIVE', 'Analgésiques pour traumatisme du genou. Repos sportif 3 semaines minimum.');

-- Lignes de prescription
INSERT INTO ligne_prescription (prescription_id, medicament_id, posologie, duree_traitement, instructions, moment_prise, quantite, substitution_autorisee) VALUES
(1, 6, '1 sachet par jour', 30, 'À prendre le matin à jeun avec un grand verre d\'eau', 'AVANT_REPAS', 30, true),
(1, 10, '1 comprimé par jour', 30, 'À prendre le soir au coucher', 'APRES_REPAS', 30, true),
(2, 1, '1 comprimé toutes les 6 heures si douleur', 15, 'Ne pas dépasser 4 comprimés par jour', 'INDIFFERENT', 60, true),
(2, 8, '0.25mg au coucher si anxiété', 30, 'Réduire progressivement la dose', 'APRES_REPAS', 30, false),
(3, 2, '1 comprimé 3 fois par jour', 10, 'À prendre pendant les repas', 'PENDANT_REPAS', 30, true),
(4, 8, '0.5mg matin et soir', 90, 'Ne pas interrompre le traitement brutalement', 'APRES_REPAS', 180, false),
(5, 14, '1 comprimé par jour', 30, 'À prendre avant le petit-déjeuner', 'AVANT_REPAS', 30, true),
(6, 1, '1 comprimé si vertige', 15, 'Maximum 3 comprimés par jour', 'INDIFFERENT', 45, true),
(7, 6, '1 sachet par jour', 30, 'À prendre le matin', 'AVANT_REPAS', 30, true),
(7, 10, '1 comprimé par jour', 30, 'À prendre le soir', 'APRES_REPAS', 30, true),
(8, 4, '1 comprimé matin et soir pendant 5 jours', 5, 'À prendre pendant les repas', 'PENDANT_REPAS', 10, false),
(9, 13, '1 comprimé avant les 3 principaux repas', 7, 'Prise 15 minutes avant le repas', 'AVANT_REPAS', 21, true),
(10, 15, '1 comprimé par jour', 30, 'À prendre le soir', 'APRES_REPAS', 30, true),
(11, 8, 'Application locale 2 fois par jour', 14, 'Appliquer sur les zones affectées', 'INDIFFERENT', 1, true),
(12, 15, '1 comprimé par jour', 21, 'À prendre le matin', 'AVANT_REPAS', 21, true),
(13, 1, '1 comprimé toutes les 6 heures si fièvre', 5, 'Ne pas dépasser 4 comprimés par jour', 'INDIFFERENT', 20, true),
(14, 10, '1 comprimé par jour', 180, 'À prendre le soir', 'APRES_REPAS', 180, false),
(15, 2, '1 comprimé 3 fois par jour pendant 7 jours', 7, 'À prendre pendant les repas', 'PENDANT_REPAS', 21, true),
(15, 1, '1 comprimé toutes les 6 heures si douleur', 14, 'Ne pas dépasser 4 comprimés par jour', 'INDIFFERENT', 56, true);