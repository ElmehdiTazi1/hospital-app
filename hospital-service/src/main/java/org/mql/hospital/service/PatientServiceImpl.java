package org.mql.hospital.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.repository.PatientRepository;
import org.mql.hospital.service.PatientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service {@link PatientService} qui encapsule
 * la logique métier concernant les opérations sur les patients.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j // Ajoute un logger SLF4J automatiquement
public class PatientServiceImpl implements PatientService {

    // Création manuelle du logger en cas de problème avec @Slf4j
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    private  PatientRepository patientRepository;

    @Override
    public List<Patient> getAllPatients() {
        // Utilisons le logger manuel en attendant que @Slf4j fonctionne
        logger.info("Récupération de tous les patients");
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        logger.info("Récupération du patient avec l'ID: {}", id);
        return patientRepository.findById(id);
    }

    @Override
    public Patient savePatient(Patient patient) {
        if (patient.getId() == null) {
            logger.info("Création d'un nouveau patient: {}", patient.getNom());
        } else {
            logger.info("Mise à jour du patient avec l'ID: {}", patient.getId());
        }
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        logger.info("Suppression du patient avec l'ID: {}", id);
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> findByNomContains(String keyword, Pageable pageable) {
        logger.info("Recherche de patients contenant '{}' dans leur nom, page: {}", keyword, pageable.getPageNumber());
        return patientRepository.findByNomContains(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> searchPatients(String keyword, Pageable pageable) {
        logger.info("Recherche avancée de patients avec le mot-clé: {}", keyword);
        return patientRepository.chercher("%" + keyword + "%", pageable);
    }
    @Bean
    public CommandLineRunner initData(PatientRepository patientRepository) {
        return args -> {

            // Supprime toutes les données existantes
            patientRepository.deleteAll();

            // Crée des patients de test
            patientRepository.save(Patient.builder()
                    .nom("Jean Dupont")
                    .dateNaissance(new Date(System.currentTimeMillis() - 40L * 365 * 24 * 60 * 60 * 1000))
                    .malade(true)
                    .score(150)
                    .build());

            patientRepository.save(Patient.builder()
                    .nom("Marie Martin")
                    .dateNaissance(new Date(System.currentTimeMillis() - 25L * 365 * 24 * 60 * 60 * 1000))
                    .malade(false)
                    .score(180)
                    .build());

            patientRepository.save(Patient.builder()
                    .nom("Pierre Leclerc")
                    .dateNaissance(new Date(System.currentTimeMillis() - 67L * 365 * 24 * 60 * 60 * 1000))
                    .malade(true)
                    .score(110)
                    .build());

            patientRepository.save(Patient.builder()
                    .nom("Sophie Bernard")
                    .dateNaissance(new Date(System.currentTimeMillis() - 32L * 365 * 24 * 60 * 60 * 1000))
                    .malade(false)
                    .score(200)
                    .build());

            patientRepository.save(Patient.builder()
                    .nom("Ahmed Alami")
                    .dateNaissance(new Date(System.currentTimeMillis() - 50L * 365 * 24 * 60 * 60 * 1000))
                    .malade(true)
                    .score(125)
                    .build());

        };
    }
    // Ajout d'une méthode utilitaire pour vérifier si un patient existe
    public boolean patientExists(Long id) {
        return patientRepository.existsById(id);
    }
}