package org.mql.hospital.service;

import org.mql.hospital.entities.LignePrescription;
import org.mql.hospital.entities.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsable de la gestion des prescriptions dans l'application.
 */
public interface PrescriptionService {

    /**
     * Récupère toutes les prescriptions.
     *
     * @return Une liste de toutes les prescriptions
     */
    List<Prescription> getAllPrescriptions();

    /**
     * Récupère une prescription par son identifiant.
     *
     * @param id L'identifiant de la prescription
     * @return Un Optional contenant la prescription si elle existe, sinon un Optional vide
     */
    Optional<Prescription> getPrescriptionById(Long id);

    /**
     * Sauvegarde une prescription (création ou mise à jour).
     *
     * @param prescription La prescription à sauvegarder
     * @return La prescription sauvegardée
     */
    Prescription savePrescription(Prescription prescription);

    /**
     * Supprime une prescription par son identifiant.
     *
     * @param id L'identifiant de la prescription à supprimer
     */
    void deletePrescription(Long id);

    /**
     * Recherche des prescriptions pour un patient.
     *
     * @param patientId L'identifiant du patient
     * @return Une liste des prescriptions du patient
     */
    List<Prescription> findByPatient(Long patientId);

    /**
     * Recherche des prescriptions pour un médecin.
     *
     * @param medecinId L'identifiant du médecin
     * @return Une liste des prescriptions du médecin
     */
    List<Prescription> findByMedecin(Long medecinId);

    /**
     * Recherche des prescriptions actives pour un patient.
     *
     * @param patientId L'identifiant du patient
     * @return Une liste des prescriptions actives du patient
     */
    List<Prescription> findActivePrescriptionsByPatient(Long patientId);

    /**
     * Recherche des prescriptions dans une période donnée.
     *
     * @param debut Date de début de la période
     * @param fin Date de fin de la période
     * @return Une liste des prescriptions dans la période spécifiée
     */
    List<Prescription> findByPeriode(Date debut, Date fin);

    /**
     * Recherche paginée des prescriptions pour un patient et une période donnée.
     *
     * @param patientId L'identifiant du patient
     * @param debut Date de début de la période
     * @param fin Date de fin de la période
     * @param pageable Les informations de pagination
     * @return Une page de prescriptions correspondantes
     */
    Page<Prescription> findByPatientAndPeriode(Long patientId, Date debut, Date fin, Pageable pageable);

    /**
     * Recherche avancée de prescriptions avec critères multiples.
     *
     * @param patientId L'identifiant du patient (peut être null)
     * @param medecinId L'identifiant du médecin (peut être null)
     * @param statut Le statut de la prescription (peut être null)
     * @param dateDebut Date de début (peut être null)
     * @param dateFin Date de fin (peut être null)
     * @param pageable Les informations de pagination
     * @return Une page de prescriptions correspondant aux critères
     */
    Page<Prescription> searchPrescriptions(
            Long patientId,
            Long medecinId,
            Prescription.StatutPrescription statut,
            Date dateDebut,
            Date dateFin,
            Pageable pageable);

    /**
     * Ajoute une ligne de prescription à une prescription existante.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @param lignePrescription La ligne de prescription à ajouter
     * @return La prescription mise à jour
     */
    Prescription addLignePrescription(Long prescriptionId, LignePrescription lignePrescription);

    /**
     * Supprime une ligne de prescription.
     *
     * @param lignePrescriptionId L'identifiant de la ligne de prescription à supprimer
     */
    void deleteLignePrescription(Long lignePrescriptionId);

    /**
     * Modifie le statut d'une prescription.
     *
     * @param id L'identifiant de la prescription
     * @param statut Le nouveau statut
     * @return La prescription mise à jour
     */
    Prescription updatePrescriptionStatus(Long id, Prescription.StatutPrescription statut);

    /**
     * Compte le nombre de prescriptions par médecin pour une période donnée.
     *
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @return Une map avec les médecins en clé et le nombre de prescriptions en valeur
     */
    Map<String, Long> countPrescriptionsByMedecin(Date dateDebut, Date dateFin);

    /**
     * Vérifie si une prescription est valide.
     *
     * @param id L'identifiant de la prescription
     * @return true si la prescription est valide, false sinon
     */
    boolean isPrescriptionValide(Long id);
}