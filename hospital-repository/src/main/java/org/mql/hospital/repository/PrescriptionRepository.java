package org.mql.hospital.repository;

import org.mql.hospital.entities.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Recherche des prescriptions par patient.
     */
    List<Prescription> findByPatientId(Long patientId);

    /**
     * Recherche des prescriptions par médecin.
     */
    List<Prescription> findByMedecinId(Long medecinId);

    /**
     * Recherche des prescriptions actives par patient.
     */
    List<Prescription> findByPatientIdAndStatut(Long patientId, Prescription.StatutPrescription statut);

    /**
     * Recherche des prescriptions par période.
     */
    List<Prescription> findByDatePrescriptionBetween(Date debut, Date fin);

    /**
     * Recherche des prescriptions par patient et période.
     */
    Page<Prescription> findByPatientIdAndDatePrescriptionBetween(Long patientId, Date debut, Date fin, Pageable pageable);

    /**
     * Recherche des prescriptions avec critères multiples.
     */
    @Query("SELECT DISTINCT p FROM Prescription p " +
            "LEFT JOIN FETCH p.patient " +
            "LEFT JOIN FETCH p.medecin " +
            "LEFT JOIN FETCH p.lignePrescriptions " +
            "WHERE (:patientId IS NULL OR p.patient.id = :patientId) AND " +
            "(:medecinId IS NULL OR p.medecin.id = :medecinId) AND " +
            "(:statut IS NULL OR p.statut = :statut) AND " +
            "(:dateDebut IS NULL OR p.datePrescription >= :dateDebut) AND " +
            "(:dateFin IS NULL OR p.datePrescription <= :dateFin)")
    Page<Prescription> recherchePrescriptions(
            @Param("patientId") Long patientId,
            @Param("medecinId") Long medecinId,
            @Param("statut") Prescription.StatutPrescription statut,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin,
            Pageable pageable);

    /**
     * Compte le nombre de prescriptions par médecin pour une période donnée.
     */
    @Query("SELECT p.medecin.id, p.medecin.nom, p.medecin.prenom, COUNT(p) " +
            "FROM Prescription p " +
            "WHERE p.datePrescription BETWEEN :dateDebut AND :dateFin " +
            "GROUP BY p.medecin.id, p.medecin.nom, p.medecin.prenom")
    List<Object[]> countPrescriptionsByMedecin(@Param("dateDebut") Date dateDebut, @Param("dateFin") Date dateFin);
}