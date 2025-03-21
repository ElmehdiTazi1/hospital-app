package org.mql.hospital.repository;

import org.mql.hospital.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    /**
     * Recherche des médecins par nom.
     */
    Page<Medecin> findByNomContainsIgnoreCase(String nom, Pageable pageable);

    /**
     * Recherche des médecins par spécialité.
     */
    List<Medecin> findBySpecialite(String specialite);

    /**
     * Recherche un médecin par son matricule.
     */
    Optional<Medecin> findByMatricule(String matricule);

    /**
     * Recherche des médecins disponibles.
     */
    List<Medecin> findByDisponibleTrue();

    /**
     * Recherche des médecins par département.
     */
    List<Medecin> findByDepartementId(Long departementId);

    /**
     * Recherche des médecins par critères multiples.
     */
    @Query("SELECT m FROM Medecin m WHERE " +
            "(:nom IS NULL OR LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
            "(:prenom IS NULL OR LOWER(m.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))) AND " +
            "(:specialite IS NULL OR m.specialite = :specialite) AND " +
            "(:departementId IS NULL OR m.departement.id = :departementId) AND " +
            "(:disponible IS NULL OR m.disponible = :disponible)")
    Page<Medecin> rechercheMedecins(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("specialite") String specialite,
            @Param("departementId") Long departementId,
            @Param("disponible") Boolean disponible,
            Pageable pageable);
}