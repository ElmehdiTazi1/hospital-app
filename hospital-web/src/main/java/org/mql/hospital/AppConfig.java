package org.mql.hospital;

import lombok.extern.slf4j.Slf4j;
import org.mql.hospital.entities.Patient;
import org.mql.hospital.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * Configuration principale de l'application.
 * Cette classe contient des configurations et des beans pour l'application.
 */
@Configuration
@Slf4j
public class AppConfig {

    /**
     * Initialise des données de test lorsque l'application démarre.
     *
     * @param patientRepository Repository pour accéder aux données des patients
     * @return CommandLineRunner pour exécuter l'initialisation des données
     */
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
}