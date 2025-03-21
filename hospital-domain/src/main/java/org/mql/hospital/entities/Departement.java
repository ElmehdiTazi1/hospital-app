package org.mql.hospital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Entité représentant un département dans l'hôpital.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departement {

    /**
     * Identifiant unique du département, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du département.
     */
    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du département doit contenir entre 2 et 100 caractères")
    private String nom;

    /**
     * Description du département.
     */
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    /**
     * Localisation du département dans l'hôpital (étage, bâtiment, etc.).
     */
    private String localisation;

    /**
     * Liste des médecins rattachés à ce département.
     */
    @OneToMany(mappedBy = "departement")
    private Set<Medecin> medecins;

    /**
     * Nombre de lits disponibles dans le département.
     */
    private Integer capaciteLits;

    /**
     * Indique si le département est actuellement actif.
     */
    private boolean actif = true;

    /**
     * Chef du département.
     */
    @OneToOne
    @JoinColumn(name = "chef_departement_id")
    private Medecin chefDepartement;
}