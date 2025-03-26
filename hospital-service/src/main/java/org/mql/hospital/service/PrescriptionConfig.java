package org.mql.hospital.service;


import org.mql.hospital.config.PersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.mql.hospital.service.PatientService;
import org.mql.hospital.service.MedecinService;
import org.mql.hospital.service.PrescriptionService;
import org.mql.hospital.service.LignePrescriptionService;
import org.mql.hospital.service.MedicamentService;
import org.mql.hospital.repository.PatientRepository;
import org.mql.hospital.repository.MedecinRepository;
import org.mql.hospital.repository.PrescriptionRepository;
import org.mql.hospital.repository.LignePrescriptionRepository;
import org.mql.hospital.repository.MedicamentRepository;
import org.mql.hospital.service.PatientServiceImpl;
import org.mql.hospital.service.MedecinServiceImpl;
import org.mql.hospital.service.PrescriptionServiceImpl;
import org.mql.hospital.service.LignePrescriptionServiceImpl;
import org.mql.hospital.service.MedicamentServiceImpl;

/**
 * Configuration pour les services et repositories liés aux prescriptions et lignes de prescription.
 * Cette classe garantit que toutes les dépendances sont correctement injectées.
 */
@Configuration
@Import(PersistenceConfig.class)
public class PrescriptionConfig {

    /**
     * Configuration du service de gestion des lignes de prescription.
     * Cette méthode n'est nécessaire que si l'autowiring automatique échoue.
     */
    @Bean
    public LignePrescriptionService lignePrescriptionService(
            LignePrescriptionRepository lignePrescriptionRepository,
            PrescriptionRepository prescriptionRepository,
            MedicamentRepository medicamentRepository) {
        return new LignePrescriptionServiceImpl(
                lignePrescriptionRepository,
                prescriptionRepository,
                medicamentRepository);
    }

    /**
     * Configuration du service de gestion des prescriptions.
     * Cette méthode n'est nécessaire que si l'autowiring automatique échoue.
     */
    @Bean
    public PrescriptionService prescriptionService(
            PrescriptionRepository prescriptionRepository,
            LignePrescriptionRepository lignePrescriptionRepository,
            PatientRepository patientRepository,
            MedecinRepository medecinRepository,
            MedicamentRepository medicamentRepository) {
        return new PrescriptionServiceImpl(
                prescriptionRepository,
                lignePrescriptionRepository,
                patientRepository,
                medecinRepository,
                medicamentRepository);
    }
}