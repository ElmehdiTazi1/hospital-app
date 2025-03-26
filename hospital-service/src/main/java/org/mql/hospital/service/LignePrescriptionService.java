package org.mql.hospital.service;

import org.mql.hospital.entities.LignePrescription;
import org.mql.hospital.entities.Medicament;
import org.mql.hospital.entities.Prescription;

import java.util.List;
import java.util.Optional;

/**
 * Service responsable de la gestion des lignes de prescription dans l'application.
 */
public interface LignePrescriptionService {

    /**
     * Récupère toutes les lignes de prescription.
     *
     * @return Une liste de toutes les lignes de prescription
     */
    List<LignePrescription> getAllLignePrescriptions();

    /**
     * Récupère une ligne de prescription par son identifiant.
     *
     * @param id L'identifiant de la ligne de prescription
     * @return Un Optional contenant la ligne de prescription si elle existe, sinon un Optional vide
     */
    Optional<LignePrescription> getLignePrescriptionById(Long id);

    /**
     * Sauvegarde une ligne de prescription (création ou mise à jour).
     *
     * @param lignePrescription La ligne de prescription à sauvegarder
     * @return La ligne de prescription sauvegardée
     */
    LignePrescription saveLignePrescription(LignePrescription lignePrescription);

    /**
     * Supprime une ligne de prescription par son identifiant.
     *
     * @param id L'identifiant de la ligne de prescription à supprimer
     */
    void deleteLignePrescription(Long id);

    /**
     * Recherche des lignes de prescription par prescription.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @return Une liste des lignes de prescription associées à la prescription spécifiée
     */
    List<LignePrescription> findByPrescription(Long prescriptionId);

    /**
     * Recherche des lignes de prescription par médicament.
     *
     * @param medicamentId L'identifiant du médicament
     * @return Une liste des lignes de prescription contenant le médicament spécifié
     */
    List<LignePrescription> findByMedicament(Long medicamentId);

    /**
     * Ajoute une ligne de prescription à une prescription existante.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @param lignePrescription La ligne de prescription à ajouter
     * @return La ligne de prescription ajoutée
     */
    LignePrescription addToPrescription(Long prescriptionId, LignePrescription lignePrescription);

    /**
     * Vérifie si un médicament est déjà présent dans une prescription.
     *
     * @param prescriptionId L'identifiant de la prescription
     * @param medicamentId L'identifiant du médicament
     * @return true si le médicament est déjà présent, false sinon
     */
    boolean isMedicamentInPrescription(Long prescriptionId, Long medicamentId);
}