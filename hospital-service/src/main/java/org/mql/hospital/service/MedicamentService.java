package org.mql.hospital.service;

import org.mql.hospital.entities.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service responsable de la gestion des médicaments dans l'application.
 */
public interface MedicamentService {

    /**
     * Récupère tous les médicaments.
     *
     * @return Une liste de tous les médicaments
     */
    List<Medicament> getAllMedicaments();

    /**
     * Récupère un médicament par son identifiant.
     *
     * @param id L'identifiant du médicament
     * @return Un Optional contenant le médicament s'il existe, sinon un Optional vide
     */
    Optional<Medicament> getMedicamentById(Long id);

    /**
     * Sauvegarde un médicament (création ou mise à jour).
     *
     * @param medicament Le médicament à sauvegarder
     * @return Le médicament sauvegardé
     */
    Medicament saveMedicament(Medicament medicament);

    /**
     * Supprime un médicament par son identifiant.
     *
     * @param id L'identifiant du médicament à supprimer
     */
    void deleteMedicament(Long id);

    /**
     * Recherche paginée des médicaments par nom.
     *
     * @param nom Le nom à rechercher
     * @param pageable Les informations de pagination
     * @return Une page de médicaments dont le nom contient la chaîne recherchée
     */
    Page<Medicament> findByNomContains(String nom, Pageable pageable);

    /**
     * Recherche des médicaments par DCI (Dénomination Commune Internationale).
     *
     * @param dci La DCI à rechercher
     * @return Une liste de médicaments correspondants
     */
    List<Medicament> findByDciContains(String dci);

    /**
     * Recherche des médicaments par laboratoire.
     *
     * @param laboratoire Le laboratoire à rechercher
     * @return Une liste de médicaments correspondants
     */
    List<Medicament> findByLaboratoire(String laboratoire);

    /**
     * Récupère tous les médicaments disponibles.
     *
     * @return Une liste des médicaments disponibles
     */
    List<Medicament> findAvailableMedicaments();

    /**
     * Récupère les médicaments dont le stock est en alerte.
     *
     * @return Une liste des médicaments en alerte de stock
     */
    List<Medicament> findMedicamentsEnAlerte();

    /**
     * Récupère les médicaments qui vont expirer avant une date donnée.
     *
     * @param date La date limite d'expiration
     * @return Une liste des médicaments qui vont expirer avant la date spécifiée
     */
    List<Medicament> findMedicamentsExpiringBefore(Date date);

    /**
     * Recherche avancée de médicaments avec critères multiples.
     *
     * @param nom Le nom à rechercher (peut être null)
     * @param dci La DCI à rechercher (peut être null)
     * @param laboratoire Le laboratoire à rechercher (peut être null)
     * @param disponible Statut de disponibilité (peut être null)
     * @param enAlerte Statut d'alerte de stock (peut être null)
     * @param pageable Les informations de pagination
     * @return Une page de médicaments correspondant aux critères
     */
    Page<Medicament> searchMedicaments(
            String nom,
            String dci,
            String laboratoire,
            Boolean disponible,
            Boolean enAlerte,
            Pageable pageable);

    /**
     * Met à jour le stock d'un médicament.
     *
     * @param id L'identifiant du médicament
     * @param quantite La quantité à ajouter (positive) ou soustraire (négative)
     * @return Le médicament mis à jour
     */
    Medicament updateStock(Long id, Integer quantite);

    /**
     * Modifie le statut de disponibilité d'un médicament.
     *
     * @param id L'identifiant du médicament
     * @param disponible true pour disponible, false pour indisponible
     * @return Le médicament mis à jour
     */
    Medicament toggleAvailability(Long id, boolean disponible);
}