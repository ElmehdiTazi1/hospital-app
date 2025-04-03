package org.mql.hospital.service;
import lombok.AllArgsConstructor;
import org.mql.hospital.entities.*;
import org.mql.hospital.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Configuration
@AllArgsConstructor
public class DataInitializer {

    private  PatientRepository patientRepository;
    private  MedecinRepository medecinRepository;
    private  DepartementRepository departementRepository;
    private  MedicamentRepository medicamentRepository;
    private  RendezVousRepository rendezVousRepository;
    private  PrescriptionRepository prescriptionRepository;
    private  LignePrescriptionRepository lignePrescriptionRepository;
    private  RoleRepository roleRepository;
    private UserRepository userRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    @Transactional
    public CommandLineRunner initData() {
        return args -> {
            // Nettoyer la base de données
            lignePrescriptionRepository.deleteAll();
            prescriptionRepository.deleteAll();
            rendezVousRepository.deleteAll();
            medicamentRepository.deleteAll();
            medecinRepository.deleteAll();
            departementRepository.deleteAll();
            patientRepository.deleteAll();
            // Initialiser les rôles
            Role roleAdmin = createRole("ROLE_ADMIN", "Administrateur du système");
            Role roleMedecin = createRole("ROLE_MEDECIN", "Médecin de l'hôpital");
            Role rolePatient = createRole("ROLE_PATIENT", "Patient de l'hôpital");
            // Initialiser les départements
            Departement cardio = createDepartement("Cardiologie", "Département spécialisé dans les pathologies cardiaques", "Bâtiment A, 3ème étage", 30, true);
            Departement neuro = createDepartement("Neurologie", "Département spécialisé dans les pathologies du système nerveux", "Bâtiment B, 2ème étage", 25, true);
            Departement pediatrie = createDepartement("Pédiatrie", "Département dédié aux soins des enfants", "Bâtiment C, Rez-de-chaussée", 40, true);
            Departement ortho = createDepartement("Orthopédie", "Département spécialisé dans les pathologies osseuses et articulaires", "Bâtiment A, 1er étage", 20, true);
            Departement onco = createDepartement("Oncologie", "Département dédié au traitement du cancer", "Bâtiment D, 4ème étage", 35, true);
            Departement pneumo = createDepartement("Pneumologie", "Département spécialisé dans les pathologies respiratoires", "Bâtiment B, 3ème étage", 15, true);
            Departement psy = createDepartement("Psychiatrie", "Département dédié aux soins de santé mentale", "Bâtiment E, 1er étage", 30, true);
            Departement dermato = createDepartement("Dermatologie", "Département spécialisé dans les affections cutanées", "Bâtiment C, 2ème étage", 10, true);
            Departement ophtalmo = createDepartement("Ophtalmologie", "Département spécialisé dans les pathologies oculaires", "Bâtiment D, 1er étage", 15, true);
            Departement urgences = createDepartement("Urgences", "Service d'accueil des urgences médicales", "Bâtiment A, Rez-de-chaussée", 50, true);

            // Initialiser les patients avec comptes utilisateurs
            Patient patient1 = createPatient("Jean Dupont", "1975-05-15", true, 150, createUser("jdupont", "jean.dupont@example.com", "password", rolePatient));
            Patient patient2 = createPatient("Marie Martin", "1980-12-10", false, 180, createUser("mmartin", "marie.martin@example.com", "password", rolePatient));
            Patient patient3 = createPatient("Pierre Leclerc", "1955-08-22", true, 110, createUser("pleclerc", "pierre.leclerc@example.com", "password", rolePatient));
            Patient patient4 = createPatient("Sophie Bernard", "1990-03-30", false, 200, createUser("sbernard", "sophie.bernard@example.com", "password", rolePatient));
            Patient patient5 = createPatient("Ahmed Alami", "1965-11-05", true, 125, createUser("aalami", "ahmed.alami@example.com", "password", rolePatient));
            // Patients sans comptes utilisateurs (pour tester la création de comptes par l'admin)
            Patient patient6 = createPatient("Fatima Benani", "1988-07-17", false, 170, null);
            Patient patient7 = createPatient("Robert Garcia", "1950-02-28", true, 105, null);
            Patient patient8 = createPatient("Nadia Roux", "1972-09-14", true, 115, null);
            Patient patient9 = createPatient("Laurent Petit", "1985-06-25", false, 190, null);
            Patient patient10 = createPatient("Samira Haddad", "1978-04-03", true, 130, null);
            Patient patient11 = createPatient("Julie Moreau", "1992-01-20", false, 175, null);
            Patient patient12 = createPatient("Mohammed Idrissi", "1960-10-12", true, 120, null);
            Patient patient13 = createPatient("Isabelle Simon", "1982-08-08", false, 185, null);
            Patient patient14 = createPatient("Thomas Leroy", "1970-03-15", true, 135, null);
            Patient patient15 = createPatient("Laila Amrani", "1995-07-30", false, 195, null);

            // Initialiser les médecins avec comptes utilisateurs
            Medecin medecin1 = createMedecin("Dubois", "Alexandre", "Cardiologie", "+33654872145", "a.dubois@hospital.org", "MED001", cardio, true, createUser("adubois", "a.dubois@hospital.org", "password", roleMedecin));
            Medecin medecin2 = createMedecin("Benkirane", "Samia", "Neurologie", "+33698541236", "s.benkirane@hospital.org", "MED002", neuro, true, createUser("sbenkirane", "s.benkirane@hospital.org", "password", roleMedecin));
            // Médecins sans comptes utilisateurs (pour tester la création de comptes par l'admin)
            Medecin medecin3 = createMedecin("Martin", "François", "Pédiatrie", "+33612457896", "f.martin@hospital.org", "MED003", pediatrie, true, null);
            Medecin medecin4 = createMedecin("Delarue", "Céline", "Orthopédie", "+33687459632", "c.delarue@hospital.org", "MED004", ortho, true, null);
            Medecin medecin5 = createMedecin("El Fassi", "Karim", "Oncologie", "+33645781236", "k.elfassi@hospital.org", "MED005", onco, true, null);
            Medecin medecin6 = createMedecin("Richard", "Sophie", "Pneumologie", "+33698754123", "s.richard@hospital.org", "MED006", pneumo, true, null);
            Medecin medecin7 = createMedecin("Bouchard", "Antoine", "Psychiatrie", "+33654123698", "a.bouchard@hospital.org", "MED007", psy, true, null);
            Medecin medecin8 = createMedecin("Ziani", "Leila", "Dermatologie", "+33612369874", "l.ziani@hospital.org", "MED008", dermato, true, null);
            Medecin medecin9 = createMedecin("Dupuis", "Marc", "Ophtalmologie", "+33645781239", "m.dupuis@hospital.org", "MED009", ophtalmo, true, null);
            Medecin medecin10 = createMedecin("Lemaire", "Hélène", "Neurologie", "+33654789632", "h.lemaire@hospital.org", "MED010", neuro, true, null);
            Medecin medecin11 = createMedecin("Berrada", "Youssef", "Cardiologie", "+33612587496", "y.berrada@hospital.org", "MED011", cardio, true, null);
            Medecin medecin12 = createMedecin("Fabre", "Isabelle", "Pédiatrie", "+33698523147", "i.fabre@hospital.org", "MED012", pediatrie, true, null);
            Medecin medecin13 = createMedecin("Laval", "Philippe", "Urgences", "+33654789513", "p.laval@hospital.org", "MED013", urgences, true, null);
            Medecin medecin14 = createMedecin("Moussaoui", "Amina", "Urgences", "+33612453698", "a.moussaoui@hospital.org", "MED014", urgences, true, null);
            Medecin medecin15 = createMedecin("Girard", "Mathieu", "Orthopédie", "+33698514723", "m.girard@hospital.org", "MED015", ortho, false, null);

            // Assigner les chefs de département
            assignChefDepartement(cardio, medecin1);
            assignChefDepartement(neuro, medecin2);
            assignChefDepartement(pediatrie, medecin3);
            assignChefDepartement(ortho, medecin4);
            assignChefDepartement(onco, medecin5);
            assignChefDepartement(pneumo, medecin6);
            assignChefDepartement(psy, medecin7);
            assignChefDepartement(dermato, medecin8);
            assignChefDepartement(ophtalmo, medecin9);
            assignChefDepartement(urgences, medecin13);

            // Initialiser les médicaments
            Medicament med1 = createMedicament("Doliprane", "Paracétamol", "Sanofi", "500mg", "Comprimé", "2026-06-30", 500, 50, 2.50, true, "Ne pas dépasser la dose prescrite. Ne pas utiliser en cas d'allergie au paracétamol.");
            Medicament med2 = createMedicament("Advil", "Ibuprofène", "Pfizer", "200mg", "Comprimé", "2026-05-15", 300, 30, 3.20, true, "Ne pas utiliser en cas d'ulcère gastrique, de grossesse ou d'allergie aux AINS.");
            Medicament med3 = createMedicament("Levothyrox", "Lévothyroxine", "Merck", "75μg", "Comprimé", "2027-03-10", 100, 20, 4.80, true, "Déconseillé en cas d'infarctus récent. À prendre à jeun.");
            Medicament med4 = createMedicament("Amoxicilline", "Amoxicilline", "Biogaran", "1g", "Comprimé", "2025-11-25", 150, 20, 5.60, true, "Contre-indiqué en cas d'allergie aux pénicillines.");
            Medicament med5 = createMedicament("Ventoline", "Salbutamol", "GlaxoSmithKline", "100μg/dose", "Aérosol", "2026-08-20", 80, 15, 4.50, true, "À utiliser avec précaution en cas de troubles cardiaques.");
            Medicament med6 = createMedicament("Kardégic", "Acide acétylsalicylique", "Sanofi", "75mg", "Sachet-dose", "2026-02-18", 200, 30, 3.90, true, "Ne pas utiliser en cas d'ulcère gastroduodénal évolutif ou d'hémophilie.");
            Medicament med7 = createMedicament("Augmentin", "Amoxicilline/Acide clavulanique", "GlaxoSmithKline", "500mg/62.5mg", "Comprimé", "2025-07-12", 120, 25, 7.20, true, "Contre-indiqué en cas d'allergie aux pénicillines ou d'antécédent d'hépatite.");
            Medicament med8 = createMedicament("Xanax", "Alprazolam", "Pfizer", "0.25mg", "Comprimé", "2026-04-05", 50, 10, 8.30, true, "Risque de dépendance. Effets sédatifs. Ne pas associer avec de l'alcool.");
            Medicament med9 = createMedicament("Lasilix", "Furosémide", "Sanofi", "40mg", "Comprimé", "2026-09-30", 180, 30, 2.80, true, "Surveillance de la kaliémie recommandée. Risque de déshydratation.");
            Medicament med10 = createMedicament("Tahor", "Atorvastatine", "Pfizer", "20mg", "Comprimé", "2027-01-25", 240, 40, 15.90, true, "Risque de myalgies. Surveillance du bilan hépatique conseillée.");
            Medicament med11 = createMedicament("Lévémir", "Insuline détémir", "Novo Nordisk", "100UI/ml", "Solution injectable", "2025-05-15", 15, 5, 42.60, true, "Risque d'hypoglycémie. Nécessite une surveillance glycémique régulière.");
            Medicament med12 = createMedicament("Stagid", "Metformine", "Merck", "700mg", "Comprimé", "2026-11-10", 350, 50, 3.10, true, "Contre-indiqué en cas d'insuffisance rénale sévère.");
            Medicament med13 = createMedicament("Spasfon", "Phloroglucinol", "Teva", "80mg", "Comprimé", "2027-02-28", 400, 50, 2.90, true, "Généralement bien toléré. Pas de contre-indication majeure connue.");
            Medicament med14 = createMedicament("Inexium", "Esoméprazole", "AstraZeneca", "20mg", "Comprimé", "2026-10-15", 120, 20, 7.50, true, "Déconseillé en cas d'ostéoporose sur traitement prolongé.");
            Medicament med15 = createMedicament("Aerius", "Desloratadine", "MSD", "5mg", "Comprimé", "2025-12-05", 90, 15, 6.20, true, "Prudence en cas d'insuffisance rénale sévère.");

            // Initialiser les rendez-vous
            createRendezVous("2025-04-10 09:00:00", patient1, medecin1, "Consultation de suivi cardiaque", 30, RendezVous.StatutRendezVous.PLANIFIE, "Patient avec antécédents d'infarctus");
            createRendezVous("2025-04-12 14:30:00", patient2, medecin3, "Consultation pédiatrique pour son enfant", 45, RendezVous.StatutRendezVous.CONFIRME, "L'enfant présente une fièvre persistante depuis 3 jours");
            createRendezVous("2025-04-05 11:15:00", patient3, medecin2, "Troubles neurologiques", 60, RendezVous.StatutRendezVous.TERMINE, "Le patient se plaint de maux de tête fréquents et de vertiges");
            createRendezVous("2025-03-28 16:00:00", patient4, medecin5, "Suivi traitement oncologique", 30, RendezVous.StatutRendezVous.TERMINE, "Résultats d'analyses à examiner");
            createRendezVous("2025-04-15 10:45:00", patient5, medecin4, "Douleurs articulaires", 30, RendezVous.StatutRendezVous.PLANIFIE, "Suspicion d'arthrose");
            createRendezVous("2025-04-08 13:00:00", patient6, medecin6, "Difficultés respiratoires", 45, RendezVous.StatutRendezVous.ANNULE, "Patiente hospitalisée en urgence pour détresse respiratoire");
            createRendezVous("2025-04-20 15:30:00", patient7, medecin7, "Consultation de suivi psychiatrique", 60, RendezVous.StatutRendezVous.PLANIFIE, "Évaluation du traitement antidépresseur");
            createRendezVous("2025-04-18 09:15:00", patient8, medecin8, "Lésion cutanée", 30, RendezVous.StatutRendezVous.CONFIRME, "Examen d'une tache suspecte au niveau du bras");
            createRendezVous("2025-04-25 11:30:00", patient9, medecin9, "Baisse d'acuité visuelle", 45, RendezVous.StatutRendezVous.PLANIFIE, "Suspicion de cataracte");
            createRendezVous("2025-04-02 14:00:00", patient10, medecin10, "Consultation neurologique", 60, RendezVous.StatutRendezVous.TERMINE, "Évaluation des troubles de l'équilibre");
            createRendezVous("2025-04-30 16:45:00", patient11, medecin11, "Douleurs thoraciques", 30, RendezVous.StatutRendezVous.PLANIFIE, "ECG à réaliser");
            createRendezVous("2025-04-22 10:00:00", patient12, medecin12, "Consultation pédiatrique", 45, RendezVous.StatutRendezVous.CONFIRME, "Suivi de croissance");
            createRendezVous("2025-04-11 08:30:00", patient13, medecin13, "Douleurs abdominales", 30, RendezVous.StatutRendezVous.TERMINE, "Bilan sanguin complet à réaliser");
            createRendezVous("2025-04-28 13:15:00", patient14, medecin14, "Céphalées persistantes", 45, RendezVous.StatutRendezVous.PLANIFIE, "Patient déjà sous traitement antalgique");
            createRendezVous("2025-05-05 11:00:00", patient15, medecin15, "Douleur au genou suite à un accident", 30, RendezVous.StatutRendezVous.PLANIFIE, "Radiographie à vérifier");

            // Initialiser les prescriptions
            Prescription prescription1 = createPrescription("2025-03-20", patient1, medecin1, 30, Prescription.StatutPrescription.ACTIVE, "Traitement préventif post-infarctus. Régime pauvre en sel conseillé.");
            Prescription prescription2 = createPrescription("2025-03-15", patient3, medecin2, 60, Prescription.StatutPrescription.ACTIVE, "Traitement pour migraines chroniques. Éviter les facteurs déclenchants.");
            Prescription prescription3 = createPrescription("2025-03-10", patient5, medecin4, 15, Prescription.StatutPrescription.TERMINEE, "Anti-inflammatoires pour arthrose. Prévoir séances de kinésithérapie.");
            Prescription prescription4 = createPrescription("2025-03-25", patient7, medecin7, 90, Prescription.StatutPrescription.ACTIVE, "Traitement antidépresseur. Suivi psychothérapeutique recommandé.");
            Prescription prescription5 = createPrescription("2025-03-05", patient9, medecin9, 30, Prescription.StatutPrescription.TERMINEE, "Collyres pour glaucome. Prévoir contrôle tension oculaire dans 1 mois.");
            Prescription prescription6 = createPrescription("2025-03-18", patient10, medecin10, 45, Prescription.StatutPrescription.ACTIVE, "Traitement pour vertiges positionnels. Exercices de rééducation.");
            Prescription prescription7 = createPrescription("2025-03-22", patient11, medecin11, 30, Prescription.StatutPrescription.ACTIVE, "Traitement préventif de l'angine de poitrine. Surveiller la tension artérielle.");
            Prescription prescription8 = createPrescription("2025-03-12", patient12, medecin3, 10, Prescription.StatutPrescription.TERMINEE, "Antibiotiques pour otite. Consulter si fièvre persiste au-delà de 3 jours.");
            Prescription prescription9 = createPrescription("2025-03-27", patient13, medecin13, 7, Prescription.StatutPrescription.ACTIVE, "Antispasmodiques pour syndrome du côlon irritable.");
            Prescription prescription10 = createPrescription("2025-03-08", patient14, medecin2, 30, Prescription.StatutPrescription.ACTIVE, "Traitement prophylactique des migraines. Tenir un journal des crises.");
            Prescription prescription11 = createPrescription("2025-03-16", patient8, medecin8, 14, Prescription.StatutPrescription.TERMINEE, "Traitement topique pour eczéma. Éviter les savons irritants.");
            Prescription prescription12 = createPrescription("2025-03-23", patient6, medecin6, 21, Prescription.StatutPrescription.ANNULEE, "Traitement pour rhinite allergique. Suite à hospitalisation, prescription modifiée.");
            Prescription prescription13 = createPrescription("2025-03-19", patient2, medecin3, 5, Prescription.StatutPrescription.TERMINEE, "Antipyrétiques pour fièvre. Hydratation importante recommandée.");
            Prescription prescription14 = createPrescription("2025-03-26", patient4, medecin5, 180, Prescription.StatutPrescription.ACTIVE, "Traitement de maintenance post-chimiothérapie. Surveillance biologique mensuelle.");
            Prescription prescription15 = createPrescription("2025-03-29", patient15, medecin4, 14, Prescription.StatutPrescription.ACTIVE, "Analgésiques pour traumatisme du genou. Repos sportif 3 semaines minimum.");

            // Initialiser les lignes de prescription
            createLignePrescription(prescription1, med6, "1 sachet par jour", 30, "À prendre le matin à jeun avec un grand verre d'eau", LignePrescription.MomentPrise.AVANT_REPAS, 30, true);
            createLignePrescription(prescription1, med10, "1 comprimé par jour", 30, "À prendre le soir au coucher", LignePrescription.MomentPrise.APRES_REPAS, 30, true);
            createLignePrescription(prescription2, med1, "1 comprimé toutes les 6 heures si douleur", 15, "Ne pas dépasser 4 comprimés par jour", LignePrescription.MomentPrise.INDIFFERENT, 60, true);
            createLignePrescription(prescription2, med8, "0.25mg au coucher si anxiété", 30, "Réduire progressivement la dose", LignePrescription.MomentPrise.APRES_REPAS, 30, false);
            createLignePrescription(prescription3, med2, "1 comprimé 3 fois par jour", 10, "À prendre pendant les repas", LignePrescription.MomentPrise.PENDANT_REPAS, 30, true);
            createLignePrescription(prescription4, med8, "0.5mg matin et soir", 90, "Ne pas interrompre le traitement brutalement", LignePrescription.MomentPrise.APRES_REPAS, 180, false);
            createLignePrescription(prescription5, med14, "1 comprimé par jour", 30, "À prendre avant le petit-déjeuner", LignePrescription.MomentPrise.AVANT_REPAS, 30, true);
            createLignePrescription(prescription6, med1, "1 comprimé si vertige", 15, "Maximum 3 comprimés par jour", LignePrescription.MomentPrise.INDIFFERENT, 45, true);
            createLignePrescription(prescription7, med6, "1 sachet par jour", 30, "À prendre le matin", LignePrescription.MomentPrise.AVANT_REPAS, 30, true);
            createLignePrescription(prescription7, med10, "1 comprimé par jour", 30, "À prendre le soir", LignePrescription.MomentPrise.APRES_REPAS, 30, true);
            createLignePrescription(prescription8, med4, "1 comprimé matin et soir pendant 5 jours", 5, "À prendre pendant les repas", LignePrescription.MomentPrise.PENDANT_REPAS, 10, false);
            createLignePrescription(prescription9, med13, "1 comprimé avant les 3 principaux repas", 7, "Prise 15 minutes avant le repas", LignePrescription.MomentPrise.AVANT_REPAS, 21, true);
            createLignePrescription(prescription10, med15, "1 comprimé par jour", 30, "À prendre le soir", LignePrescription.MomentPrise.APRES_REPAS, 30, true);
            createLignePrescription(prescription11, med8, "Application locale 2 fois par jour", 14, "Appliquer sur les zones affectées", LignePrescription.MomentPrise.INDIFFERENT, 1, true);
            createLignePrescription(prescription12, med15, "1 comprimé par jour", 21, "À prendre le matin", LignePrescription.MomentPrise.AVANT_REPAS, 21, true);
            createLignePrescription(prescription13, med1, "1 comprimé toutes les 6 heures si fièvre", 5, "Ne pas dépasser 4 comprimés par jour", LignePrescription.MomentPrise.INDIFFERENT, 20, true);
            createLignePrescription(prescription14, med10, "1 comprimé par jour", 180, "À prendre le soir", LignePrescription.MomentPrise.APRES_REPAS, 180, false);
            createLignePrescription(prescription15, med2, "1 comprimé 3 fois par jour pendant 7 jours", 7, "À prendre pendant les repas", LignePrescription.MomentPrise.PENDANT_REPAS, 21, true);
            createLignePrescription(prescription15, med1, "1 comprimé toutes les 6 heures si douleur", 14, "Ne pas dépasser 4 comprimés par jour", LignePrescription.MomentPrise.INDIFFERENT, 56, true);

            System.out.println("Data initialization completed successfully!");
        };
    }

    private Departement createDepartement(String nom, String description, String localisation, int capaciteLits, boolean actif) {
        Departement departement = new Departement();
        departement.setNom(nom);
        departement.setDescription(description);
        departement.setLocalisation(localisation);
        departement.setCapaciteLits(capaciteLits);
        departement.setActif(actif);
        departement.setMedecins(new HashSet<>());
        return departementRepository.save(departement);
    }

    private Patient createPatient(String nom, String dateNaissanceStr, boolean malade, int score,User user) {
        try {
            Patient patient = new Patient();
            patient.setNom(nom);
            patient.setDateNaissance(dateFormat.parse(dateNaissanceStr));
            patient.setMalade(malade);
            patient.setScore(score);
            patient.setUser(user);
            return patientRepository.save(patient);
        } catch (Exception e) {
            System.err.println("Error creating patient: " + e.getMessage());
            return null;
        }
    }

    private Medecin createMedecin(String nom, String prenom, String specialite, String telephone,
                                  String email, String matricule, Departement departement, boolean disponible,User user) {
        Medecin medecin = new Medecin();
        medecin.setNom(nom);
        medecin.setPrenom(prenom);
        medecin.setSpecialite(specialite);
        medecin.setTelephone(telephone);
        medecin.setEmail(email);
        medecin.setMatricule(matricule);
        medecin.setDepartement(departement);
        medecin.setDisponible(disponible);
        medecin.setUser(user);
        return medecinRepository.save(medecin);
    }

    private void assignChefDepartement(Departement departement, Medecin medecin) {
        departement.setChefDepartement(medecin);
        departementRepository.save(departement);
    }

    private Medicament createMedicament(String nom, String dci, String laboratoire, String dosage, String forme,
                                        String dateExpirationStr, int quantiteStock, int seuilAlerte,
                                        double prix, boolean disponible, String contreIndications) {
        try {
            Medicament medicament = new Medicament();
            medicament.setNom(nom);
            medicament.setDci(dci);
            medicament.setLaboratoire(laboratoire);
            medicament.setDosage(dosage);
            medicament.setForme(forme);
            medicament.setDateExpiration(dateFormat.parse(dateExpirationStr));
            medicament.setQuantiteStock(quantiteStock);
            medicament.setSeuilAlerte(seuilAlerte);
            medicament.setPrix(new BigDecimal(String.valueOf(prix)));
            medicament.setDisponible(disponible);
            medicament.setContreIndications(contreIndications);
            return medicamentRepository.save(medicament);
        } catch (Exception e) {
            System.err.println("Error creating medication: " + e.getMessage());
            return null;
        }
    }

    private RendezVous createRendezVous(String dateHeureStr, Patient patient, Medecin medecin, String motif,
                                        int duree, RendezVous.StatutRendezVous statut, String notes) {
        try {
            RendezVous rendezVous = new RendezVous();
            rendezVous.setDateHeure(LocalDateTime.parse(dateHeureStr, dateTimeFormatter));
            rendezVous.setPatient(patient);
            rendezVous.setMedecin(medecin);
            rendezVous.setMotif(motif);
            rendezVous.setDuree(duree);
            rendezVous.setStatut(statut);
            rendezVous.setNotes(notes);
            return rendezVousRepository.save(rendezVous);
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            return null;
        }
    }

    private Prescription createPrescription(String datePrescriptionStr, Patient patient, Medecin medecin,
                                            int dureeValidite, Prescription.StatutPrescription statut,
                                            String observations) {
        try {
            Prescription prescription = new Prescription();
            prescription.setDatePrescription(dateFormat.parse(datePrescriptionStr));
            prescription.setPatient(patient);
            prescription.setMedecin(medecin);
            prescription.setDureeValidite(dureeValidite);
            prescription.setStatut(statut);
            prescription.setObservations(observations);
            prescription.setLignePrescriptions(new HashSet<>());
            return prescriptionRepository.save(prescription);
        } catch (Exception e) {
            System.err.println("Error creating prescription: " + e.getMessage());
            return null;
        }
    }

    private LignePrescription createLignePrescription(Prescription prescription, Medicament medicament,
                                                      String posologie, Integer dureeTraitement,
                                                      String instructions, LignePrescription.MomentPrise momentPrise,
                                                      Integer quantite, Boolean substitutionAutorisee) {
        LignePrescription lignePrescription = new LignePrescription();
        lignePrescription.setPrescription(prescription);
        lignePrescription.setMedicament(medicament);
        lignePrescription.setPosologie(posologie);
        lignePrescription.setDureeTraitement(dureeTraitement);
        lignePrescription.setInstructions(instructions);
        lignePrescription.setMomentPrise(momentPrise);
        lignePrescription.setQuantite(quantite);
        lignePrescription.setSubstitutionAutorisee(substitutionAutorisee);
        return lignePrescriptionRepository.save(lignePrescription);
    }
    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }
    private User createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setActive(true);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}