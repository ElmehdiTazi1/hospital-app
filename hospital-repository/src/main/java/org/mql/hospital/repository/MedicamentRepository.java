package org.mql.hospital.repository;

import org.mql.hospital.entities.Medicament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    /**
     * Recherche des médicaments par nom.
     */
    Page<Medicament> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    /**
     * Recherche des médicaments par DCI.
     */
    List<Medicament> findByDciContainingIgnoreCase(String dci);

    /**
     * Recherche des médicaments par laboratoire.
     */
    List<Medicament> findByLaboratoireIgnoreCase(String laboratoire);

    /**
     * Recherche des médicaments disponibles.
     */
    List<Medicament> findByDisponibleTrue();

    /**
     * Recherche des médicaments dont le stock est inférieur ou égal au seuil d'alerte.
     */
    @Query("SELECT m FROM Medicament m WHERE m.quantiteStock <= m.seuilAlerte")
    List<Medicament> findMedicamentsEnAlerte();

    /**
     * Compte le nombre de médicaments dont le stock est inférieur ou égal au seuil d'alerte.
     */
    @Query("SELECT COUNT(m) FROM Medicament m WHERE m.quantiteStock <= m.seuilAlerte")
    long countByQuantiteStockLessThanEqualSeuilAlerte();

    /**
     * Recherche des médicaments qui vont expirer avant une date donnée.
     */
    List<Medicament> findByDateExpirationBeforeAndQuantiteStockGreaterThan(Date date, Integer quantiteMinimale);

    /**
     * Compte le nombre de médicaments qui vont expirer avant une date donnée.
     */
    long countByDateExpirationBeforeAndQuantiteStockGreaterThan(Date date, Integer quantiteMinimale);

    /**
     * Recherche simple des médicaments.
     * Version simplifiée qui ne contient que la recherche par nom.
     */
    @Query("SELECT m FROM Medicament m WHERE " +
            "(:nom IS NULL OR LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%')))")
    Page<Medicament> rechercheMedicamentsSimple(
            @Param("nom") String nom,
            Pageable pageable);
}