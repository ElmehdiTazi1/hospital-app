package org.mql.hospital.service;

import org.mql.hospital.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service responsable de la gestion des médecins dans l'application.
 */
public interface MedecinService {

    /**
     * Récupère tous les médecins.
     *
     * @return Une liste de tous les médecins
     */
    List<Medecin> getAllMedecins();

    /**
     * Récupère un médecin par son identifiant.
     *
     * @param id L'identifiant du médecin
     * @return Un Optional contenant le médecin s'il existe, sinon un Optional vide
     */
    Optional<Medecin> getMedecinById(Long id);

    /**
     * Récupère un médecin par son matricule.
     *
     * @param matricule Le matricule du médecin
     * @return Un Optional contenant le médecin s'il existe, sinon un Optional vide
     */
    Optional<Medecin> getMedecinByMatricule(String matricule);

    /**
     * Sauvegarde un médecin (création ou mise à jour).
     *
     * @param medecin Le médecin à sauvegarder
     * @return Le médecin sauvegardé
     */
    Medecin saveMedecin(Medecin medecin);

    /**
     * Supprime un médecin par son identifiant.
     *
     * @param id L'identifiant du médecin à supprimer
     */
    void deleteMedecin(Long id);

    /**
     * Recherche des médecins par nom.
     *
     * @param nom Le nom à rechercher
     * @param pageable Les informations de pagination
     * @return Une page de médecins dont le nom contient la chaîne recherchée
     */
    Page<Medecin> findByNomContains(String nom, Pageable pageable);

    /**
     * Recherche des médecins par spécialité.
     *
     * @param specialite La spécialité à rechercher
     * @return Une liste de médecins ayant la spécialité spécifiée
     */
    List<Medecin> findBySpecialite(String specialite);

    /**
     * Recherche des médecins disponibles.
     *
     * @return Une liste de médecins disponibles
     */
    List<Medecin> findAvailableMedecins();

    /**
     * Recherche des médecins par département.
     *
     * @param departementId L'identifiant du département
     * @return Une liste de médecins appartenant au département spécifié
     */
    List<Medecin> findByDepartement(Long departementId);

    /**
     * Recherche avancée de médecins avec critères multiples.
     *
     * @param nom Le nom à rechercher (peut être null)
     * @param prenom Le prénom à rechercher (peut être null)
     * @param specialite La spécialité à rechercher (peut être null)
     * @param departementId L'identifiant du département (peut être null)
     * @param disponible Statut de disponibilité (peut être null)
     * @param pageable Les informations de pagination
     * @return Une page de médecins correspondant aux critères de recherche
     */
    Page<Medecin> searchMedecins(
            String nom,
            String prenom,
            String specialite,
            Long departementId,
            Boolean disponible,
            Pageable pageable);

    /**
     * Vérifie si un médecin existe.
     *
     * @param id L'identifiant du médecin
     * @return true si le médecin existe, false sinon
     */
    boolean medecinExists(Long id);

    /**
     * Définit un médecin comme chef de département.
     *
     * @param medecinId L'identifiant du médecin
     * @param departementId L'identifiant du département
     * @return Le médecin mis à jour
     */
    Medecin setChefDepartement(Long medecinId, Long departementId);
}