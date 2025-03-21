package org.mql.hospital.service;

import org.mql.hospital.entities.RendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service responsable de la gestion des rendez-vous dans l'application.
 */
public interface RendezVousService {

    /**
     * Récupère tous les rendez-vous.
     *
     * @return Une liste de tous les rendez-vous
     */
    List<RendezVous> getAllRendezVous();

    /**
     * Récupère un rendez-vous par son identifiant.
     *
     * @param id L'identifiant du rendez-vous
     * @return Un Optional contenant le rendez-vous s'il existe, sinon un Optional vide
     */
    Optional<RendezVous> getRendezVousById(Long id);

    /**
     * Sauvegarde un rendez-vous (création ou mise à jour).
     *
     * @param rendezVous Le rendez-vous à sauvegarder
     * @return Le rendez-vous sauvegardé
     */
    RendezVous saveRendezVous(RendezVous rendezVous);

    /**
     * Supprime un rendez-vous par son identifiant.
     *
     * @param id L'identifiant du rendez-vous à supprimer
     */
    void deleteRendezVous(Long id);

    /**
     * Recherche des rendez-vous pour un patient.
     *
     * @param patientId L'identifiant du patient
     * @return Une liste des rendez-vous du patient
     */
    List<RendezVous> findByPatient(Long patientId);

    /**
     * Recherche des rendez-vous pour un médecin.
     *
     * @param medecinId L'identifiant du médecin
     * @return Une liste des rendez-vous du médecin
     */
    List<RendezVous> findByMedecin(Long medecinId);

    /**
     * Recherche des rendez-vous pour un médecin avec un statut spécifique.
     *
     * @param medecinId L'identifiant du médecin
     * @param statut Le statut des rendez-vous recherchés
     * @return Une liste des rendez-vous correspondants
     */
    List<RendezVous> findByMedecinAndStatut(Long medecinId, RendezVous.StatutRendezVous statut);

    /**
     * Recherche des rendez-vous dans une période donnée.
     *
     * @param debut Date et heure de début de la période
     * @param fin Date et heure de fin de la période
     * @return Une liste des rendez-vous dans la période spécifiée
     */
    List<RendezVous> findByPeriode(LocalDateTime debut, LocalDateTime fin);

    /**
     * Recherche des rendez-vous pour un médecin dans une période donnée.
     *
     * @param medecinId L'identifiant du médecin
     * @param debut Date et heure de début de la période
     * @param fin Date et heure de fin de la période
     * @return Une liste des rendez-vous correspondants
     */
    List<RendezVous> findByMedecinAndPeriode(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Recherche paginée des rendez-vous pour un patient avec un statut spécifique.
     *
     * @param patientId L'identifiant du patient
     * @param statut Le statut des rendez-vous recherchés
     * @param pageable Les informations de pagination
     * @return Une page de rendez-vous correspondants
     */
    Page<RendezVous> findByPatientAndStatut(Long patientId, RendezVous.StatutRendezVous statut, Pageable pageable);

    /**
     * Recherche avancée de rendez-vous avec critères multiples.
     *
     * @param medecinId L'identifiant du médecin (peut être null)
     * @param patientId L'identifiant du patient (peut être null)
     * @param statut Le statut des rendez-vous (peut être null)
     * @param dateDebut Date et heure de début (peut être null)
     * @param dateFin Date et heure de fin (peut être null)
     * @param pageable Les informations de pagination
     * @return Une page de rendez-vous correspondant aux critères
     */
    Page<RendezVous> searchRendezVous(
            Long medecinId,
            Long patientId,
            RendezVous.StatutRendezVous statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Pageable pageable);

    /**
     * Vérifie si un médecin est disponible pour un créneau donné.
     *
     * @param medecinId L'identifiant du médecin
     * @param debut Date et heure de début du créneau
     * @param fin Date et heure de fin du créneau
     * @return true si le médecin est disponible, false sinon
     */
    boolean isMedecinAvailable(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Change le statut d'un rendez-vous.
     *
     * @param id L'identifiant du rendez-vous
     * @param statut Le nouveau statut
     * @return Le rendez-vous mis à jour
     */
    RendezVous updateRendezVousStatus(Long id, RendezVous.StatutRendezVous statut);
}