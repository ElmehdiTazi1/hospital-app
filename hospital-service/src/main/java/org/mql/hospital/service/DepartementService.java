package org.mql.hospital.service;

import org.mql.hospital.entities.Departement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsable de la gestion des départements dans l'application.
 */
public interface DepartementService {

    /**
     * Récupère tous les départements.
     *
     * @return Une liste de tous les départements
     */
    List<Departement> getAllDepartements();

    /**
     * Récupère un département par son identifiant.
     *
     * @param id L'identifiant du département
     * @return Un Optional contenant le département s'il existe, sinon un Optional vide
     */
    Optional<Departement> getDepartementById(Long id);

    /**
     * Sauvegarde un département (création ou mise à jour).
     *
     * @param departement Le département à sauvegarder
     * @return Le département sauvegardé
     */
    Departement saveDepartement(Departement departement);

    /**
     * Supprime un département par son identifiant.
     *
     * @param id L'identifiant du département à supprimer
     */
    void deleteDepartement(Long id);

    /**
     * Recherche des départements par nom.
     *
     * @param nom Le nom à rechercher
     * @param pageable Les informations de pagination
     * @return Une page de départements dont le nom contient la chaîne recherchée
     */
    Page<Departement> findByNomContains(String nom, Pageable pageable);

    /**
     * Récupère tous les départements actifs.
     *
     * @return Une liste des départements actifs
     */
    List<Departement> findActiveDepartements();

    /**
     * Recherche le département d'un chef de département.
     *
     * @param medecinId L'identifiant du médecin chef de département
     * @return Le département dirigé par le médecin spécifié
     */
    Departement findByChefDepartement(Long medecinId);

    /**
     * Récupère le nombre de médecins par département.
     *
     * @return Une map avec les IDs des départements en clé et le nombre de médecins en valeur
     */
    Map<String, Long> countMedecinsByDepartement();

    /**
     * Recherche des départements ayant une capacité de lits minimale.
     *
     * @param capaciteMinimale La capacité minimale de lits
     * @return Une liste de départements ayant au moins la capacité spécifiée
     */
    List<Departement> findByCapaciteMinimale(Integer capaciteMinimale);

    /**
     * Active ou désactive un département.
     *
     * @param id L'identifiant du département
     * @param actif true pour activer, false pour désactiver
     * @return Le département mis à jour
     */
    Departement toggleDepartementStatus(Long id, boolean actif);

    /**
     * Assigne un chef à un département.
     *
     * @param departementId L'identifiant du département
     * @param medecinId L'identifiant du médecin à assigner comme chef
     * @return Le département mis à jour
     */
    Departement assignChefDepartement(Long departementId, Long medecinId);
}