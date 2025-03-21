package org.mql.hospital.repository;

import org.mql.hospital.entities.LignePrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface LignePrescriptionRepository extends JpaRepository<LignePrescription, Long> {

    /**
     * Recherche des lignes de prescription par prescription.
     */
    List<LignePrescription> findByPrescriptionId(Long prescriptionId);

    /**
     * Recherche des lignes de prescription par médicament.
     */
    List<LignePrescription> findByMedicamentId(Long medicamentId);

    /**
     * Recherche des lignes de prescription pour un patient et un médicament donnés.
     */
    @Query("SELECT lp FROM LignePrescription lp " +
            "JOIN lp.prescription p " +
            "WHERE p.patient.id = :patientId AND lp.medicament.id = :medicamentId")
    List<LignePrescription> findByPatientIdAndMedicamentId(
            @Param("patientId") Long patientId,
            @Param("medicamentId") Long medicamentId);

    /**
     * Compte les médicaments les plus prescrits pour une période donnée.
     */
    @Query("SELECT lp.medicament.id, lp.medicament.nom, COUNT(lp) " +
            "FROM LignePrescription lp " +
            "JOIN lp.prescription p " +
            "WHERE p.datePrescription BETWEEN :dateDebut AND :dateFin " +
            "GROUP BY lp.medicament.id, lp.medicament.nom " +
            "ORDER BY COUNT(lp) DESC")
    List<Object[]> findMostPrescribedMedicaments(
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin);

    /**
     * Vérifie si un médicament a été prescrit à un patient dans un intervalle de temps donné.
     */
    @Query("SELECT COUNT(lp) > 0 FROM LignePrescription lp " +
            "JOIN lp.prescription p " +
            "WHERE p.patient.id = :patientId AND lp.medicament.id = :medicamentId " +
            "AND p.datePrescription BETWEEN :dateDebut AND :dateFin")
    boolean hasMedicamentBeenPrescribedToPatient(
            @Param("patientId") Long patientId,
            @Param("medicamentId") Long medicamentId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin);
}