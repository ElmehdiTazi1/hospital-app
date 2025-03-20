package org.mql.hospital.api.service;


import org.mql.hospital.api.dto.PatientDTO;
import org.mql.hospital.api.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface de service définissant les opérations disponibles pour la gestion des patients.
 */
public interface PatientService {

    /**
     * Récupère tous les patients.
     *
     * @return Une liste de tous les patients
     */
    List<PatientDTO> getAllPatients();

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id L'identifiant du patient
     * @return Le DTO du patient
     * @throws ResourceNotFoundException si le patient n'existe pas
     */
    PatientDTO getPatientById(Long id) throws ResourceNotFoundException;

    /**
     * Enregistre un patient (création ou mise à jour).
     *
     * @param patientDTO Les données du patient à enregistrer
     * @return Le DTO du patient enregistré
     */
    PatientDTO savePatient(PatientDTO patientDTO);

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id L'identifiant du patient à supprimer
     * @throws ResourceNotFoundException si le patient n'existe pas
     */
    void deletePatient(Long id) throws ResourceNotFoundException;

    /**
     * Recherche des patients par nom.
     *
     * @param keyword Le mot-clé à rechercher dans les noms
     * @param pageable Les informations de pagination
     * @return Une page de DTOs de patients
     */
    Page<PatientDTO> findByNomContains(String keyword, Pageable pageable);

    /**
     * Recherche avancée de patients.
     *
     * @param keyword Le mot-clé de recherche
     * @param pageable Les informations de pagination
     * @return Une page de DTOs de patients
     */
    Page<PatientDTO> searchPatients(String keyword, Pageable pageable);

    /**
     * Vérifie si un patient existe.
     *
     * @param id L'identifiant du patient
     * @return true si le patient existe, false sinon
     */
    boolean patientExists(Long id);
}