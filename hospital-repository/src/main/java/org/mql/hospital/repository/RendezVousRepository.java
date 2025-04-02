package org.mql.hospital.repository;

import org.mql.hospital.entities.RendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    /**
     * Recherche des rendez-vous par patient.
     */
    List<RendezVous> findByPatientId(Long patientId);

    /**
     * Recherche des rendez-vous par médecin.
     */
    List<RendezVous> findByMedecinId(Long medecinId);

    /**
     * Recherche des rendez-vous par médecin et statut.
     */
    List<RendezVous> findByMedecinIdAndStatut(Long medecinId, RendezVous.StatutRendezVous statut);

    /**
     * Recherche des rendez-vous pour une période donnée.
     */
    List<RendezVous> findByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);

    /**
     * Recherche des rendez-vous par médecin pour une période donnée.
     */
    List<RendezVous> findByMedecinIdAndDateHeureBetween(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Recherche des rendez-vous par patient et statut.
     */
    Page<RendezVous> findByPatientIdAndStatut(Long patientId, RendezVous.StatutRendezVous statut, Pageable pageable);

    /**
     * Recherche des rendez-vous avec critères multiples.
     */
    @Query("SELECT rv FROM RendezVous rv WHERE " +
            "(:medecinId IS NULL OR rv.medecin.id = :medecinId) AND " +
            "(:patientId IS NULL OR rv.patient.id = :patientId) AND " +
            "(:statut IS NULL OR rv.statut = :statut) AND " +
            "(:dateDebut IS NULL OR rv.dateHeure >= :dateDebut) AND " +
            "(:dateFin IS NULL OR rv.dateHeure <= :dateFin)")
    Page<RendezVous> rechercheRendezVous(
            @Param("medecinId") Long medecinId,
            @Param("patientId") Long patientId,
            @Param("statut") RendezVous.StatutRendezVous statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable);

    /**
     * Vérifie si un médecin a des rendez-vous pour un créneau donné.
     */
    boolean existsByMedecinIdAndDateHeureBetween(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Compte le nombre de rendez-vous pour une période donnée et qui ne sont pas annulés.
     */
    long countByDateHeureBetweenAndStatutNot(LocalDateTime debut, LocalDateTime fin, RendezVous.StatutRendezVous statut);

    /**
     * Récupère les rendez-vous pour une période donnée et qui ne sont pas annulés, triés par date et heure.
     */
    List<RendezVous> findByDateHeureBetweenAndStatutNotOrderByDateHeureAsc(
            LocalDateTime debut, LocalDateTime fin, RendezVous.StatutRendezVous statut);
}