package org.mql.hospital.repository;

import org.mql.hospital.entities.Departement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartementRepository extends JpaRepository<Departement, Long> {

    /**
     * Recherche des départements par nom.
     */
    Page<Departement> findByNomContainsIgnoreCase(String nom, Pageable pageable);

    /**
     * Recherche des départements actifs.
     */
    List<Departement> findByActifTrue();

    /**
     * Recherche des départements par chef de département.
     */
    Departement findByChefDepartementId(Long medecinId);

    /**
     * Compte le nombre de médecins par département.
     */
    @Query("SELECT d.id, d.nom, COUNT(m) FROM Departement d LEFT JOIN d.medecins m GROUP BY d.id, d.nom")
    List<Object[]> countMedecinsByDepartement();

    /**
     * Recherche des départements ayant une capacité de lits supérieure ou égale à un seuil.
     */
    List<Departement> findByCapaciteLitsGreaterThanEqual(Integer capaciteMinimale);
}