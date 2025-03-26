package org.mql.hospital.service;


import org.mql.hospital.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service responsable de la gestion des patients dans l'application.
 * Cette interface définit les opérations disponibles pour manipuler les données
 * des patients et encapsule la logique métier associée.
 */
public interface PatientService {

    /**
     * Récupère tous les patients.
     *
     * @return Une liste de tous les patients dans le système
     */
    List<Patient> getAllPatients();

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id L'identifiant du patient à récupérer
     * @return Un Optional contenant le patient s'il existe, sinon un Optional vide
     */
    Optional<Patient> getPatientById(Long id);

    /**
     * Sauvegarde un patient (création ou mise à jour).
     *
     * @param patient Le patient à sauvegarder
     * @return Le patient sauvegardé avec son ID généré s'il s'agit d'une création
     */
    Patient savePatient(Patient patient);

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id L'identifiant du patient à supprimer
     */
    void deletePatient(Long id);

    /**
     * Recherche des patients dont le nom contient un mot-clé spécifié.
     *
     * @param keyword Le mot-clé à rechercher dans les noms des patients
     * @param pageable Les informations de pagination
     * @return Une page de patients dont le nom contient le mot-clé
     */
    Page<Patient> findByNomContains(String keyword, Pageable pageable);

    /**
     * Recherche avancée de patients par une requête personnalisée.
     *
     * @param keyword Le mot-clé pour la recherche
     * @param pageable Les informations de pagination
     * @return Une page de patients correspondant aux critères de recherche
     */
    Page<Patient> searchPatients(String keyword, Pageable pageable);

    boolean patientExists(Long id);
}